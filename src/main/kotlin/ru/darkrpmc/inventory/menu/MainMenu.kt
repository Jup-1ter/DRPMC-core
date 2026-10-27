package ru.darkrpmc.inventory.menu

import net.kyori.adventure.text.format.NamedTextColor.DARK_AQUA
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.ClickType.LEFT
import org.bukkit.event.inventory.ClickType.RIGHT
import ru.darkrpmc.Permission
import ru.darkrpmc.inventory.item
import ru.darkrpmc.plugin
import ru.darkrpmc.professions.ProfessionInfo
import ru.darkrpmc.text.colored

class MainMenu(val player: Player) : Menu(2, "Меню".colored(DARK_AQUA)) {
    override fun handleClick(slot: Int, clickType: ClickType) {
        if (clickType != RIGHT && clickType != LEFT) return
        when (slot) {
            0 -> ProfMenu(player)
            1 -> ShopMenu(player)
            16 -> ProfessionInfo.NONRP.run { pickProtocol(player) }
        }
    }

    init {
        inventory.setItem(0, profButton)
        inventory.setItem(1, shopButton)
        if (player in Permission.NonRPProfession) inventory.setItem(16, adminButton)
        player.openInventory(inv)
    }
}

private val profButton =
    item(Material.CLAY_BALL, "Профессии".colored(DARK_AQUA), 1, NamespacedKey(plugin, "buttons/professions"))
private val shopButton =
    item(Material.CLAY_BALL, "Магазин".colored(DARK_AQUA), 1, NamespacedKey(plugin, "buttons/shop"))
private val adminButton =
    item(Material.CLAY_BALL, "Админ-режим".colored(DARK_AQUA), 1,  NamespacedKey(plugin, "buttons/professions"))