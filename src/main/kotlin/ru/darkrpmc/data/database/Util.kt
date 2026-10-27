package ru.darkrpmc.data.database

import org.bukkit.configuration.file.YamlConfiguration
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.statements.UpdateBuilder
import org.jetbrains.exposed.sql.transactions.transaction
import ru.darkrpmc.*
import ru.darkrpmc.data.*
import ru.darkrpmc.data.config.*
import java.util.concurrent.Executors

private val dbThread = Executors.newSingleThreadExecutor()
var stop = false
lateinit var serverID: String

fun setupDB() {
    if (!plugin.dataFolder.resolve("db config.yml").exists()) plugin.saveResource("db config.yml", false)
    val dbCfg = YamlConfiguration.loadConfiguration(plugin.dataFolder.resolve("db config.yml"))
    val dbConfigProblems = TreeNode("db config")

    val url = dbCfg.getString("url", dbConfigProblems, false)
    val user = dbCfg.getString("user", dbConfigProblems, false)
    val password = dbCfg.getString("password", dbConfigProblems, false, forceString = true)
    val sid = dbCfg.getString("server-id", dbConfigProblems, false, forceString = true)
    val autoSavePeriod = dbCfg.getShort("auto-save-period", dbConfigProblems)

    if (url == null || sid == null || autoSavePeriod == null || user == null || password == null) {
        logger.severe("Не удалось загрузить параметры базы данных:\n$dbConfigProblems")
    } else {
        serverID = sid
        try {
            Database.connect(url, user = user, password = password)
            transaction { SchemaUtils.create(AccountsTable, PerServerDataTable) }
            if (autoSavePeriod > 0)
                scheduler.runTaskTimer(plugin, ::savePlayersDataAsync, autoSavePeriod * 20L, autoSavePeriod * 20L)
            return
        } catch (e: ClassNotFoundException) {
            logger.severe("Не удалось подключиться к базе данных: Не найден класс драйвера ${e.message}")
        } catch (e: Exception) {
            logger.severe("Не удалось подключиться к базе данных: $e")
        }
    }
    stop = true
    server.shutdown()
}

fun asyncTransaction(db: Database? = null, statement: Transaction.() -> Any) =
    dbThread.execute { transaction(db, statement) }

fun loadAccount(name: String) = transaction {
    AccountsTable.selectAll().where(AccountsTable.selector(name)).singleOrNull()?.let(::Account)
}

fun loadPerServerData(name: String) = transaction {
    PerServerDataTable.selectAll().where(PerServerDataTable.selector(name)).singleOrNull()?.let(::PerServerData)
}

fun saveAccount(name: String, account: Account) = try {
    transaction {
        fun mainInfo(t: AccountsTable, st: UpdateBuilder<Int>) {
            st[t.regIPColumn] = account.regIP
            st[t.lastIPColumn] = account.lastIP
            st[t.lastSeenColumn] = account.lastSeen
            st[t.hadModColumn] = account.hadMod
            st[t.firstHWIDColumn] = account.firstHWID
            st[t.rankColumn] = account.rank
            st[t.donateGroupColumn] = account.donateGroup
        }
        if (AccountsTable.update({ AccountsTable.selector(name) }, body = ::mainInfo) == 0) AccountsTable.insert {
            it[nameColumn] = name
            mainInfo(this, it)
        }
    }
} catch (e: Exception) {
    logger.severe("Не удалось заменить данные игрока $name: $e")
}

fun savePerServerData(name: String, data: PerServerData) = try {
    transaction {
        fun mainInfo(t: PerServerDataTable, st: UpdateBuilder<Int>) {
            st[t.lastSeenColumn] = data.lastSeen
            st[t.balanceColumn] = data.balanceCents
            st[t.backpackColumn] = data.backpack
            st[t.vaultColumn] = data.vault
            st[t.backpackLvlColumn] = data.backpackLvl
            st[t.vaultLvlColumn] = data.vaultLvl
            st[t.invLvlColumn] = data.invLvl
        }
        if (PerServerDataTable.update({ PerServerDataTable.selector(name) }, body = ::mainInfo) == 0)
            PerServerDataTable.insert {
                it[nameColumn] = name
                it[serverColumn] = serverID
                mainInfo(this, it)
            }
    }
} catch (e: Exception) {
    logger.severe("Не удалось заменить данные игрока $name: $e")
}

fun savePlayersDataAsync() = dbThread.execute(::savePlayersData)

fun savePlayersData() {
    accounts.forEach(::saveAccount)
    perServerData.forEach(::savePerServerData)
}