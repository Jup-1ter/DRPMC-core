package ru.darkrpmc.events.world.regions

import org.bukkit.event.Cancellable
import org.bukkit.event.HandlerList
import org.bukkit.event.player.PlayerMoveEvent
import ru.darkrpmc.world.regions.Region

private val HANDLER_LIST: HandlerList = HandlerList()

class PlayerRegionLeaveEvent(
    region: Region,
    move: PlayerMoveEvent
) : PlayerRegionEvent(move.player, region, move), Cancellable {

    companion object {
        @Suppress("unused") @JvmStatic
        fun getHandlerList() = HANDLER_LIST
    }

    override fun getHandlers() = HANDLER_LIST

    private var cancel = false

    override fun isCancelled() = cancel

    override fun setCancelled(cancel: Boolean) {
        this.cancel = cancel
    }
}