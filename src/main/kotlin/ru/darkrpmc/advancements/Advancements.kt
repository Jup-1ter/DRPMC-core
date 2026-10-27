package ru.darkrpmc.advancements

import org.bukkit.NamespacedKey
import ru.darkrpmc.plugin
import ru.darkrpmc.server

val rootAdv = server.getAdvancement(NamespacedKey(plugin, "root"))!!
val invAdv = server.getAdvancement(NamespacedKey(plugin, "game/inventory_advancement0000"))!!