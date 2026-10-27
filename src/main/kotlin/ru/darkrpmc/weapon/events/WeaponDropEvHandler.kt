package ru.darkrpmc.weapon.events

import org.bukkit.entity.Player
import org.bukkit.event.*
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.CrossbowMeta
import ru.darkrpmc.data.persistent.FirearmDataType
import ru.darkrpmc.firearmKey
import ru.darkrpmc.weapon.*
import ru.darkrpmc.weapon.events.RechargingEvHandler.hasAmmo

object WeaponDropEvHandler: Listener {
    @EventHandler(priority = EventPriority.LOW)
    fun onDropItem(event: PlayerDropItemEvent) {
        val item = event.itemDrop.itemStack
        val meta = item.itemMeta

        // Если предмет - арбалет с данными оружия
        if (meta is CrossbowMeta && meta.persistentDataContainer.has(firearmKey))
            handleFirearmDrop(event.player, item, meta, event)
        // Если предмет - не оружие, проверить состояние перезарядки
        else handleNonFirearmDrop(event.player)
    }

    private fun handleFirearmDrop(player: Player, item: ItemStack, meta: CrossbowMeta, event: PlayerDropItemEvent) {
        if (meta.hasChargedProjectiles()) {
            meta.setChargedProjectiles(emptyList())
            item.setItemMeta(meta)
            event.isCancelled = true
        } else player.returnItem()

        val recharge = recharges.remove(player.name) ?: return
        if (item.isTheSameWeapon(recharge.first)) {
            recharge.second.cancel()
            player.setCooldown(recharge.first, 0)
            event.isCancelled = true
        }
    }

    private fun handleNonFirearmDrop(player: Player) {
        val recharge = recharges[player.name] ?: return
        val ammoType =
            recharge.first.itemMeta.persistentDataContainer.get(firearmKey, FirearmDataType)?.ammoType ?: return
        if (hasAmmo(player.inventory, ammoType)) {
            recharge.second.cancel()
            player.setCooldown(recharge.first, 0)
        }
    }
}