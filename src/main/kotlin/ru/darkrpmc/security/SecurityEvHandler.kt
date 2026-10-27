package ru.darkrpmc.security

import net.kyori.adventure.text.format.NamedTextColor.RED
import org.bukkit.event.*
import org.bukkit.event.player.PlayerLoginEvent
import ru.darkrpmc.*
import ru.darkrpmc.commands.hidden
import ru.darkrpmc.data.accounts
import ru.darkrpmc.data.config.Settings
import ru.darkrpmc.data.database.loadAccount
import ru.darkrpmc.text.colored

@Suppress("unused")
object SecurityEvHandler : Listener {
    @EventHandler(priority = EventPriority.LOWEST)
    fun onLogin(event: PlayerLoginEvent) {
        val player = event.player
        val name = player.name
        val account = loadAccount(name)
        if (account == null) {
            event.disallow(PlayerLoginEvent.Result.KICK_OTHER, "Вы не зарегистрированы в системе".colored(RED))
            return
        }
        accounts[name] = account
        if (player.isOp && event.address.hostName !in Settings.Security.opsIPs)
            event.disallow(PlayerLoginEvent.Result.KICK_OTHER, "Вы не можете зайти под данной уч. записью".colored(RED))
        else if (player in hidden)
            server.onlinePlayers.forEach { if (it !in Permission.SeeHidden) it.hidePlayer(plugin, player) }
    }
}