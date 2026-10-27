package ru.darkrpmc.inventory

import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority.HIGHEST
import org.bukkit.event.EventPriority.LOWEST
import org.bukkit.event.Listener
import org.bukkit.event.block.Action.LEFT_CLICK_AIR
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.inventory.*
import org.bukkit.event.inventory.ClickType.*
import org.bukkit.event.inventory.InventoryAction.MOVE_TO_OTHER_INVENTORY
import org.bukkit.event.player.*
import org.bukkit.scheduler.BukkitTask
import ru.darkrpmc.advancements.invAdv
import ru.darkrpmc.data.persistent.*
import ru.darkrpmc.data.serverData
import ru.darkrpmc.inventory.menu.Menu
import ru.darkrpmc.plugin
import ru.darkrpmc.professions.enforcers.cuffs
import ru.darkrpmc.professions.enforcers.stunned
import ru.darkrpmc.scheduler

object InvEvHandler : Listener {
    @EventHandler(priority = HIGHEST, ignoreCancelled = true)
    fun onPlayerJoin(event: PlayerJoinEvent) = event.player.updateInvBlock()

    @EventHandler(priority = LOWEST, ignoreCancelled = true)
    fun onInvClick(event: InventoryClickEvent) {
        val player = event.whoClicked
        if (event.click == DROP || event.click == CONTROL_DROP) dropPlayers.put(
            player.name, scheduler.runTask(plugin) { -> dropPlayers -= player.name })?.cancel()

        when (val clickedHolder = event.clickedInventory?.holder) {
            is Menu -> {
                event.isCancelled = true
                clickedHolder.handleClick(event.slot, event.click)
                return
            }

            is Backpack -> scheduler.runTask(plugin) { -> clickedHolder.loadBuffer() }
            is Vault -> scheduler.runTask(plugin) { -> clickedHolder.loadBuffer() }
        }

        val curItemMeta = event.currentItem?.itemMeta
        val flags = curItemMeta.flags
        if (flags.immobile) event.isCancelled = true
        when (event.click) {
            RIGHT, LEFT, CREATIVE -> if (curItemMeta != null) {
                InvActionDataType.read(curItemMeta.persistentDataContainer)?.action?.invoke(event)
            }

            NUMBER_KEY -> if (player.inventory.getItem(event.hotbarButton)?.flags?.immobile == true) event.isCancelled =
                true

            else -> {}
        }
        // @formatter:off
        if (event.action == MOVE_TO_OTHER_INVENTORY && (flags.invRestricted || event.inventory.holder is Menu))
            event.isCancelled = true
        // @formatter:on
    }

    @EventHandler(priority = LOWEST, ignoreCancelled = true)
    fun onInvDrag(event: InventoryDragEvent) {
        val inv = event.inventory
        if (inv.holder is Menu && event.rawSlots.any { it < inv.size }) event.isCancelled = true
    }

    @EventHandler
    fun onBlockInteract(event: PlayerInteractEvent) {
        if (event.clickedBlock?.type == Material.ENDER_CHEST) {
            event.isCancelled = true
            Vault(event.player)
            return
        }
        val action =
            BlockInteractActionDataType.read((event.item?.itemMeta ?: return).persistentDataContainer)?.action ?: return
        event.isCancelled = true
        if (event.action == LEFT_CLICK_AIR) scheduler.runTask(plugin) { ->
            if (event.player.name !in dropPlayers) action(event)
        } else action(event)
    }

    @EventHandler
    fun onEntityInteract(event: PlayerInteractEntityEvent) {
        val action = EntityInteractActionDataType.read(
            (event.player.inventory.itemInMainHand.itemMeta ?: return).persistentDataContainer
        )?.action ?: return
        event.isCancelled = true
        action(event)
    }

    @EventHandler(priority = LOWEST, ignoreCancelled = true)
    fun onDamage(event: EntityDamageByEntityEvent) {
        val damager = event.damager as? Player ?: return
        if (damager.name in cuffs || damager.name in stunned) event.isCancelled = true

        if (AttackActionDataType.read(
                (damager.inventory.itemInMainHand.itemMeta ?: return).persistentDataContainer
            )?.action?.invoke(event) == true
        ) event.isCancelled = true
    }

    private val dropPlayers = hashMapOf<String, BukkitTask>()

    @EventHandler
    fun onItemDrop(event: PlayerDropItemEvent) {
        val name = event.player.name
        dropPlayers.put(name, scheduler.runTaskLater(plugin, { -> dropPlayers -= name }, 1))?.cancel()
        if (event.itemDrop.itemStack.flags.invRestricted) event.isCancelled = true
    }

    @EventHandler
    fun onHandSwap(event: PlayerSwapHandItemsEvent) {
        if (event.mainHandItem.flags.immobile || event.offHandItem.flags.immobile) event.isCancelled = true
    }

    @EventHandler
    fun onJoin(event: PlayerJoinEvent) = event.player.inventory.setBasicItems()

    @EventHandler
    fun onAdvancement(event: PlayerAdvancementDoneEvent) {
        if (event.advancement.displayName() == invAdv.displayName()) event.player.updateInvBlock()
    }

    @EventHandler
    fun onInvClose(event: InventoryCloseEvent) {
        if (event.inventory.holder is Backpack) (event.player as? Player)?.serverData?.backpack = HashMap(
            event.inventory.contents.mapIndexedNotNull { i, item ->
                if (item != null && item.type != Material.BARRIER) i to item else null
            }.toMap()
        )
    }
}