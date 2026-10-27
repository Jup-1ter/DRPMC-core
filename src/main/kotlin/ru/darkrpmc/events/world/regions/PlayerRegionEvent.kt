package ru.darkrpmc.events.world.regions

import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerEvent
import org.bukkit.event.player.PlayerMoveEvent
import ru.darkrpmc.world.regions.Region

abstract class PlayerRegionEvent(player: Player, val region: Region, val move: PlayerMoveEvent) : PlayerEvent(player)