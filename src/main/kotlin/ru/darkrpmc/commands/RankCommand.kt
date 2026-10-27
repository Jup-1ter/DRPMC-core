package ru.darkrpmc.commands

import com.mojang.brigadier.arguments.BoolArgumentType
import com.mojang.brigadier.arguments.StringArgumentType.getString
import com.mojang.brigadier.arguments.StringArgumentType.string
import com.mojang.brigadier.context.CommandContext
import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.command.brigadier.Commands.argument
import io.papermc.paper.command.brigadier.Commands.literal
import net.kyori.adventure.text.format.NamedTextColor.GRAY
import org.bukkit.GameMode
import org.bukkit.entity.Player
import org.jetbrains.exposed.sql.SqlExpressionBuilder.less
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.update
import ru.darkrpmc.*
import ru.darkrpmc.data.*
import ru.darkrpmc.data.database.AccountsTable
import ru.darkrpmc.data.database.AccountsTable.rankColumn
import ru.darkrpmc.data.database.AccountsTable.selector
import ru.darkrpmc.data.database.asyncTransaction
import ru.darkrpmc.professions.NonRP
import ru.darkrpmc.professions.ProfessionInfo
import ru.darkrpmc.text.*

// @formatter:off
val rankCommand = literal("rank")
    .requires { it.sender.checkUnsafe(Permission.RankCommand) }
    .then(argument("целевой игрок", string())
        .suggests { ctx, b ->
            val sender = ctx.source.sender
            if (sender is Player) server.onlinePlayers.forEach {
                if (it.name.startsWith(b.remaining, true) && sender.canSee(it)) b.suggest(it.name)
            }
            b.buildFuture()
        }
        .then(argument("ранг", RankArgument())
            .executes(::process)
            .then(argument("скрыть исполнителя", BoolArgumentType.bool())
                .requires { it.sender is Player && it.sender.checkUnsafe(Permission.HideExecutor) }
                .executes { ctx -> process(ctx, !BoolArgumentType.getBool(ctx, "скрыть исполнителя")) }
            )
        )
    ).build()
// @formatter:on

private fun process(ctx: CommandContext<CommandSourceStack>, showExecutor: Boolean? = null): Int {
    val src = ctx.source
    val sender = src.sender
    val name = getString(ctx, "целевой игрок")
    val rank = ctx.getArgument("ранг", Rank::class.java)
    val mediocre = sender !in Permission.RankBypass
    val senderRank = (sender as? Player)?.rank ?: Rank.USER
    if (mediocre && senderRank <= rank) {
        sender.sendRichMessage("<red>Вы можете установить ранг только ниже собственного")
        return -1
    }
    val player = server.getPlayer(name)
    if (player != null) {
        if (mediocre && player == sender) {
            sender.sendRichMessage("<red>Вы не можете изменить свой ранг")
            return -1
        }
        if (mediocre && senderRank <= player.rankOrUser) {
            sender.sendRichMessage("<red>Вы можете изменять ранги только нижестоящих игроков")
            return -1
        }
        player.setRank(
            rank, if (showExecutor ?: (sender is Player && player.canSee(sender))) sender as Player else null
        )
        sender.sendMessage(
            "Ранг игрока ".colored(GRAY).append(playerComponent(player)).append(" установлен на ".colored(GRAY))
                .append(rank.displayName).append(" ($rank)".colored(GRAY))
        )
        return 1
    } else asyncTransaction {
        var s = selector(name)
        if (mediocre) s = s and rankColumn.less(senderRank)
        if (AccountsTable.update({ s }) {
                it[rankColumn] = rank
            } > 0) sender.sendMessage(
            miniMessage(
                "<gray>Ранг игрока <dark_aqua>$name</dark_aqua> установлен на "
            ).append(rank.displayName).append(" ($rank)".colored(GRAY))
        ) else sender.sendRichMessage("<red>Игрок не найден" + if (mediocre) " или его ранг не ниже вашего" else "")
    }
    return 0
}

fun Player.setRank(rank: Rank, by: Player? = null) {
    this.rank = rank
    if (rank !in Permission.SpeedOwn) {
        walkSpeed = .2f
        flySpeed = .1f
    }
    if (rank !in Permission.NonRPProfession && profession is NonRP) {
        setProfession(ProfessionInfo.CITIZEN)
        tpToSpawnAsync()
        gameMode = GameMode.SURVIVAL
    }
    if (rank !in Permission.HideOwn) {
        hidden -= this
        server.onlinePlayers.forEach { it.showPlayer(plugin, this) }
    }
    if (rank !in Permission.SeeHidden) hidden.forEach { hidePlayer(plugin, it) }
    else hidden.forEach { showPlayer(plugin, it) }
    if (rank !in Permission.HighlightRegions) regionHighlighting.remove(name)
    updateCommands()
    val message = miniMessage("<gray><dark_aqua>Ваш</dark_aqua> ранг установлен на ").append(rank.displayName)
    sendMessage(
        if (by != null) message.append(" администратором ".colored(GRAY)).append(playerComponent(by)) else message
    )
}