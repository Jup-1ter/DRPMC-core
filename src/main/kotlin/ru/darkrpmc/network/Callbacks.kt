@file:Suppress("UNUSED_PARAMETER")

package ru.darkrpmc.network

import org.bukkit.entity.Player
import ru.darkrpmc.security.validateModVersion

fun handshake(channel: String, sender: Player, data: ByteArray) {
    if (sender.validateModVersion(String(data))) {
        sender.requestClientInfo(0)
        sender.sendBlockList()
        sender.sendProfessions()
    }
}

fun blockList(channel: String, sender: Player, data: ByteArray) {
    sender.sendBlockList()
}

fun blockBuy(channel: String, sender: Player, data: ByteArray) {
    // TODO: сделать покупку блоков
}