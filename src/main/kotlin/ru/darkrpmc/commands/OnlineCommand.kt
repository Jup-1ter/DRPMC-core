package ru.darkrpmc.commands

import com.mojang.brigadier.arguments.StringArgumentType.getString
import com.mojang.brigadier.arguments.StringArgumentType.word
import io.papermc.paper.command.brigadier.Commands.argument
import io.papermc.paper.command.brigadier.Commands.literal
import ru.darkrpmc.*

@Suppress("UnstableApiUsage")
val onlineCommand = literal("online")
    .requires { it.sender.checkUnsafe(Permission.Online) }
    .executes { ctx ->
        ctx.source.sender.sendPlainMessage(server.onlinePlayers.joinToString { it.name })
        return@executes 1
    }
    .then(argument("игрок", word())
        .executes { ctx ->
            ctx.source.sender.sendPlainMessage(isOnline(getString(ctx, "игрок")).toString())
            return@executes 1
        }
    )
    .build()