package ru.darkrpmc.professions

import net.kyori.adventure.text.format.NamedTextColor.*
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import ru.darkrpmc.inventory.item
import ru.darkrpmc.text.colored

enum class ProfessionCategory(val item: ItemStack) {
    Government(item(Material.LIGHT_BLUE_DYE, "Правительство".colored(AQUA))),
    LawEnforcement(item(Material.BLUE_DYE, "Силовые структуры".colored(BLUE))),
    Army(item(Material.GREEN_DYE, "Армия".colored(DARK_GREEN))),
    Civilian(item(Material.LIGHT_GRAY_DYE, "Гражданские".colored(GRAY))),
    Criminal(item(Material.RED_DYE, "Криминал".colored(DARK_RED)));
}