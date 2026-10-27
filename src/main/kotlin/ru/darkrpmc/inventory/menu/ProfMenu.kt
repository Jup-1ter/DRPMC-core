package ru.darkrpmc.inventory.menu

import net.kyori.adventure.text.format.NamedTextColor.DARK_AQUA
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.ClickType.LEFT
import org.bukkit.event.inventory.ClickType.RIGHT
import ru.darkrpmc.data.config.Settings
import ru.darkrpmc.data.profession
import ru.darkrpmc.data.uptime
import ru.darkrpmc.plugin
import ru.darkrpmc.professions.ProfessionCategory
import ru.darkrpmc.professions.ProfessionCategory.*
import ru.darkrpmc.professions.ProfessionInfo
import ru.darkrpmc.scheduler
import ru.darkrpmc.text.colored

class ProfMenu(val player: Player, private val category: ProfessionCategory? = null) :
    Menu(if (category == null) 1 else (ProfessionInfo.entries.count { it.category == category } + 8) / 9,
        "Выбор профессии".colored(DARK_AQUA)) {

    private val end = player.uptime.lastProfChange + Settings.Professions.changeCooldown

    init {
        if (category == null) {
            inv.setItem(0, Government.item)
            inv.setItem(2, LawEnforcement.item)
            inv.setItem(4, Army.item)
            inv.setItem(6, Civilian.item)
            inv.setItem(8, Criminal.item)
        } else scheduler.runTaskTimer(plugin, { task ->
            if (task != null && inv.viewers.isEmpty()) task.cancel()
            var slot = 0
            ProfessionInfo.entries.forEach { if (it.category == category) inv.setItem(slot++, it.genItemFor(player)) }
        }, 0, 20)
        player.openInventory(inv)
    }

    override fun handleClick(slot: Int, clickType: ClickType) {
        if (clickType != RIGHT && clickType != LEFT) return
        if (category == null) when (slot) {
            0 -> ProfMenu(player, Government)
            2 -> ProfMenu(player, LawEnforcement)
            4 -> ProfMenu(player, Army)
            6 -> ProfMenu(player, Civilian)
            8 -> ProfMenu(player, Criminal)
        } else ProfessionInfo.entries.filter { it.category == category }.getOrNull(slot)?.run {
            if (this != player.profession.info) pickProtocol(player)
        }
    }
}