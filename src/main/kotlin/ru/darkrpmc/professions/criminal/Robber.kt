package ru.darkrpmc.professions.criminal

import net.kyori.adventure.text.format.NamedTextColor.GOLD
import org.bukkit.Material
import org.bukkit.entity.Player
import ru.darkrpmc.inventory.item
import ru.darkrpmc.professions.ProfessionInfo
import ru.darkrpmc.professions.ProfessionSource
import ru.darkrpmc.text.colored

class Robber(player: Player, source: ProfessionSource = ProfessionSource.SYSTEM) : Raider(
    ProfessionInfo.ROBBER, source, player
) {
    override val shopItems = super.shopItems + (item(Material.NAUTILUS_SHELL, "Взломщик".colored(GOLD)) to 250)
}