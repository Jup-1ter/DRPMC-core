package ru.darkrpmc.data

import org.bukkit.entity.Player
import org.jetbrains.exposed.sql.update
import ru.darkrpmc.*
import ru.darkrpmc.data.database.*

/*
 * Данные игроков можно классифицировать по 3 уровням:
 * 1. Данные, хранимые в пределах 1 сессии игрока:
 *    - Профессия и её источник
 *    - Когда в последний раз был ограблен
 *    - Блоки игрока
 * 2. Данные, хранимые в пределах 1 аптайма:
 *    - Ежедневный кейс
 *    - Последняя смена профессии
 * 3. Данные, хранимые на постоянной основе. Их 2 категории:
 *    1. Данные аккаунта. Общие для всех игровых серверов:
 *       - Пароль
 *       - Регистрационный IP
 *       - Последний IP
 *       - Донат валюта
 *       и т.д.
 *    2. Данные индивидуальные для каждого игрового сервера:
 *       - Баланс
 *       - Последнее местоположение
 *       - Последнее появление
 *       и т.д.
 */

val Player.session: InSessionData
    get() {
        val potentialData = sessionData[name]
        if (potentialData != null) return potentialData

        logger.severe("Для игрока $name отсутствуют запрошенные сессионные данные")
        val newData = InSessionData(this)
        sessionData[name] = newData
        return newData
    }

var Player.profession
    get() = session.profession
    set(profession) {
        session.profession = profession
        updateGUI()
    }

val Player.salary get() = profession.info.salary // TODO бонус

val Player.uptime get() = uptimeDataOrDefault(name)

fun uptimeDataOrDefault(name: String): UptimeData {
    val potentialData = uptimeData[name]
    if (potentialData != null) return potentialData

    logger.severe("Для игрока $name отсутствуют запрошенные аптайм-данные")
    val newData = UptimeData()
    uptimeData[name] = newData
    return newData
}

fun accountOf(name: String): Account? {
    val cache = accounts[name]
    return if (isOnline(name)) {
        if (cache == null) {
            logger.severe("В памяти отсутствует аккаунт игрока в сети $name. Загрузка из БД...")
            printStackTrace()
            val acc = loadAccount(name)
            if (acc != null) accounts[name] = acc
            acc
        } else cache
    } else {
        if (cache == null) loadAccount(name)
        else {
            logger.severe("В памяти кэширован аккаунт игрока не в сети $name. Выгрузка в БД...")
            printStackTrace()
            accounts -= name
            saveAccount(name, cache)
            cache
        }
    }
}

private fun printStackTrace() {
    Thread.currentThread().stackTrace.drop(2).forEach { element ->
        logger.severe("${element.className}.${element.methodName}(${element.fileName}:${element.lineNumber})")
    }
}

val Player.account get() = accountOf(name)

val Player.serverData get() = serverDataOf(name)

fun serverDataOrDefault(name: String): PerServerData {
    var data = serverDataOf(name)
    if (data == null) {
        data = PerServerData()
        if (isOnline(name)) {
            logger.severe("В памяти и БД отсутствуют данные игрока в сети $name. Создан новый экземпляр")
            perServerData[name] = data
        }
    }
    return data
}

fun serverDataOf(name: String): PerServerData? {
    val cache = perServerData[name]
    return if (isOnline(name)) {
        if (cache == null) {
            logger.severe("В памяти отсутствуют данные игрока в сети $name. Загрузка из БД...")
            val data = loadPerServerData(name)
            if (data != null) perServerData[name] = data
            data
        } else cache
    } else {
        if (cache == null) loadPerServerData(name)
        else {
            logger.severe("В памяти кэшированы данные игрока не в сети $name. Выгрузка в БД...")
            perServerData -= name
            savePerServerData(name, cache)
            cache
        }
    }
}

var Player.balanceCents
    get() = getBalanceOf(name)
    set(cents) {
        setBalanceOf(name, cents ?: 0)
        updateGUI()
    }

var Player.balance
    get() = balanceCents?.div(100.0)
    set(amount) {
        balanceCents = amount?.times(100)?.toLong()
    }

fun getBalanceOf(name: String) = serverDataOf(name)?.balanceCents
fun setBalanceOf(name: String, balance: Long) {
    val cache = perServerData[name]
    if (isOnline(name)) {
        if (cache == null) {
            logger.severe("В памяти отсутствуют данные игрока в сети $name. Загрузка из БД...")
            loadPerServerData(name)?.let {
                it.balanceCents = balance
                perServerData[name] = it
            }
        } else cache.balanceCents = balance
    } else {
        if (cache == null) asyncTransaction {
            if (PerServerDataTable.update({ PerServerDataTable.selector(name) }) {
                    it[balanceColumn] = balance
                } == 0) logger.severe("Попытка изменения баланса игрока не в сети, отсутствующего в БД")
        } else {
            logger.severe("В памяти кэшированы данные игрока не в сети $name. Выгрузка в БД...")
            perServerData -= name
            cache.balanceCents = balance
            savePerServerData(name, cache)
        }
    }
}

var Player.rank
    get() = getRankOf(name)
    set(rank) = setRankOf(name, rank ?: Rank.USER)
val Player.rankOrUser get() = rank ?: Rank.USER

fun getRankOf(name: String) = accountOf(name)?.rank
fun setRankOf(name: String, rank: Rank) {
    val cache = accounts[name]
    val player = server.getPlayer(name)
    if (player != null) {
        if (cache == null) {
            logger.severe("В памяти отсутствуют данные игрока в сети $name. Загрузка из БД...")
            loadAccount(name)?.let {
                it.rank = rank
                accounts[name] = it
            }
        } else cache.rank = rank
        player.updateTabName()
    } else {
        if (cache == null) asyncTransaction {
            if (AccountsTable.update({ AccountsTable.selector(name) }) {
                    it[rankColumn] = rank
                } == 0) logger.severe("Попытка изменения роли игрока не в сети, отсутствующего в БД")
        } else {
            logger.severe("В памяти кэшированы данные игрока не в сети $name. Выгрузка в БД...")
            perServerData -= name
            cache.rank = rank
            saveAccount(name, cache)
        }
    }
}

var Player.donateGroup
    get() = getDonateGroupOf(name)
    set(donateGroup) = setDonateGroupOf(name, donateGroup ?: DonateGroup.USER)
val Player.donateGroupOrUser get() = donateGroup ?: DonateGroup.USER

fun getDonateGroupOf(name: String) = accountOf(name)?.donateGroup
fun setDonateGroupOf(name: String, donateGroup: DonateGroup) {
    val cache = accounts[name]
    val player = server.getPlayer(name)
    if (player != null) {
        if (cache == null) {
            logger.severe("В памяти отсутствуют данные игрока в сети $name. Загрузка из БД...")
            loadAccount(name)?.let {
                it.donateGroup = donateGroup
                accounts[name] = it
            }
        } else cache.donateGroup = donateGroup
        player.updateTabName()
    } else {
        if (cache == null) asyncTransaction {
            if (AccountsTable.update({ AccountsTable.selector(name) }) {
                    it[donateGroupColumn] = donateGroup
                } == 0) logger.severe("Попытка изменения роли игрока не в сети, отсутствующего в БД")
        } else {
            logger.severe("В памяти кэшированы данные игрока не в сети $name. Выгрузка в БД...")
            perServerData -= name
            cache.donateGroup = donateGroup
            saveAccount(name, cache)
        }
    }
}

fun Player.writeOffCents(amount: Long) = balanceCents.let {
    if (it == null) false
    else {
        if (amount > it) false
        else {
            balanceCents = it - amount
            true
        }
    }
}

fun Player.writeOff(amount: Int) = writeOffCents((amount * 100).toLong())

fun Player.giveCents(amount: Long) {
    balanceCents = (balanceCents ?: 0) + amount
}

fun Player.give(amount: Long) = giveCents(amount * 100)