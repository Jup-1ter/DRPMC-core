package ru.darkrpmc.text

import io.papermc.paper.adventure.providers.ClickCallbackProviderImpl
import io.papermc.paper.command.brigadier.MessageComponentSerializer
import net.kyori.adventure.text.*
import net.kyori.adventure.text.event.ClickCallback
import net.kyori.adventure.text.event.ClickCallback.DEFAULT_LIFETIME
import net.kyori.adventure.text.event.ClickCallback.Options
import net.kyori.adventure.text.event.HoverEvent
import net.kyori.adventure.text.format.NamedTextColor.DARK_AQUA
import net.kyori.adventure.text.format.NamedTextColor.GRAY
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.serializer.json.JSONComponentSerializer
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import ru.darkrpmc.fullName
import java.time.temporal.TemporalAmount

fun String.colored(color: TextColor) = Component.text(this).color(color)
fun List<String>.colored(color: TextColor): List<TextComponent> = map { it.colored(color) }

//fun String.colored(color: Int): TextComponent {
//    return Component.text(this).color(TextColor.color(color))
//}

private val mm = MiniMessage.miniMessage()
fun miniMessage(text: String) = mm.deserialize(text)

val m = MessageComponentSerializer.message()

operator fun String.times(i: Int) = buildString { repeat(i) { append(this@times) } }

private val ccbP = ClickCallbackProviderImpl()
fun createClickEvent(duration: TemporalAmount, action: (Player) -> Unit) =
    ccbP.create(ClickCallback.widen(action, Player::class.java), Options.builder().lifetime(duration).build())

private val wrapper = "─────────────────────────────".colored(GRAY)
fun ComponentLike.wrapBroadcast() = Component.text()
    .append(wrapper).appendNewline().append(this).appendNewline().append(wrapper).build()

@Suppress("NonAsciiCharacters", "LocalVariableName")
fun playersComponent(beholder: CommandSender, players: Collection<Player>, падеж: Падеж): Component {
    val canSee = players.filter { beholder !is Player || beholder.canSee(it) }
    val ps = canSee.size
    return if (ps == 1) playerComponent(canSee.first())
    else {
        val ending = when (падеж) {
            Падеж.Именительный -> "" to "а"
            Падеж.Родительный -> "а" to "ов"
            Падеж.Дательный -> "у" to "ам"
            Падеж.Винительный -> "а" to "ов"
            Падеж.Творительный -> "ом" to "ами"
            Падеж.Предложный -> "е" to "ах"
        }
        val sorted = canSee.sortedBy { it.name }
        "$ps игрок${if (ps % 10 == 1) ending.first else ending.second}".colored(DARK_AQUA).hoverEvent(
            HoverEvent.showText(sorted.joinToString { it.name }.colored(DARK_AQUA))
        ).clickEvent(createClickEvent(DEFAULT_LIFETIME) { player ->
            val message =
                Component.text().append(playerComponent(sorted.first()))
            sorted.drop(1).forEach { message.append(", ".colored(GRAY)).append(playerComponent(it)) }
            player.sendMessage(message)
        })
    }
}

// TODO получение информации об игроке
fun playerComponent(player: Player) = player.name.colored(DARK_AQUA).hoverEvent(HoverEvent.showText(player.fullName))

fun minutesAndSeconds(time: Long): String {
    var secs = time / 1000
    val mins = secs / 60
    secs %= 60
    return "$mins:${if (secs < 10) "0" else ""}$secs"
}

val jsonSerializer = JSONComponentSerializer.json()