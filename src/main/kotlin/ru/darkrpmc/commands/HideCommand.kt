@file:Suppress("UnstableApiUsage")

package ru.darkrpmc.commands

import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.tree.LiteralCommandNode
import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.command.brigadier.Commands.literal
import net.kyori.adventure.text.format.NamedTextColor.DARK_AQUA
import net.kyori.adventure.text.format.NamedTextColor.GRAY
import org.bukkit.entity.Player
import ru.darkrpmc.*
import ru.darkrpmc.text.*

val hidden = hashSetOf<Player>()

/** Команда для скрытия игроков */
val hideCommand: LiteralCommandNode<CommandSourceStack> = literal("hide")
    .requires { it.sender is Player && it.sender.checkUnsafe(Permission.HideOwn) }
    .then(literal("toggle").executes(::handleToggle))
    .then(literal("enable").executes(::handleEnable))
    .then(literal("disable").executes(::handleDisable))
    .then(literal("on").executes(::handleEnable))
    .then(literal("off").executes(::handleDisable))
    .executes(::handleToggle)
    .build()

private fun handleToggle(ctx: CommandContext<CommandSourceStack>): Int {
    val sender = ctx.source.sender as Player
    val start = "Теперь вас ".colored(GRAY)
    sender.sendMessage(
        if (sender in hidden) {
            hidden -= sender
            server.onlinePlayers.forEach { it.showPlayer(plugin, sender) }
            start.append(miniMessage("<gray>могут видеть <dark_aqua>все"))
        } else {
            val canSee = arrayListOf<Player>()
            hidden += sender
            server.onlinePlayers.forEach {
                if (it !in Permission.SeeHidden) it.hidePlayer(plugin, sender)
                else if (it != sender) canSee += it
            }
            if (canSee.size == 0) start.append("никто".colored(DARK_AQUA)).append(" не может видеть".colored(GRAY))
            else start.append(
                "мо${if (canSee.size % 10 == 1) "жет" else "гут"} видеть только ".colored(GRAY)
                    .append(playersComponent(sender, canSee, Падеж.Именительный))
            )
        }
    )
    return 1
}

private fun handleEnable(ctx: CommandContext<CommandSourceStack>): Int {
    val sender = ctx.source.sender as Player
    if (hidden.add(sender)) {
        val canSee = arrayListOf<Player>()
        server.onlinePlayers.forEach {
            if (it !in Permission.SeeHidden) it.hidePlayer(plugin, sender)
            else if (it != sender) canSee += it
        }
        val start = "Теперь вас ".colored(GRAY)
        sender.sendMessage(
            if (canSee.size == 0) start.append("никто".colored(DARK_AQUA)).append(" не может видеть".colored(GRAY))
            else start.append(
                "мо${if (canSee.size % 10 == 1) "жет" else "гут"} видеть только ".colored(GRAY)
                    .append(playersComponent(sender, canSee, Падеж.Именительный))
            )
        )
        return 1
    } else sender.sendRichMessage("<red>Ничего не изменилось, вы и так скрыты")
    return 0
}

private fun handleDisable(ctx: CommandContext<CommandSourceStack>): Int {
    val sender = ctx.source.sender as Player
    if (hidden.remove(sender)) {
        hidden -= sender
        server.onlinePlayers.forEach { it.showPlayer(plugin, sender) }
        sender.sendRichMessage("<gray>Теперь вас могут видеть <dark_aqua>все")
        return 1
    } else sender.sendRichMessage("<red>Ничего не изменилось, все и так могут вас видеть")
    return 0
}