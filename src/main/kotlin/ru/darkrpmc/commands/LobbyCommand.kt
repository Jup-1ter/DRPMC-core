package ru.darkrpmc.commands

import io.papermc.paper.command.brigadier.Commands.literal
import org.bukkit.entity.Player
import ru.darkrpmc.toLobby

@Suppress("UnstableApiUsage")
val lobbyCommand = literal("lobby")
    .requires { src -> src.sender is Player }
    .executes { ctx ->
        (ctx.source.sender as Player).toLobby()
        return@executes 1
    }
    .build()