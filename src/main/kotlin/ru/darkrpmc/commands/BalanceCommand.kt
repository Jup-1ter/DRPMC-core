package ru.darkrpmc.commands

import com.mojang.brigadier.arguments.BoolArgumentType.bool
import com.mojang.brigadier.arguments.BoolArgumentType.getBool
import com.mojang.brigadier.arguments.DoubleArgumentType.doubleArg
import com.mojang.brigadier.arguments.DoubleArgumentType.getDouble
import io.papermc.paper.command.brigadier.Commands.argument
import io.papermc.paper.command.brigadier.Commands.literal
import io.papermc.paper.command.brigadier.argument.ArgumentTypes.players
import net.kyori.adventure.text.format.NamedTextColor.GOLD
import net.kyori.adventure.text.format.NamedTextColor.GRAY
import net.minecraft.commands.CommandSourceStack.ERROR_NOT_PLAYER
import net.minecraft.commands.arguments.EntityArgument
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import ru.darkrpmc.Permission
import ru.darkrpmc.checkUnsafe
import ru.darkrpmc.data.balance
import ru.darkrpmc.text.*
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver as PSAR

// @formatter:off
@Suppress("UnstableApiUsage")
val balanceCommand = literal("balance")
    .requires { it.sender.checkUnsafe(Permission.BalanceGet) }
    .executes { ctx ->
        val player = ctx.source.sender as? Player ?: throw ERROR_NOT_PLAYER.create()
        player.sendRichMessage("<gray>Ваш баланс <gold>${player.balance}$")
        return@executes 1
    }
    .then(argument("целевые игроки", players())
        .executes { ctx ->
            val src = ctx.source
            val sender = src.sender
            var targets = ctx.getArgument("целевые игроки", PSAR::class.java).resolve(ctx.source)
            if (sender is Player) targets = targets.filter { sender.canSee(it) }
            val tSize = targets.size
            if (tSize == 0) throw EntityArgument.NO_PLAYERS_FOUND.create()
            targets.forEach {
                sender.sendMessage(
                    "Баланс игрока ".colored(GRAY).append(playerComponent(it)).append(" ${it.balance}$".colored(GOLD))
                )
            }
            return@executes tSize
        }
        .then(changeBalanceBranch("=", BalanceAction.SET))
        .then(changeBalanceBranch("+", BalanceAction.ADD))
        .then(changeBalanceBranch("-", BalanceAction.SUBTRACT))
    )
    .build()

private fun changeBalanceBranch(
    literalName: String,
    action: BalanceAction
) = literal(literalName)
    .requires { it.sender.checkUnsafe(Permission.BalanceChange) }
    .then(argument("значение", doubleArg())
        .executes { ctx -> process(
            ctx.source.sender,
            ctx.getArgument("целевые игроки", PSAR::class.java).resolve(ctx.source),
            action,
            getDouble(ctx, "значение")
        ) }
        .then(argument("скрыть исполнителя", bool())
            .executes { ctx -> process(
                ctx.source.sender,
                ctx.getArgument("целевые игроки", PSAR::class.java).resolve(ctx.source),
                action,
                getDouble(ctx, "значение"),
                !getBool(ctx, "скрыть исполнителя")
            ) }
        )
    )

private fun process(
    sender: CommandSender,
    targets: List<Player>,
    action: BalanceAction,
    amount: Double,
    showExecutor: Boolean? = null
): Int {
    var filtered = targets
    if (sender is Player) filtered = targets.filter { sender.canSee(it) }
    val tSize = filtered.size
    if (tSize == 0) throw EntityArgument.NO_PLAYERS_FOUND.create()
    filtered.forEach {
        val message = miniMessage("<dark_aqua>Ваш <gray>баланс установлен на <gold>${
            when (action) {
                BalanceAction.SET -> {
                    it.balance = amount
                    amount
                }
                BalanceAction.ADD -> (it.balance?.plus(amount)).also { a -> it.balance = a }
                BalanceAction.SUBTRACT -> (it.balance?.minus(amount)).also { a -> it.balance = a }
            }
        }$")
        it.sendMessage(
            if (showExecutor ?: (sender is Player && it.canSee(sender)))
                message.append(" администратором ".colored(GRAY).append(playerComponent(sender as Player)))
            else message
        )
    }
    return tSize
}

private enum class BalanceAction { SET, ADD, SUBTRACT }