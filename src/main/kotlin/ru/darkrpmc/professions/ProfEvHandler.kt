package ru.darkrpmc.professions

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.PlayerDeathEvent
import ru.darkrpmc.data.config.Settings
import ru.darkrpmc.data.profession
import ru.darkrpmc.events.professions.PlayerChangeProfessionEvent
import ru.darkrpmc.professions.civilian.Citizen

@Suppress("unused")
object ProfEvHandler : Listener {
    @EventHandler
    fun onDeath(event: PlayerDeathEvent) {
        val player = event.player
        var profession = player.profession
        if (profession.info.resetAfterDeath) {
            profession = Citizen(player)
            player.profession = profession
        }
        player.setRespawnLocation(
            if (profession is WithCustomSpawn) profession.spawn
            else Settings.World.spawn, true
        )
    }

    @EventHandler
    fun onProfChange(event: PlayerChangeProfessionEvent) {
        // TODO обработка смены профессии
    }
}