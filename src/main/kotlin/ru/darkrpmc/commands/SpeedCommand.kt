@file:Suppress("UnstableApiUsage")

package ru.darkrpmc.commands

import com.mojang.brigadier.Command
import com.mojang.brigadier.arguments.BoolArgumentType.bool
import com.mojang.brigadier.arguments.BoolArgumentType.getBool
import com.mojang.brigadier.arguments.FloatArgumentType.floatArg
import com.mojang.brigadier.arguments.FloatArgumentType.getFloat
import com.mojang.brigadier.context.CommandContext
import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.command.brigadier.Commands.argument
import io.papermc.paper.command.brigadier.Commands.literal
import io.papermc.paper.command.brigadier.argument.ArgumentTypes.players
import net.kyori.adventure.text.format.NamedTextColor.GOLD
import net.kyori.adventure.text.format.NamedTextColor.GRAY
import net.minecraft.commands.CommandSourceStack.ERROR_NOT_PLAYER
import net.minecraft.commands.arguments.EntityArgument
import org.bukkit.entity.Player
import ru.darkrpmc.Permission
import ru.darkrpmc.checkUnsafe
import ru.darkrpmc.commands.SpeedType.FLY
import ru.darkrpmc.commands.SpeedType.WALK
import ru.darkrpmc.text.*
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver as PSAR

// @formatter:off
/** Команда для получения / изменения скорости игроков */
val speedCommand = literal("movement-speed")
    .requires { it.sender.checkUnsafe(Permission.SpeedBase) }
    // Получение своей скорости
    .executes { ctx ->
        val sender = ctx.source.sender
        if (sender !is Player) throw ERROR_NOT_PLAYER.create()
        val fSpeed = sender.flySpeed
        val wSpeed = sender.walkSpeed
        sender.sendRichMessage(
            "<gray><dark_aqua>Ваша</dark_aqua> скорость полёта " +
                    "<gold>${fSpeed}</gold> <dark_purple>(x${fSpeed * 10})</dark_purple>, ходьбы - " +
                    "<gold>${wSpeed}</gold> <dark_purple>(x${wSpeed * 5})</dark_purple>"
        )
        Command.SINGLE_SUCCESS
    }
    // Подкоманда для получения скорости других игроков
    .then(literal("of")
        .requires { it.sender.checkUnsafe(Permission.SpeedCheckOthers) }
        .then(argument("целевые игроки", players())
            .executes { ctx ->
                val sender = ctx.source.sender
                var targets = ctx.getArgument("целевые игроки", PSAR::class.java).resolve(ctx.source)
                if (sender is Player) targets = targets.filter { sender.canSee(it) }
                val tSize = targets.size
                if (tSize == 0) throw EntityArgument.NO_PLAYERS_FOUND.create()
                targets.forEach {
                    val fSpeed = it.flySpeed
                    val wSpeed = it.walkSpeed
                    sender.sendMessage(
                        "Скорость полёта ".colored(GRAY).append(playerComponent(it)).append(" $fSpeed ".colored(GOLD))
                            .append(miniMessage(
                                "<dark_purple>(x${fSpeed * 10})<gray>, ходьбы - " +
                                        "<gold>${wSpeed} <dark_purple>(x${wSpeed * 5})"
                            ))
                    )
                }
                tSize
            }
        )
    )
    //// Изменение скорости
    /// Установка абсолютных значений
    // Когда игрок находится в полёте
    .then(genOwnBranch(abs = true, cur = true))
    // Когда на земле
    .then(genOwnBranch(WALK, true, true))
    // Установка относительных значений
    .then(literal("x")
        .requires { it.sender.checkUnsafe(Permission.SpeedOwn) }
        // Когда в полёте
        .then(genOwnBranch(cur = true))
        // Когда на земле
        .then(genOwnBranch(WALK, cur = true))
    )
    // Ветка для изменения скорости полёта
    .then(literal("fly")
        .requires { it.sender.checkUnsafe(Permission.SpeedOwn) }
        // Установка абсолютного значения
        .then(genOwnBranch(abs = true))
        // Установка относительного значения
        .then(literal("x").then(genOwnBranch()))
        .then(argument("целевые игроки", players())
            .requires { it.sender.checkUnsafe(Permission.SpeedOthers) }
            // Установка абсолютного значения
            .then(genOtherBranch(abs = true))
            // Установка относительного значения
            .then(literal("x").then(genOtherBranch()))
        )
    )
    // Ветка для изменения скорости ходьбы
    .then(literal("walk")
        .requires { it.sender.checkUnsafe(Permission.SpeedOwn) }
        // Установка абсолютного значения
        .then(genOwnBranch(WALK, true))
        // Установка относительного значения
        .then(literal("x").then(genOwnBranch(WALK)))
        .then(argument("целевые игроки", players())
            .requires { it.sender.checkUnsafe(Permission.SpeedOthers) }
            // Установка абсолютного значения
            .then(genOtherBranch(WALK, true))
            // Установка относительного значения
            .then(literal("x").then(genOtherBranch(WALK)))
        )
    ).build()

private fun setOwn(ctx: CommandContext<CommandSourceStack>, type: SpeedType, abs: Boolean): Int {
    (ctx.source.sender as Player).setSpeed(
        type,
        if (abs) getFloat(ctx, type.str)
        else getFloat(ctx, "относ. " + type.str) * type.default
    )
    return 1
}

private fun setOthers(
    ctx: CommandContext<CommandSourceStack>,
    type: SpeedType,
    abs: Boolean,
    showExecutor: Boolean? = null
): Int {
    val src = ctx.source
    val sender = src.sender
    var targets = ctx.getArgument("целевые игроки", PSAR::class.java).resolve(src)
    if (sender is Player) targets = targets.filter { sender.canSee(it) }
    val tSize = targets.size
    if (tSize == 0) throw EntityArgument.NO_PLAYERS_FOUND.create()
    val speed =
        if (abs) getFloat(ctx, type.str)
        else getFloat(ctx, "относ. " + type.str) * type.default
    targets.forEach {
        it.setSpeed(
            type, speed, if (showExecutor ?: (sender is Player && it.canSee(sender))) sender as Player else null
        )
    }
    sender.sendMessage(
        "${type.str.replaceFirstChar(Char::uppercase)} ".colored(GRAY)
            .append(playersComponent(sender, targets, Падеж.Родительный))
            .append(miniMessage(
                " <gray>установлена на <gold>$speed</gold> <dark_purple>(x${speed / type.default})</dark_purple>"
            ))
    )
    return tSize
}

private fun Player.setSpeed(
    type: SpeedType,
    value: Float,
    by: Player? = null
) {
    when (type) {
        FLY -> flySpeed = value
        WALK -> walkSpeed = value
    }
    val message = miniMessage(
        "<gray><dark_aqua>Ваша</dark_aqua> ${type.str} установлена на <gold>$value</gold> <dark_purple>(x${
            value / type.default
        })</dark_purple>"
    )
    sendMessage(
        if (by != null) message.append(" администратором ".colored(GRAY)).append(playerComponent(by)) else message
    )
}

private fun genOwnBranch(type: SpeedType = FLY, abs: Boolean = false, cur: Boolean = false) =
    argument(
        (if (abs) "" else "относ. ") + type.str,
        floatArg(if (abs) type.minAbs else type.minFactor, if (abs) 1f else type.maxFactor)
    )
        .requires {
            var req = it.sender.checkUnsafe(Permission.SpeedOwn)
            if (cur) req = req && (it.sender as? Player)?.isFlying == (type == FLY)
            req
        }
        .executes { ctx -> setOwn(ctx, type, abs) }
private fun genOtherBranch(type: SpeedType = FLY, abs: Boolean = false) =
    argument(
        (if (abs) "" else "относ. ") + type.str,
        floatArg(if (abs) type.minAbs else type.minFactor, if (abs) 1f else type.maxFactor)
    )
        .executes { ctx -> setOthers(ctx, type, abs) }
        .then(argument("скрыть исполнителя", bool())
            .executes { ctx -> setOthers(ctx, type, abs, !getBool(ctx, "скрыть исполнителя")) }
        )
// @formatter:on

/** Перечисление видов скорости
 *  @param default скорость по умолчанию для этого типа
 *  @param str наименование этого типа
 *  @param minAbs минимальное абсолютное значение для скорости этого типа
 *  @param minFactor минимальное относительное значение для скорости этого типа
 *  @param maxFactor максимальное относительное значение для скорости этого типа

 *  @property FLY Скорость полёта
 *  @property WALK Скорость ходьбы */
private enum class SpeedType(
    val default: Float, val str: String, val minAbs: Float, val minFactor: Float, val maxFactor: Float
) {
    /** Скорость полёта */
    FLY(.1f, "скорость полёта", -1f, -10f, 10f),

    /** Скорость ходьбы */
    WALK(.2f, "скорость ходьбы", 0f, 0f, 5f)
}