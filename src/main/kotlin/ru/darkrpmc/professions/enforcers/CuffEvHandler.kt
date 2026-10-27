package ru.darkrpmc.professions.enforcers

import com.destroystokyo.paper.event.player.PlayerJumpEvent
import org.bukkit.entity.Player
import org.bukkit.event.*
import org.bukkit.event.block.*
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.*
import ru.darkrpmc.inventory.menu.CuffMenu
import kotlin.math.min

object CuffEvHandler : Listener {
    @EventHandler
    fun onDisconnect(event: PlayerQuitEvent) {
        val name = event.player.name
        cuffs -= name
        stunned -= name
    }

    @EventHandler
    fun onDeath(event: PlayerDeathEvent) {
        val name = event.player.name
        cuffs -= name
        stunned -= name
    }

    // Не даёт скованному/оглушённому взаимодействовать с блоками
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    fun onDamageBlock(event: BlockDamageEvent) {
        val name = event.player.name
        if (name in cuffs || name in stunned) event.isCancelled = true
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    fun onBlockBreak(event: BlockBreakEvent) {
        val name = event.player.name
        if (name in cuffs || name in stunned) event.isCancelled = true
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    fun onBlockPlace(event: BlockPlaceEvent) {
        val name = event.player.name
        if (name in cuffs || name in stunned) event.isCancelled = true
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    fun onMove(event: PlayerMoveEvent) {
        val from = event.from
        if (event.player.name in stunned) { // Позволяет двигаться только вниз
            from.y = min(from.y, event.to.y)
            event.to = from
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    fun onJump(event: PlayerJumpEvent) {
        if (event.player.name in cuffs) event.isCancelled = true
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    fun onInteract(event: PlayerInteractEntityEvent) {
        val name = event.player.name
        if (name in cuffs || name in stunned) {
            event.isCancelled = true
            return
        }
        val clickedAt = event.rightClicked as? Player ?: return
        if (clickedAt.name in cuffs) CuffMenu(event.player, clickedAt)
    }
}