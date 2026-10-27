package ru.darkrpmc.logs

import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.player.PlayerJoinEvent
import ru.darkrpmc.ip
import ru.darkrpmc.text.logRepresentation

@Suppress("unused")
object LogEvHandler : Listener {
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onJoin(event: PlayerJoinEvent) {
        val player = event.player
        val ip = player.ip
        sendLog(LogCategory.CONNECTION, "Игрок ${player.name} [$ip] присоединился к серверу", player)
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onDamageByPlayer(event: EntityDamageByEntityEvent) {
        val damaged = event.entity
        val damager = event.damager
        if (damaged !is Player || damager !is Player) return
        @Suppress("UnusedVariable") // TODO убрать
        val damagedName = damaged.name
        @Suppress("UnusedVariable") // TODO убрать
        val damagerName = damager.name

        sendLog(
            LogCategory.DAMAGE,
            "Игрок ${damager.logRepresentation} нанёс урон игроку ${damaged.logRepresentation} в размере ${event.finalDamage} ед. при помощи ${damager.activeItem.logRepresentation}",
            damager, damaged
        )
    }
}