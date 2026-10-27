package ru.darkrpmc.weapon.events

import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority.MONITOR
import org.bukkit.event.Listener
import org.bukkit.event.block.Action.RIGHT_CLICK_AIR
import org.bukkit.event.block.Action.RIGHT_CLICK_BLOCK
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.player.*
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.meta.CrossbowMeta
import ru.darkrpmc.*
import ru.darkrpmc.data.persistent.FirearmDataType
import ru.darkrpmc.data.persistent.FirearmStateDataType
import ru.darkrpmc.professions.enforcers.*
import ru.darkrpmc.weapon.*

private val weaponFunctions = mapOf(
    "stun" to f@{ event: EntityDamageByEntityEvent ->
        stun(event.entity as? Player ?: return@f)
    }
)

object ShootingEvHandler : Listener {

    @EventHandler
    fun onRMB(event: PlayerInteractEvent) {
        val action = event.action
        // Только для ПКМ основной рукой
        if (event.hand != EquipmentSlot.HAND || action != RIGHT_CLICK_BLOCK && action != RIGHT_CLICK_AIR) return

        val player = event.player
        val name = player.name

        // Только для свободных игроков
        if (name in cuffs || name in stunned) return

        val item = event.item ?: return
        val meta = item.itemMeta

        // Только для арбалетов
        if (meta !is CrossbowMeta) return

        val pdc = meta.persistentDataContainer

        // Только для арбалетов с данными оружия
        val firearm = FirearmDataType.read(pdc) ?: return
        val state = FirearmStateDataType.read(pdc) ?: return

        event.isCancelled = true

        // Время нажатия пкм
        val rmbClick = System.currentTimeMillis()

        // Если оружие не заблокировано (достаётся / перезаряжается)
        if (player.hasCooldown(item)) return

        // if ((weaponBlockEnd[name] ?: 0) > rmbClick) return
        // Интервал в мс между выстрелами
        val interval = 1000.0 * 60 / firearm.fireRate

        if (meta.hasChargedProjectiles()) { // Если арбалет заряжён (оружие взведено)
            lastRMBs[name] = rmbClick

            if (name in automaticTasks) return

            if (player.isUnderWater || state.ammo < 1) {
                world.playSound(player.eyeLocation, Sound.BLOCK_DISPENSER_FAIL, 1f, 1.5f)
                return
            }

            // Время с последнего выстрела
            val sinceLastShot = rmbClick - state.lastShot

            // Функция для сохранения данных оружия
            fun save() {
                FirearmStateDataType.write(pdc, state)
                meta.itemName(fullName(firearm.displayName, state.ammo))
                item.itemMeta = meta
            }

            // Выстрелить, если с последнего выстрела до нажатия прошло достаточно времени
            if (sinceLastShot >= interval) {
                player.shoot(firearm, state)
                save()
            }

            // Если между нажатием и последним выстрелом прошло достаточно мало времени считать, что это автоматика
            if (sinceLastShot < THRESHOLD) automaticTasks[name] = scheduler.runTaskTimer(plugin, { ->
                val now = System.currentTimeMillis()
                if (now - lastRMBs[name]!! < THRESHOLD) { // Если с момента клика прошло времени < порога
                    // Если с последнего выстрела до нажатия прошло достаточно времени
                    if (now - state.lastShot >= interval) {
                        // Если предмет остался тем же и есть патроны в магазине
                        if (player.inventory.itemInMainHand.isTheSameWeapon(item) && state.ammo > 0) {
                            // Выстрелить
                            player.shoot(firearm, state)
                            save()
                        } else automaticTasks.remove(name)?.cancel()
                    }
                } else automaticTasks.remove(name)?.cancel()
            }, 0, 1)
        } else {
            meta.addChargedProjectile(projectile)
            item.itemMeta = meta
        }
    }

    @EventHandler(priority = MONITOR)
    fun onPlayerLeave(event: PlayerQuitEvent) {
        val name = event.player.name
        automaticTasks.remove(name)?.cancel()
        lastRMBs -= name
        recharges.remove(name)?.second?.cancel()
        event.player.returnItem()
    }

    @EventHandler
    fun onSneak(event: PlayerToggleSneakEvent) {
        val player = event.player
        val inv = player.inventory
        val firearm = inv.itemInMainHand.firearm ?: return
        if (!firearm.scope) return
        if (event.isSneaking) {
            val offhand = inv.itemInOffHand
            if (!offhand.isEmpty && buffer.put(player.name, offhand) != null)
                logger.warning("Буфер игрока ${player.name} перезаписан")
            inv.setItemInOffHand(scopeItem)
        } else player.returnItem()
    }

    @EventHandler
    fun onDamage(event: EntityDamageByEntityEvent) {
        weaponFunctions[(event.damager as? Player)?.inventory?.itemInMainHand?.firearm?.function ?: return]
            ?.invoke(event)
    }
}