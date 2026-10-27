package ru.darkrpmc.inventory.menu

import net.kyori.adventure.text.format.NamedTextColor.*
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.ClickType.LEFT
import org.bukkit.event.inventory.ClickType.RIGHT
import org.bukkit.inventory.ItemStack
import ru.darkrpmc.data.config.Settings
import ru.darkrpmc.data.profession
import ru.darkrpmc.data.session
import ru.darkrpmc.inventory.distributeSymmetrically
import ru.darkrpmc.inventory.item
import ru.darkrpmc.plugin
import ru.darkrpmc.professions.NonRP
import ru.darkrpmc.professions.LawEnforcer
import ru.darkrpmc.professions.criminal.Raider
import ru.darkrpmc.professions.enforcers.*
import ru.darkrpmc.text.colored

class CuffMenu(val player: Player, private val cuffed: Player) : Menu(
    1, ("Человек в " + if (cuffs[cuffed.name] == CuffType.HANDCUFFS) "наручниках" else "стяжках").colored(GRAY)
) {
    init {
        val buttons = arrayListOf<ItemStack>()

        if (cuffs[cuffed.name] != CuffType.HANDCUFFS || player.profession is LawEnforcer) buttons += releaseButton

        // @formatter:off
        if (
            (player.profession is Raider || player.profession is NonRP) &&
            (player.session.lastRobbed + Settings.PlayerInteract.robberyCooldown < System.currentTimeMillis())
        ) buttons += stealingButton
        // @formatter:on

        if (player.profession is LawEnforcer || player.profession is NonRP) {
            buttons += searchButton
            buttons += arrestButton
        }
        distributeSymmetrically(buttons).forEach(inv::setItem)
        player.openInventory(inv)
    }

    override fun handleClick(slot: Int, clickType: ClickType) {
        if (clickType != LEFT && clickType != RIGHT) return

        when (inv.getItem(slot)) {
            stealingButton -> {
                // TODO Реализовать логику кражи
                player.sendMessage("Попытка ограбления игрока.".colored(YELLOW))
            }

            releaseButton -> {
                release(player, cuffed)
                player.closeInventory()
            }

            searchButton -> {
                // TODO Реализовать логику обыска
                player.sendMessage("Вы обыскали игрока.".colored(GREEN))
            }

            arrestButton -> {
                // TODO Реализовать логику ареста
                player.sendMessage("Вы арестовали игрока.".colored(GREEN))
            }
        }
    }
}

// @formatter:off
private val releaseButton = item(
    Material.LIME_STAINED_GLASS_PANE, "Освободить".colored(GREEN),
    lore = listOf("Снять наручники/стяжки с игрока".colored(GRAY))
)

private val stealingButton = item(
    Material.YELLOW_STAINED_GLASS_PANE, "Обокрасть".colored(YELLOW),
    lore = listOf("Попытаться украсть деньги у игрока".colored(GRAY))
)

private val searchButton = item(
    Material.CLAY_BALL, "Обыскать".colored(GREEN), 1, NamespacedKey(plugin, "buttons/search"),
    lore = listOf("Проверить вещи игрока".colored(GRAY))
)

private val arrestButton = item(
    Material.CLAY_BALL, "Арестовать".colored(GREEN), 1, NamespacedKey(plugin, "buttons/arrest"),
    lore = listOf("Арестовать игрока".colored(GRAY))
)