package ru.darkrpmc

import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents
import org.bukkit.entity.Player
import org.bukkit.event.Listener
import org.bukkit.plugin.PluginManager
import org.bukkit.plugin.java.JavaPlugin
import ru.darkrpmc.commands.Commands
import ru.darkrpmc.commands.runRegHighlighter
import ru.darkrpmc.data.config.loadConfig
import ru.darkrpmc.data.database.*
import ru.darkrpmc.inventory.InvEvHandler
import ru.darkrpmc.logs.LogEvHandler
import ru.darkrpmc.network.doNetworkPrep
import ru.darkrpmc.professions.ProfEvHandler
import ru.darkrpmc.professions.enforcers.CuffEvHandler
import ru.darkrpmc.professions.enforcers.startEffectCycle
import ru.darkrpmc.professions.startSalaryTimer
import ru.darkrpmc.security.SecurityEvHandler
import ru.darkrpmc.weapon.events.*
import ru.darkrpmc.world.WorldEvHandler

class Core : JavaPlugin() {

    override fun onEnable() {
        setupVars(this) // Установка "статических" значений
        world.isAutoSave = false
        saveDefaultConfig()
        setupDB()
        loadConfig()
        regEvHandlers()
        doNetworkPrep()
        startSalaryTimer()
        regCommands()
        startInfoDisplayTimer()
        startEffectCycle()
    }

    override fun onDisable() {
        server.onlinePlayers.forEach(Player::clearBlocks)
        if (!stop) savePlayersData()
    }

    private fun regEvHandlers() = server.pluginManager.regEvHandlers(
        MainEvHandler, LogEvHandler, SecurityEvHandler, CuffEvHandler, WeaponDropEvHandler, InvWeaponEvHandler,
        ProfEvHandler, InvEvHandler, WorldEvHandler, ShootingEvHandler, RechargingEvHandler
    )

    private fun PluginManager.regEvHandlers(vararg handlers: Listener) =
        handlers.forEach { registerEvents(it, this@Core) }

    @Suppress("UnstableApiUsage")
    private fun regCommands() = lifecycleManager.registerEventHandler(LifecycleEvents.COMMANDS) { e ->
        Commands.entries.forEach { it.register(e.registrar()) }
        runRegHighlighter()
    }

    private fun startInfoDisplayTimer() =
        scheduler.runTaskTimer(this, { -> server.onlinePlayers.forEach(Player::updateGUI) }, 20, 20)
}