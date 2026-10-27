package ru.darkrpmc.professions.goverment

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextComponent
import net.kyori.adventure.text.event.HoverEvent
import net.kyori.adventure.text.format.NamedTextColor.*
import org.bukkit.boss.BarColor
import org.bukkit.boss.BarStyle
import org.bukkit.entity.Player
import org.bukkit.scoreboard.Criteria
import org.bukkit.scoreboard.DisplaySlot
import ru.darkrpmc.*
import ru.darkrpmc.data.profession
import ru.darkrpmc.professions.ProfessionInfo
import ru.darkrpmc.professions.ProfessionSource
import ru.darkrpmc.professions.enforcers.*
import ru.darkrpmc.text.*
import java.time.Duration.of
import java.time.temporal.ChronoUnit

private const val rate = 4
private const val period = 20L / rate

private const val recruitmentDuration = 15L
private const val recruitmentIterations = recruitmentDuration * rate

private const val electionDuration = 15L
private const val electionIterations = electionDuration * rate

private var lastElection = 0L
var electionInProgress = false
private const val reElectCD = 1000 * 60 * 10
val toNextElection get() = lastElection + reElectCD - System.currentTimeMillis()
val isAnyMayor get() = server.onlinePlayers.any { it.profession is Mayor }

val candidates = hashMapOf<Player, Int>()
private val bar = server.createBossBar(null, BarColor.YELLOW, BarStyle.SOLID)
private val votingScoreboard = scoreboardManager.newScoreboard
private val votingObjective = votingScoreboard.getObjective("Выборы мэра") ?: (votingScoreboard.registerNewObjective(
    "Выборы мэра",
    Criteria.DUMMY,
    miniMessage("<gray>Выборы <dark_aqua>мэра")
)
    .apply { displaySlot = DisplaySlot.SIDEBAR })

@Suppress("UnusedReceiverParameter")
fun ProfessionInfo.pickMayor(player: Player) {
    if (electionInProgress || isAnyMayor && toNextElection > 0 || player.name in cuffs || player.name in stunned) return
    if (candidates.isNotEmpty()) {
        if (candidates.put(player, 1) == null) {
            player.sendRichMessage("<gray>Вы выдвинули свою кандидатуру на пост <dark_aqua>мэра")
            player.closeInventory()
        }
        return
    }
    player.closeInventory()

    candidates[player] = 1

    player.sendRichMessage("<gray>Начат набор кандидатов на пост <dark_aqua>мэра")

    bar.setTitle("Набор кандидатов на пост мэра")
    bar.progress = 1.0
    server.onlinePlayers.forEach {
        if (it != player) it.sendMessage(recruitmentMessage)
        bar.addPlayer(it)
    }

    var remaining = recruitmentIterations.toDouble()
    scheduler.runTaskTimer(plugin, { task ->
        if (--remaining <= 0) {
            startElection()
            task.cancel()
        } else bar.progress = remaining / recruitmentIterations
    }, period, period)
}

private fun startElection() {
    electionInProgress = true

    candidates.keys.forEach { votingObjective.getScore(it).score = 1 }
    val electionMessage = electionMessage
    server.onlinePlayers.forEach {
        if (it.scoreboard == mainScoreboard) it.scoreboard = votingScoreboard
        if (it !in candidates) it.sendMessage(electionMessage)
    }

    bar.setTitle("Голосование за мера")
    bar.progress = 1.0
    var remaining = electionIterations.toDouble()
    scheduler.runTaskTimer(plugin, { task ->
        if (--remaining <= 0) {
            processElectionResults()
            task.cancel()
        } else bar.progress = remaining / electionIterations
    }, period, period)

}

private fun processElectionResults() {
    electionInProgress = false
    bar.removeAll()
    val winner = candidates.filter { it.key.isOnline }.maxByOrNull { it.value } // TODO проверка тюрьмы
    server.onlinePlayers.forEach {
        if (it.scoreboard == votingScoreboard) it.scoreboard = mainScoreboard
        if (winner == null) it.sendRichMessage(
            "<gray>К сожалению, <dark_aqua>мэр</dark_aqua> не был избран, т.к. все кандидаты выбыли."
        )
        else processWinner(it, winner.key, winner.value)
    }
    candidates.keys.forEach(votingScoreboard::resetScores)
    candidates.clear()
    lastElection = System.currentTimeMillis()
}

private fun processWinner(player: Player, mayor: Player, score: Int) {
    val scorePercent = score * 100.0 / candidates.values.sum()

    if (player == mayor) {
        if (player.profession is Mayor) player.sendRichMessage(
            "<gray>Вы переизбраны на пост <dark_aqua>мэра</dark_aqua>, набрав <gold>$score</gold> " +
                    "<dark_purple>(${String.format("%.1f", scorePercent)}%)</dark_purple> голосов!"
        )
        else {
            player.sendRichMessage(
                "<gray>Вы избраны на пост <dark_aqua>мэра</dark_aqua>, набрав <gold>$score</gold> " +
                        "<dark_purple>(${String.format("%.1f", scorePercent)}%)</dark_purple> голосов!"
            )
            player.setProfession(ProfessionInfo.MAYOR, ProfessionSource.TAKEN)
        }
    } else {
        if (player.profession is Mayor) {
            player.setProfession(ProfessionInfo.CITIZEN, ProfessionSource.DEMOTE)
            player.sendRichMessage("<gray>Вы больше не являетесь <dark_aqua>мэром</dark_aqua>.")
        }
        player.sendMessage(
            Component.text()
                .append("На пост ".colored(GRAY)).append("мэра".colored(DARK_AQUA))
                .append(" избран ".colored(GRAY)).append(playerComponent(mayor))
                .append(", набрав ".colored(GRAY)).append("$score".colored(GOLD))
                .append(" (${String.format("%.1f", scorePercent)}%)".colored(DARK_PURPLE))
                .append(" голосов!".colored(GRAY)).build()
        )
    }
}

private val recruitmentMessage
    get() = Component.text()
        .append(miniMessage("<gray>Начат набор <dark_aqua>кандидатов</dark_aqua> на пост <dark_aqua>мэра</dark_aqua>!"))
        .appendNewline()
        .append(
            " >> Нажмите, чтобы принять участие <<".colored(GOLD)
                .hoverEvent(HoverEvent.showText("(Принять участие)".colored(GOLD)))
                .clickEvent(createClickEvent(of(recruitmentDuration, ChronoUnit.SECONDS)) { player ->
                    if (candidates.put(player, 1) == null)
                        player.sendRichMessage("<gray>Вы выдвинули свою кандидатуру на пост <dark_aqua>мэра")
                })
        ).wrapBroadcast()

private val electionMessage: Component
    get() {
        val message = Component.text()
            .append(miniMessage("<gray>Начались выборы <dark_aqua>мэра</dark_aqua>! Кандидаты на пост:"))

        candidates.forEach { (candidate, score) ->
            message.appendNewline().append(" >> ".colored(DARK_GRAY)).append(
                candidate.name.colored(DARK_AQUA).hoverEvent(HoverEvent.showText("(Проголосовать)".colored(GRAY)))
                    .clickEvent(createClickEvent(of(electionDuration, ChronoUnit.SECONDS)) { caster ->
                        // Проверки не требуются, т.к. callback одноразовый
                        val newScore = score + 1
                        candidates[candidate] = newScore
                        votingObjective.getScore(candidate).score = newScore
                        caster.sendMessage("Вы отдали свой голос за ".colored(GRAY).append(playerComponent(candidate)))
                    })
            )
        }

        return message.build().wrapBroadcast()
    }

fun ProfessionInfo.mayorLoreAdder(player: Player): List<TextComponent> {
    val c = cuffs[player.name]
    when {
        player.profession.info == this -> return listOf("Вы уже имеете эту профессию".colored(RED))
        c != null -> return listOf("Вы в ${if (c == CuffType.HANDCUFFS) "наручниках" else "стяжках"}".colored(RED))
        player.name in stunned -> return listOf("Вы оглушены шокером".colored(RED))
    }
    val result = arrayListOf<TextComponent>()
    if (isAnyMayor) result += "В городе присутствует мэр".colored(YELLOW)
    when {
        player in candidates -> result += "Вы уже выдвинули свою кандидатуру".colored(RED)
        electionInProgress -> result += listOf(
            "Набор кандидатов окончен,",
            "голосование в процессе"
        ).colored(RED)

        isAnyMayor && toNextElection > 0 -> result += listOf(
            "Смена мэра будет",
            "доступна через ${minutesAndSeconds(toNextElection)}"
        ).colored(RED)
    }
    return result
}