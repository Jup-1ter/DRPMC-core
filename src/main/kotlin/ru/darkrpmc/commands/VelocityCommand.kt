@file:Suppress("UnstableApiUsage")

package ru.darkrpmc.commands

import com.mojang.brigadier.Command
import com.mojang.brigadier.arguments.DoubleArgumentType.doubleArg
import com.mojang.brigadier.arguments.DoubleArgumentType.getDouble
import io.papermc.paper.command.brigadier.Commands.argument
import io.papermc.paper.command.brigadier.Commands.literal
import io.papermc.paper.command.brigadier.argument.ArgumentTypes
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver
import net.kyori.adventure.text.format.NamedTextColor.GOLD
import net.kyori.adventure.text.format.NamedTextColor.GRAY
import net.minecraft.commands.CommandSourceStack.ERROR_NOT_PLAYER
import net.minecraft.commands.arguments.EntityArgument
import org.bukkit.entity.Player
import org.bukkit.util.Vector
import ru.darkrpmc.*
import ru.darkrpmc.text.colored
import ru.darkrpmc.text.playerComponent

/** Команда для получения / изменения скорости игроков */
val velocityCommand = literal("velocity")
    .requires { it.sender.checkUnsafe(Permission.VelocityBase) }
    // Получение своей скорости
    .executes { ctx ->
        val sender = ctx.source.sender
        if (sender !is Player) throw ERROR_NOT_PLAYER.create()
        val vel = sender.velocity
        sender.sendRichMessage(
            "<gray><dark_aqua>Ваша</dark_aqua> текущая скорость <gold>{${vel.x}; ${vel.y}; ${vel.z}}</gold>"
        )
        Command.SINGLE_SUCCESS
    }
    // Подкоманда для получения скорости других игроков
    .then(
        literal("of")
            .requires { it.sender.checkUnsafe(Permission.VelocityOthers) }
            .then(
                argument("целевые игроки", ArgumentTypes.players())
                    .executes { ctx ->
                        val sender = ctx.source.sender
                        var targets = ctx.getArgument("целевые игроки", PlayerSelectorArgumentResolver::class.java)
                            .resolve(ctx.source)
                        if (sender is Player) targets = targets.filter { sender.canSee(it) }
                        val tSize = targets.size
                        if (tSize == 0) throw EntityArgument.NO_PLAYERS_FOUND.create()
                        targets.forEach {
                            val vel = it.velocity
                            sender.sendMessage(
                                "Текущая скорость ".colored(GRAY).append(playerComponent(it))
                                    .append(" {${vel.x}; ${vel.y}; ${vel.z}}".colored(GOLD))
                            )
                        }
                        tSize
                    }
            )
    )
    .then(
        argument("x", doubleArg())
            .requires { it.sender.checkUnsafe(Permission.VelocityOwn) }
            .then(
                argument("y", doubleArg())
                .then(
                    argument("z", doubleArg())
                        .executes { ctx ->
                            val sender = ctx.source.sender
                            if (sender !is Player) throw ERROR_NOT_PLAYER.create()
                            sender.velocity = Vector(
                                getDouble(ctx, "x"), getDouble(ctx, "y"), getDouble(ctx, "z")
                            )
                            return@executes 1
                        }
                )
            )
    ).build()