package ru.darkrpmc.professions

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextComponent
import net.kyori.adventure.text.format.NamedTextColor.*
import org.bukkit.Location
import ru.darkrpmc.*
import ru.darkrpmc.data.*
import ru.darkrpmc.text.colored

interface WithCustomSpawn {
    val spawn: Location
}

fun startSalaryTimer() {
    scheduler.runTaskTimer(plugin, { ->
        server.onlinePlayers.forEach {
            val toGive = it.salary
            it.giveCents(toGive)
            // TODO it.sendMessage(formSalaryMessage(toGive, it.salaryBonus, it.donateGroup))
        }
    }, 0, 20 * 60 * 5)
}

@Suppress("Unused") // TODO убрать
private fun formSalaryMessage(toGive: Double, salaryBonus: Double, group: DonateGroup): TextComponent.Builder {
    val message = Component.text().content("Вы получили зарплату в размере ").color(GRAY)
        .append("$%.3f".format(toGive).colored(GOLD))
    if (salaryBonus != .0 || group.salaryBonus != .0) {
        message.append(" (".colored(DARK_AQUA))
        val donateBonus = "бонус от $group"
        if (salaryBonus != .0) {
            message.append("${salaryBonus * 100}% бонус".colored(DARK_AQUA))
            if (group.salaryBonus != .0) message.append(" + $donateBonus".colored(DARK_AQUA))
        } else message.append(donateBonus.colored(DARK_AQUA))
        message.append(")".colored(DARK_AQUA))
    }
    return message
}