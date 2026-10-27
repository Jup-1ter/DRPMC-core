package ru.darkrpmc.data

import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.jetbrains.exposed.sql.ResultRow
import ru.darkrpmc.DonateGroup
import ru.darkrpmc.Rank
import ru.darkrpmc.data.database.AccountsTable
import ru.darkrpmc.data.database.PerServerDataTable
import ru.darkrpmc.math.geometry.Point
import ru.darkrpmc.professions.Profession
import ru.darkrpmc.professions.civilian.Citizen
import java.time.LocalDateTime

/**
 * Данные игрока, хранимые в пределах 1 игрового сеанса.
 * @property profession Текущая профессия игрока
 * @property lastRobbed Когда игрока последний раз грабили
 * @property withMod Имеет ли игрок модификацию DRPMC
 * @property hwid Уникальный идентификатор устройства игрока; (hwid != null) == withMod
 */
class InSessionData(
    player: Player,
    var profession: Profession = Citizen(player),
    var lastRobbed: Long = 0,
    var withMod: Boolean = false,
    var hwid: String? = null
) {
    val blocks = HashSet<Point>()
}

val sessionData = hashMapOf<String, InSessionData>()

/**
 * Данные игрока, хранимые в пределах 1 рабочего цикла сервера.
 * @property haveDailyCase Имеет ли игрок ежедневный кейс
 * @property lastProfChange Когда игрок последний раз меня профессию
 */
class UptimeData(
    var haveDailyCase: Boolean = true,
    var lastProfChange: Long = 0
)

val uptimeData = hashMapOf<String, UptimeData>()

/**
 * Данные аккаунта игрока на системе серверов
 * @property regIP IP игрока при первом заходе
 * @property lastIP IP игрока при предыдущем заходе
 * @property lastSeen Когда игрок в последний раз был замечен на сервере
 * @property hadMod Заходил ли игрок с модом
 * @property firstHWID Первый замеченный у игрока HWID
 */
class Account(
    val regIP: String,
    var lastIP: String,
    var lastSeen: LocalDateTime = LocalDateTime.now(),
    var hadMod: Boolean = false,
    var firstHWID: ByteArray? = null,
    var rank: Rank = Rank.USER,
    var donateGroup: DonateGroup = DonateGroup.USER,
) {
    constructor(row: ResultRow) : this(
        row[AccountsTable.regIPColumn],
        row[AccountsTable.lastIPColumn],
        row[AccountsTable.lastSeenColumn],
        row[AccountsTable.hadModColumn],
        row[AccountsTable.firstHWIDColumn],
        row[AccountsTable.rankColumn],
        row[AccountsTable.donateGroupColumn]
    )
}

val accounts = hashMapOf<String, Account>()

/**
 * Постоянные данные игрока. Хранятся в базе данных
 * @property lastSeen Последнее появление игрока на игровом сервере
 * @property balanceCents Баланс игрока в центах
 */
class PerServerData(
    var lastSeen: LocalDateTime = LocalDateTime.now(),
    var balanceCents: Long = 100_00,
    var backpack: HashMap<Int, ItemStack> = hashMapOf(),
    var vault: HashMap<Int, ItemStack> = hashMapOf(),
    var backpackLvl: Int = 0,
    var vaultLvl: Int = 0,
    var invLvl: Int = 0
) {
    constructor(row: ResultRow) : this(
        row[PerServerDataTable.lastSeenColumn],
        row[PerServerDataTable.balanceColumn],
        row[PerServerDataTable.backpackColumn] ?: hashMapOf(),
        row[PerServerDataTable.vaultColumn] ?: hashMapOf(),
        row[PerServerDataTable.backpackLvlColumn],
        row[PerServerDataTable.vaultLvlColumn],
        row[PerServerDataTable.invLvlColumn]
    )
}

val perServerData = hashMapOf<String, PerServerData>()