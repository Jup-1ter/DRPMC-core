package ru.darkrpmc.professions

/**
 * Перечисление способов получения профессии.
 * @property SYSTEM профессия была установлена плагином
 * @property ADMIN профессия была выдана администратором
 * @property TAKEN игрок самостоятельно выбрал профессию
 * @property DEMOTE игрок был уволен
 */
enum class ProfessionSource {
    SYSTEM,
    ADMIN,
    TAKEN,
    DEMOTE
}