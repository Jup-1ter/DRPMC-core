@file:Suppress("UnstableApiUsage")

package ru.darkrpmc

import io.papermc.paper.plugin.lifecycle.event.LifecycleEventManager
import org.bukkit.Bukkit
import org.bukkit.plugin.Plugin
import java.util.logging.Logger

/** Экземпляр плагина */
lateinit var plugin: Core

/** Экземпляр логера */
lateinit var logger: Logger

lateinit var lifecycleManager: LifecycleEventManager<Plugin>

/** Экземпляр сервера */
val server = Bukkit.getServer()

/** Экземпляр планировщика */
val scheduler = server.scheduler

/** Экземпляр менеджера скорбордов */
val scoreboardManager = server.scoreboardManager

/** Экземпляр основного скорборда */
val mainScoreboard = server.scoreboardManager.mainScoreboard

/** Основной мир */
val world = server.getWorld("world")!!

fun setupVars(pl: Core) {
    plugin = pl
    logger = pl.logger
    lifecycleManager = pl.lifecycleManager
}