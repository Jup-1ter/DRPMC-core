package ru.darkrpmc.commands

import com.mojang.brigadier.tree.LiteralCommandNode
import io.papermc.paper.command.brigadier.CommandSourceStack

// @formatter:off
enum class Commands(
    private val command: LiteralCommandNode<CommandSourceStack>,
    private val description: String,
    private val alts: Set<String> = emptySet()
) {
    HighlightCommand(highlightCommand, "Подсвечивает указанный объект", setOf("hl")),
    LobbyCommand(lobbyCommand, "Позволяет вернуться в лобби"),
    SpeedCommand(
        speedCommand, "Изменяет вашу или указанного игрока скорость",
        setOf("speed", "скорость-передвижения", "скорость")
    ),
    VelocityCommand(velocityCommand, "Изменяет вашу или указанного игрока текущую скорость", setOf("vel")),
    RankCommand(rankCommand, "Служит для установки административной роли игрока"),
    RPItemCommand(rpItemCommand, "Служит для выдачи игрокам особых предметов", setOf("rpi", "rpitem")),
    HideCommand(hideCommand, "Служит для скрытия игроков", setOf("h", "v", "cloak")),
    OnlineCommand(onlineCommand, "Служит для отображения списка игроков"),
    WeaponCommand(weaponCommand, "Служит для выдачи оружия"),
    BalanceCommand(balanceCommand, "Служит для получения и изменения баланса игроков");

    fun register(registrar: io.papermc.paper.command.brigadier.Commands) =
        registrar.register(command, description, alts)
}