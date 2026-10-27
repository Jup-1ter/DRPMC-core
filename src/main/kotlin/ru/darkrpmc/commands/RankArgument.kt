package ru.darkrpmc.commands

import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.exceptions.CommandSyntaxException
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.command.brigadier.argument.CustomArgumentType.Converted
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.entity.Player
import ru.darkrpmc.Permission
import ru.darkrpmc.Rank
import ru.darkrpmc.data.rankOrUser
import ru.darkrpmc.text.colored
import ru.darkrpmc.text.m
import java.util.concurrent.CompletableFuture

class RankArgument : Converted<Rank, String> {
    override fun convert(nativeType: String): Rank {
        try {
            return Rank.valueOf(nativeType.uppercase())
        } catch (ignored: IllegalArgumentException) {
            val message = m.serialize("Неизвестный ранг $nativeType".colored(NamedTextColor.RED))
            throw CommandSyntaxException(SimpleCommandExceptionType(message), message)
        }
    }

    override fun getNativeType(): StringArgumentType = StringArgumentType.word()

    override fun <S : Any> listSuggestions(
        ctx: CommandContext<S>,
        builder: SuggestionsBuilder
    ): CompletableFuture<Suggestions> {
        Rank.entries.filter {
            val sender = (ctx.source as? CommandSourceStack)?.sender
            it.name.startsWith(builder.remaining, true) &&
                    (sender !is Player || sender in Permission.RankBypass || it < sender.rankOrUser)
        }.forEach { builder.suggest(it.name, m.serialize(it.displayName)) }
        return builder.buildFuture()
    }
}