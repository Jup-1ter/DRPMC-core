package ru.darkrpmc.events.professions

import org.bukkit.entity.Player
import org.bukkit.event.HandlerList
import org.bukkit.event.player.PlayerEvent
import ru.darkrpmc.professions.*

private val HANDLER_LIST: HandlerList = HandlerList()

class PlayerChangeProfessionEvent(
    player: Player,
    val old: Profession,
    val new: ProfessionInfo,
    val source: ProfessionSource
) : PlayerEvent(player) {
    companion object {
        @Suppress("unused")
        @JvmStatic
        fun getHandlerList() = HANDLER_LIST
    }

    override fun getHandlers() = HANDLER_LIST
}