package ru.darkrpmc.professions.criminal

import net.kyori.adventure.text.format.NamedTextColor.GOLD
import net.kyori.adventure.text.format.NamedTextColor.RED
import org.bukkit.Material
import org.bukkit.entity.Player
import ru.darkrpmc.data.donateGroupOrUser
import ru.darkrpmc.inventory.item
import ru.darkrpmc.professions.*
import ru.darkrpmc.text.colored

abstract class Raider(
    info: ProfessionInfo, source: ProfessionSource, player: Player
) : Criminal(info, source, player) {
    override val shopItems = super.shopItems + (player.donateGroupOrUser.printerType.item to 300)
}

abstract class Bandit(
    info: ProfessionInfo, source: ProfessionSource, player: Player
) : Raider(info, source, player) {
    override val shopItems = super.shopItems + listOf(
        item(Material.NAUTILUS_SHELL, "Взломщик".colored(GOLD)) to 200,
        item(Material.LEATHER, "Стяжки".colored(RED)) to 200
    )
}