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
import ru.darkrpmc.inventory.*
import ru.darkrpmc.professions.enforcers.handcuffs
import ru.darkrpmc.professions.enforcers.tie
import ru.darkrpmc.text.colored
import ru.darkrpmc.text.m
import ru.darkrpmc.weapon.AmmoType
import java.util.concurrent.CompletableFuture

class RPItemArgument : Converted<ItemStack, String> {
    override fun convert(nativeType: String): ItemStack {
        try {
            return RPItem.valueOf(nativeType.uppercase()).item
        } catch (ignored: IllegalArgumentException) {
            val message = m.serialize("Неизвестный предмет $nativeType".colored(NamedTextColor.RED))
            throw CommandSyntaxException(SimpleCommandExceptionType(message), message)
        }
    }

    override fun getNativeType(): StringArgumentType = StringArgumentType.word()

    override fun <S : Any> listSuggestions(
        ctx: CommandContext<S>,
        builder: SuggestionsBuilder
    ): CompletableFuture<Suggestions> {
        RPItem.entries
            .filter { it.name.startsWith(builder.remaining, true) }
            .forEach { builder.suggest(it.name, m.serialize(it.item.displayName())) }
        return builder.buildFuture()
    }

    enum class RPItem(val item: ItemStack) {
        MENU(menuItem), PASSPORT(passportItem), BACKPACK(backpackItem),
        HANDCUFFS(handcuffs), TIE(tie),
        PISTOL_AMMO(AmmoType.PISTOL.item),
        SUBMACHINE_AMMO(AmmoType.SUBMACHINE.item),
        RIFLE_AMMO(AmmoType.RIFLE.item),
        SHOTGUN_AMMO(AmmoType.SHOTGUN.item),
        SNIPER_AMMO(AmmoType.SNIPER.item),
        TASER_AMMO(AmmoType.TASER.item)
    }
}