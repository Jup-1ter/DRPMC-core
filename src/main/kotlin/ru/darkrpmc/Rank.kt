package ru.darkrpmc

import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.NamedTextColor.*
import ru.darkrpmc.Permission.*
import ru.darkrpmc.text.colored
import java.util.*

enum class Rank(
    rusName: String,
    private val color: NamedTextColor,
    private val perms: EnumSet<Permission> = EnumSet.noneOf(Permission::class.java)
) {
    USER("Игрок", GRAY),
    INTERN(
        "Стажёр", WHITE, EnumSet.of(
            SpeedBase, SpeedOwn, HighlightEntities, InvulnerabilityOwn, NonRPProfession
        )
    ),
    HELPER("Хелпер", DARK_BLUE),
    JUNIOR_MODER("Мл. Модер", BLUE, EnumSet.of(SpeedCheckOthers)),
    MODER("Модер", DARK_AQUA, EnumSet.of(VelocityBase, VelocityOwn, VelocityOthers)),
    HIGHER_MODER("Ст. Модер", DARK_GREEN),
    JUNIOR_ADMIN("Мл. Админ", GREEN, EnumSet.of(HideExecutor)),
    ADMIN("Админ", YELLOW, EnumSet.of(SpeedOthers)),
    HIGHER_ADMIN(
        "Ст. Админ", GOLD,
        EnumSet.of(HighlightRegions, InvulnerabilityOthers, SeeHidden, HideOwn, BalanceGet)
    ),
    SUPERVISOR("Куратор", RED, EnumSet.of(RankCommand, SeeHidden, RPItem, Online, Weapon, BalanceChange)),
    FOUNDER("Высшая Адм.", DARK_PURPLE, EnumSet.of(RankBypass));

    val displayName = rusName.colored(this@Rank.color)

    val permissions: EnumSet<Permission> by lazy {
        entries.asSequence()
            .takeWhile { it < this }
            .fold(EnumSet.copyOf(perms)) { permSet, rank -> permSet.apply { addAll(rank.perms) } }
    }
}