package ru.darkrpmc.commands

import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.exceptions.CommandSyntaxException
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import io.papermc.paper.command.brigadier.argument.CustomArgumentType.Converted
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.inventory.ItemStack
import ru.darkrpmc.text.colored
import ru.darkrpmc.text.m
import ru.darkrpmc.weapon.StandardWeapon
import java.util.concurrent.CompletableFuture

class WeaponArgument : Converted<ItemStack, String> {
    override fun convert(nativeType: String): ItemStack {
        try {
            return StandardWeapon.valueOf(nativeType.uppercase()).item
        } catch (ignored: IllegalArgumentException) {
            val message = m.serialize("Неизвестное оружие $nativeType".colored(NamedTextColor.RED))
            throw CommandSyntaxException(SimpleCommandExceptionType(message), message)
        }
    }

    override fun getNativeType(): StringArgumentType = StringArgumentType.word()

    override fun <S : Any> listSuggestions(
        ctx: CommandContext<S>,
        builder: SuggestionsBuilder
    ): CompletableFuture<Suggestions> {
        StandardWeapon.entries
            .filter { it.name.startsWith(builder.remaining, true) }
            .forEach { builder.suggest(it.name, m.serialize(it.displayName)) }
        return builder.buildFuture()
    }
}