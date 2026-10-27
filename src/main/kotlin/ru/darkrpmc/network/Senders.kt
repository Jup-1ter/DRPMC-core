package ru.darkrpmc.network

import org.bukkit.entity.Player
import ru.darkrpmc.plugin

lateinit var professionsBytes: ByteArray

fun Player.requestClientInfo(timeout: Long) {
    sendPluginMessage(plugin, "client_info", ByteArray(0))
    if (timeout > 0) TODO("Автокик по истечение времени")
}

fun Player.sendBlockList() {
    sendPluginMessage(plugin, "block_list", ByteArray(0)) // TODO отправлять список блоков
}

fun Player.sendProfessions() {
    sendPluginMessage(plugin, "profession_list", professionsBytes)
}