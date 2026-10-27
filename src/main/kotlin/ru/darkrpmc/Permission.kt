package ru.darkrpmc

import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import ru.darkrpmc.data.rankOrUser

/** Перечисление существующих прав, которые могут быть назначены разным ролям */
enum class Permission {
    /** Право использовать команду и просматривать свою скорость */
    SpeedBase,

    /** Право просматривать скорость других игроков */
    SpeedCheckOthers,

    /** Право изменять собственную скорость */
    SpeedOwn,

    /** Право изменять скорость других игроков */
    SpeedOthers,

    HighlightRegions, HighlightEntities,
    VelocityBase, VelocityOwn, VelocityOthers,
    NonRPProfession, HideExecutor,
    InvulnerabilityOwn, InvulnerabilityOthers,
    RankCommand, RankBypass,
    RPItem, Weapon,
    HideOwn, SeeHidden,
    BalanceGet, BalanceChange,
    Online;

    operator fun contains(subject: CommandSender) = subject.isOp ||
            (subject is Player && subject.rankOrUser in this)

    operator fun contains(rank: Rank) = this in rank.permissions
}