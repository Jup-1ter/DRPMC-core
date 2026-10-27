package ru.darkrpmc.weapon.events

import io.papermc.paper.event.player.PlayerInventorySlotChangeEvent
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.player.PlayerItemHeldEvent
import org.bukkit.inventory.meta.CrossbowMeta
import ru.darkrpmc.firearmKey
import ru.darkrpmc.weapon.*

object InvWeaponEvHandler: Listener {

    @EventHandler
    fun onInventoryClick(event: InventoryClickEvent) {
        val player = event.whoClicked as? Player ?: return
        val selectedSlot = player.inventory.heldItemSlot
        if (event.hotbarButton == selectedSlot || event.slot == selectedSlot) player.inventory.itemInMainHand
            .editMeta(CrossbowMeta::class.java) {
                it.setChargedProjectiles(emptyList())
            }
    }

    @EventHandler
    fun onSlotChange(event: PlayerInventorySlotChangeEvent) {
        val player = event.player
        if (event.slot != player.inventory.heldItemSlot) return

        val newMeta = event.newItemStack.itemMeta
        val oldState = event.oldItemStack.state
        val newPDC = newMeta?.persistentDataContainer

        when {
            newMeta == null || newPDC?.state == null -> if (oldState != null) handleRechargeCancellation(player)
            oldState != null && oldState.serialNumber != newPDC.state?.serialNumber -> {
                handleRechargeCancellation(player)
                newPDC.firearm?.let {
                    player.setCooldown(event.newItemStack, (it.deploy * 20).toInt())
                }
            }
            oldState == null -> {
                newPDC.firearm?.let {
                    player.setCooldown(event.newItemStack, (it.deploy * 20).toInt())
                }
            }
        }
    }

    @EventHandler
    fun onHeldItemChange(event: PlayerItemHeldEvent) {
        val player = event.player

        // Завершаем перезарядку, если она есть
        recharges.remove(player.name)?.second?.cancel()

        // Обработка предыдущего предмета
        event.player.inventory.getItem(event.previousSlot)?.let { prevItem ->
            val previousMeta = prevItem.itemMeta as? CrossbowMeta ?: return@let
            if (previousMeta.persistentDataContainer.has(firearmKey)) {
                if (previousMeta.hasChargedProjectiles()) {
                    previousMeta.setChargedProjectiles(emptyList())
                    prevItem.itemMeta = previousMeta
                }
                player.returnItem()
                player.setCooldown(prevItem, 0)
            }
        }

        // Обработка нового предмета
        val newItem = event.player.inventory.getItem(event.newSlot) ?: return
        player.setCooldown(newItem, ((newItem.firearm ?: return).deploy * 20).toInt())
    }

    private fun handleRechargeCancellation(player: Player) {
        val recharge = recharges.remove(player.name) ?: return
        recharge.second.cancel()
        player.setCooldown(recharge.first, 0)
    }
}