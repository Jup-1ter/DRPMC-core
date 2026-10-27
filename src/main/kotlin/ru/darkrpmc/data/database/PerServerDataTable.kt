package ru.darkrpmc.data.database

import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.CurrentDateTime
import org.jetbrains.exposed.sql.javatime.datetime

/**
 *  Таблица данных игроков
 *
 * @property serverColumn колонка id сервера, к которому относятся данные
 * @property nameColumn колонка имени игрока, которому принадлежат данные
 * @property lastSeenColumn колонка последнего появления игрока на игровом сервере
 * @property balanceColumn колонка баланса игрока на данном игровом сервере
 * @property backpackColumn колонка содержимого рюкзака игрока
 * @property vaultColumn колонка содержимого хранилища игрока
 * @property backpackLvlColumn колонка уровня рюкзака игрока
 * @property vaultLvlColumn колонка уровня хранилища игрока
 * @property invLvlColumn колонка уровня инвентаря игрока
 *
 * @property primaryKey Первичный ключ таблицы. Представлен комбинацией колонок [serverColumn] и [nameColumn]
 */
object PerServerDataTable : Table() {
    val serverColumn = varchar("server", 128)
    val nameColumn = reference("name", AccountsTable.nameColumn)
    override val primaryKey = PrimaryKey(serverColumn, nameColumn)

    val lastSeenColumn = datetime("last_seen").defaultExpression(CurrentDateTime)
    val balanceColumn = long("balance").default(10000)
    val backpackColumn = registerColumn("backpack", InventoryColumnType()).nullable()
    val vaultColumn = registerColumn("vault", InventoryColumnType()).nullable()
    val backpackLvlColumn = integer("backpack_lvl").default(0)
    val vaultLvlColumn = integer("vault_lvl").default(0)
    val invLvlColumn = integer("inventory_lvl").default(0)

    fun selector(name: String) = nameColumn eq name
}