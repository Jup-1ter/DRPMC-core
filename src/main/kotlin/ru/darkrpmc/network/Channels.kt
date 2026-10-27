package ru.darkrpmc.network

import org.bukkit.plugin.messaging.PluginMessageListener
import ru.darkrpmc.plugin
import ru.darkrpmc.server

val messenger = server.messenger

fun registerChannels() {
    regOut(
        "client_info",
        "profession_list",
        "profession_change",
        "profession_slots",
        "block_list"
    )
    regOut("BungeeCord", prefix = "")

    regIn("handshake", ::handshake)
    regIn("block_list", ::blockList)
    regIn("block_buy", ::blockBuy)
}

private fun regOut(vararg channels: String, prefix: String = "drpmc:") =
    channels.forEach { messenger.registerOutgoingPluginChannel(plugin, prefix + it) }

private fun regIn(channel: String, listener: PluginMessageListener, prefix: String = "drpmc:") =
    messenger.registerIncomingPluginChannel(plugin, prefix + channel, listener)