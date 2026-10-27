package ru.darkrpmc

import net.kyori.adventure.text.format.NamedTextColor.DARK_GRAY
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority.MONITOR
import org.bukkit.event.Listener
import org.bukkit.event.player.*
import ru.darkrpmc.commands.hidden
import ru.darkrpmc.data.*
import ru.darkrpmc.data.database.*
import ru.darkrpmc.professions.ProfessionInfo
import ru.darkrpmc.text.colored
import java.time.LocalDateTime

@Suppress("unused")
object MainEvHandler : Listener {
    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        event.joinMessage(null)
        val player = event.player
        val name = player.name

        perServerData[name] = loadPerServerData(name) ?: PerServerData()
        uptimeData.putIfAbsent(name, UptimeData())
        sessionData[name] = InSessionData(player)

        player.tpToSpawnAsync()
        player.updateTabName()
        player.setSkin(ProfessionInfo.CITIZEN.skin, ProfessionInfo.CITIZEN.skinModel)
        player.updateGUI()
        player.sendPlayerListHeader("DarkRPMC".colored(DARK_GRAY))
        RPTeam.addPlayer(player)

        if (player !in Permission.SeeHidden) hidden.forEach { player.hidePlayer(plugin, it) }
    }

    @EventHandler(priority = MONITOR)
    fun onPlayerLeave(event: PlayerQuitEvent) {
        event.quitMessage(null)
        RPTeam.removePlayer(event.player)
        nonRPTeam.removePlayer(event.player)
        if (stop) return

        val name = event.player.name
        sessionData -= name
        perServerData.remove(name)?.let {
            it.lastSeen = LocalDateTime.now()
            savePerServerData(name, it)
        }
        accounts.remove(name)?.let { saveAccount(name, it) }
    }

    @EventHandler(priority = MONITOR, ignoreCancelled = true) // Для команды /speed
    fun onToggleFlight(event: PlayerToggleFlightEvent) = event.player.updateCommands()
}