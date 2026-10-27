package ru.darkrpmc.weapon.events

import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerSwapHandItemsEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.PlayerInventory
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.persistence.PersistentDataContainer
import ru.darkrpmc.data.persistent.FirearmDataType
import ru.darkrpmc.data.persistent.FirearmStateDataType
import ru.darkrpmc.plugin
import ru.darkrpmc.scheduler
import ru.darkrpmc.weapon.*
import kotlin.math.min

object RechargingEvHandler: Listener {
    @EventHandler
    fun onSwapHands(event: PlayerSwapHandItemsEvent) {
        val item = event.offHandItem
        val meta = item.itemMeta ?: return

        val pdc = meta.persistentDataContainer
        val firearm = FirearmDataType.read(pdc) ?: return
        val state = FirearmStateDataType.read(pdc) ?: return

        // Перемещение предметов между руками
        event.setOffHandItem(event.mainHandItem)
        event.setMainHandItem(item)

        // Проверка на максимальное количество патронов
        if (state.ammo >= firearm.magazine) return

        val player = event.player
        val inventory = player.inventory

        // Проверка наличия нужных боеприпасов
        if (!hasAmmo(inventory, firearm.ammoType)) return

        // Проверка перезарядки
        if (player.hasCooldown(item)) return

        startReloading(player, item, meta, pdc, state, firearm)
    }

    fun hasAmmo(inventory: PlayerInventory, ammoType: AmmoType) =
        inventory.any { it != null && ammoType == AmmoType.getFor(it) }

    private fun startReloading(
        player: Player,
        item: ItemStack,
        meta: ItemMeta,
        pdc: PersistentDataContainer,
        state: FirearmState,
        firearm: Firearm
    ) {
        player.returnItem()
        player.setCooldown(item, (firearm.recharge * 20).toInt())

        recharges[player.name] = item to scheduler.runTaskLater(plugin, { ->
            val curItem = player.inventory.itemInMainHand
            if (!curItem.checkSerialNumber(state.serialNumber)) return@runTaskLater

            reloadAmmo(player.inventory, state, firearm)
            FirearmStateDataType.write(pdc, state)

            meta.itemName(fullName(firearm.displayName, state.ammo))
            curItem.itemMeta = meta
        }, (firearm.recharge * 20).toLong())
    }

    private fun reloadAmmo(inventory: PlayerInventory, state: FirearmState, firearm: Firearm) {
        for (slot in inventory) {
            if (slot == null || firearm.ammoType != AmmoType.getFor(slot)) continue

            if (firearm.singleRecharging) {
                slot.amount--
                state.ammo++
                break
            }

            val transferAmount = min(slot.amount, firearm.magazine - state.ammo)
            slot.amount -= transferAmount
            state.ammo = (state.ammo + transferAmount).toShort()

            if (state.ammo == firearm.magazine) break
        }
    }
}