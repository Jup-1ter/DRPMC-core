package ru.darkrpmc

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor.*
import org.bukkit.scoreboard.Team

val RPTeam = mainScoreboard.getTeam("rp") ?: mainScoreboard.registerNewTeam("rp").apply {
    color(GOLD)
    displayName(Component.text("Игроки").color(GRAY))
    setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.FOR_OWN_TEAM)
}

val nonRPTeam = mainScoreboard.getTeam("nonrp") ?: mainScoreboard.registerNewTeam("nonrp").apply {
    color(DARK_AQUA)
    displayName(Component.text("Администрация").color(GRAY))
    setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.FOR_OTHER_TEAMS)
}