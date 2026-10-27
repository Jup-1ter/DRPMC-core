package ru.darkrpmc.commands

import com.mojang.brigadier.arguments.BoolArgumentType.bool
import com.mojang.brigadier.arguments.BoolArgumentType.getBool
import com.mojang.brigadier.arguments.IntegerArgumentType.getInteger
import com.mojang.brigadier.arguments.IntegerArgumentType.integer
import com.mojang.brigadier.context.CommandContext
import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.command.brigadier.Commands.argument
import io.papermc.paper.command.brigadier.Commands.literal
import io.papermc.paper.command.brigadier.argument.ArgumentTypes.players
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor.DARK_AQUA
import net.kyori.adventure.text.format.NamedTextColor.GRAY
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import ru.darkrpmc.*
import ru.darkrpmc.inventory.times
import ru.darkrpmc.text.*

// @formatter:off
val rpItemCommand = literal("rp-item")
    .requires { it.sender.checkUnsafe(Permission.RPItem) }
    .then(argument("целевые игроки", players())
        .suggests { ctx, b ->
            val sender = ctx.source.sender
            if (sender is Player) server.onlinePlayers.forEach {
                if (it.name.startsWith(b.remaining, true) && sender.canSee(it)) b.suggest(it.name)
            }
            b.buildFuture()
        }
        .then(argument("предмет", RPItemArgument())
            .executes(::process)
            .then(argument("количество", integer(1))
                .requires { it.sender is Player && it.sender.checkUnsafe(Permission.HideExecutor) }
                .executes { ctx -> process(ctx, getInteger(ctx, "количество")) }
                .then(argument("скрыть исполнителя", bool())
                    .requires { it.sender is Player && it.sender.checkUnsafe(Permission.HideExecutor) }
                    .executes { ctx ->
                        process(ctx, getInteger(ctx, "количество"), !getBool(ctx, "скрыть исполнителя"))
                    }
                )
            )
        )
    ).build()

private fun process(ctx: CommandContext<CommandSourceStack>, quantity: Int = 1, showExecutor: Boolean? = null): Int {
    val src = ctx.source
    val sender = src.sender
    val item = ctx.getArgument("предмет", ItemStack::class.java)
    var targets = ctx.getArgument("целевые игроки", PlayerSelectorArgumentResolver::class.java).resolve(ctx.source)
    if (sender is Player) targets = targets.filter { sender.canSee(it) }
    val items = item * quantity
    var secondPart = " выдан предмет ".colored(GRAY).append(item.displayName())
    if (quantity != 1) secondPart = secondPart.append(miniMessage("<gray> в количестве <gold>$quantity</gold> шт."))
    val mainMessage = "Вам".colored(DARK_AQUA).append(secondPart)
    targets.forEach {
        it.inventory.addItem(items)
        it.sendMessage(
            if (showExecutor ?: (sender is Player && it.canSee(sender)))
                mainMessage.append(" администратором ".colored(GRAY).append(playerComponent(sender as Player)))
            else mainMessage
        )
    }
    val tSize = targets.size

    sender.sendMessage(
        Component.text().append(playersComponent(sender, targets, Падеж.Дательный)).append(secondPart).build()
    )
    return tSize
}