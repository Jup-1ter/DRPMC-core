package ru.darkrpmc.data.config

import org.bukkit.Location
import org.bukkit.configuration.ConfigurationSection
import ru.darkrpmc.math.geometry.Point
import ru.darkrpmc.text.окончПорядк
import ru.darkrpmc.world
import ru.darkrpmc.world.regions.CuboidRegion

/**
 * Регулярное выражение для распознавания точки пространства [Point] в строке.
 * Формат: три числа с плавающей точкой, разделённые пробелами.
 */
private val pointRegex = Regex("^(-?\\d+(?:\\.\\d+)?) (-?\\d+(?:\\.\\d+)?) (-?\\d+(?:\\.\\d+)?)$")

/**
 * Регулярное выражение для распознавания положения [Location] в строке.
 * Формат: три числа с плавающей точкой, разделённые пробелами и опционально 2 числа означающие градус поворота.
 */
private val locRegex = Regex(
    "^(-?\\d+(?:\\.\\d+)?) (-?\\d+(?:\\.\\d+)?) (-?\\d+(?:\\.\\d+)?)(?: (-?\\d+(?:\\.\\d+)?) (-?\\d+(?:\\.\\d+)?))?$"
)

/**
 * Получает объект [CuboidRegion] из конфигурационного раздела.
 *
 * @param path Путь к разделу в конфигурации, содержащему параметры `from` и `to`.
 * @param errorContainer Контейнер для сбора ошибок.
 * @param useCfgDefault Если `true`, использовать значения по умолчанию из конфигурации.
 * @param default Значение по умолчанию, если координаты не найдены.
 * @return Объект [CuboidRegion] или `null`, если координаты не найдены и нет значения по умолчанию.
 */
fun ConfigurationSection.getCuboidRegion(
    path: String,
    errorContainer: TreeNode,
    useCfgDefault: Boolean = true,
    default: CuboidRegion? = null
): CuboidRegion? {

    val section = getConfigurationSection(path, errorContainer, useCfgDefault) ?: return default

    val from = section.getPoint("from", errorContainer, useCfgDefault)
    val to = section.getPoint("to", errorContainer, useCfgDefault)

    return if (from != null && to != null) CuboidRegion(from, to) else default
}

/**
 * Получает объект [Location].
 *
 * @param path Путь к строке в конфигурации, содержащей координаты.
 * @param errorContainer Контейнер для сбора ошибок.
 * @param useCfgDefault Если `true`, использовать значения по умолчанию из конфигурации.
 * @param default Значение по умолчанию, если значение по заданному [path] некорректно.
 * @return Объект [Location] или `null`, если позиция не найдена и не указан [default].
 */
fun ConfigurationSection.getLocation(
    path: String,
    errorContainer: TreeNode,
    useCfgDefault: Boolean = true,
    default: Location? = null
) = parseLocation(getString(path, errorContainer, useCfgDefault, locRegex)) ?: getDefaultLocation(
    path, useCfgDefault, default
)

/**
 * Получает объект [Point].
 *
 * @param path Путь к строке в конфигурации, содержащей координаты.
 * @param errorContainer Контейнер для сбора ошибок.
 * @param useCfgDefault Если `true`, использовать значения по умолчанию из конфигурации.
 * @param default Значение по умолчанию, если значение по заданному [path] некорректно.
 * @return Объект [Point] или `null`, если позиция не найдена и не указан [default].
 */
fun ConfigurationSection.getPoint(
    path: String,
    errorContainer: TreeNode,
    useCfgDefault: Boolean = true,
    default: Point? = null
) = parsePoint(getString(path, errorContainer, useCfgDefault, pointRegex)) ?: getDefaultPoint(
    path, useCfgDefault, default
)

/**
 * Получает значение по умолчанию для объекта [Location].
 *
 * @param path Путь к строке в конфигурации, содержащей координаты.
 * @param useCfgDefault Если `true`, использовать значения по умолчанию из конфигурации.
 * @param default Предлагаемое значение по умолчанию.
 * @return Объект [Location] или `null`, если позиция не найдена и не указан [default].
 */
private fun ConfigurationSection.getDefaultLocation(
    path: String,
    useCfgDefault: Boolean,
    default: Location?
): Location? {
    if (default != null) return default
    if (!useCfgDefault) return null

    val rawDefault = defaultSection?.getString(path) ?: return null
    return parseLocation(rawDefault)
}

/**
 * Получает значение по умолчанию для объекта [Point].
 *
 * @param path Путь к строке в конфигурации, содержащей координаты.
 * @param useCfgDefault Если `true`, использовать значения по умолчанию из конфигурации.
 * @param default Предлагаемое значение по умолчанию.
 * @return Объект [Point] или `null`, если позиция не найдена и не указан [default].
 */
private fun ConfigurationSection.getDefaultPoint(
    path: String,
    useCfgDefault: Boolean,
    default: Point?
): Point? {
    if (default != null) return default
    if (!useCfgDefault) return null

    val rawDefault = defaultSection?.getString(path) ?: return null
    return parsePoint(rawDefault)
}

/**
 * Парсит строку и возвращает объект [Location].
 *
 * @param raw Строка с координатами.
 * @return Объект [Location] или `null`, если строка не соответствует шаблону [locRegex].
 */
private fun parseLocation(raw: String?): Location? {
    return locRegex.find(raw ?: return null)?.destructured?.let { (x, y, z, yaw, pitch) ->
        Location(
            world,
            x.toDouble(),
            y.toDouble(),
            z.toDouble(),
            yaw.toFloatOrNull() ?: 0f,
            pitch.toFloatOrNull() ?: 0f
        )
    }
}

/**
 * Парсит строку и возвращает объект [Point].
 *
 * @param raw Строка с координатами.
 * @return Объект [Point] или `null`, если строка не соответствует шаблону [pointRegex].
 */
private fun parsePoint(raw: String?): Point? {
    if (raw == null) return null

    val matchResult = pointRegex.find(raw) ?: return null
    val (x, y, z) = matchResult.destructured.toList().map(String::toDouble)
    return Point(x, y, z)
}

/**
 * Получает список объектов типа [E].
 *
 * @param path Путь к списку в конфигурации.
 * @param errorContainer Контейнер для сбора ошибок.
 * @param useCfgDefault Если `true`, использовать значения по умолчанию из конфигурации.
 * @param default Значение по умолчанию, если список не найден.
 * @return Список объектов типа [E] или `null`, если список не найден и не указан [default].
 */
@Suppress("UNCHECKED_CAST", "Unused")
inline fun <reified E> ConfigurationSection.getList(
    path: String,
    errorContainer: TreeNode,
    useCfgDefault: Boolean = true,
    default: List<E>? = null
): List<E>? {
    val def = default ?: if (useCfgDefault) defaultSection?.getList(path) as? List<E> else null
    if (!contains(path)) {
        errorContainer += "Отсутствует $path"
        return def
    }
    if (!isList(path)) {
        errorContainer += "$path {${get(path)!!::class.simpleName}} не является списком"
        return def
    }
    val errors = TreeNode(path)
    val list = getList(path) ?: return def
    list.forEachIndexed { i, el ->
        val n = i + 1
        if (el !is E) {
            errors += "$n-${n.окончПорядк()} элемент списка не является ${E::class.simpleName}"
        }
    }
    if (!errors.isEmpty()) {
        errorContainer += errors
        return def
    }
    return list as? List<E> ?: def
}

/**
 * Получает список строк.
 *
 * @param path Путь к списку в конфигурации.
 * @param errorContainer Контейнер для сбора ошибок.
 * @param useCfgDefault Если `true`, использовать значения по умолчанию из конфигурации.
 * @param pattern Регулярное выражение для проверки каждой строки в списке.
 * @param default Значение по умолчанию, если список не найден.
 * @param forceStrings Если `true`, принудительно преобразовывать элементы в строки.
 * @return Список строк или `null`, если список не найден и не указан [default].
 */
fun ConfigurationSection.getStringList(
    path: String,
    errorContainer: TreeNode,
    useCfgDefault: Boolean = true,
    pattern: Regex = Regex(".*"),
    default: List<String>? = null,
    forceStrings: Boolean = false
): List<String>? {
    val def = default ?: if (useCfgDefault) defaultSection?.getStringList(path) else null
    if (!contains(path)) {
        errorContainer += "Отсутствует $path"
        return def
    }
    if (!isList(path)) {
        errorContainer += "$path {${get(path)!!::class.simpleName}} не является списком"
        return def
    }
    val errors = TreeNode(path)
    val result = ArrayList<String>()
    getList(path)!!.forEachIndexed { i, raw ->
        val el = if (forceStrings) raw.toString() else raw
        val start = "${i + 1}-${(i + 1).окончПорядк()} элемент списка ($raw)"
        if (el is String) {
            if (el.matches(pattern)) result.add(el)
            else errors += "$start не соответствует шаблону: ${pattern.pattern}"
        } else errors += "$start {${el!!::class.simpleName}} не является строкой"
    }
    if (!errors.isEmpty()) errorContainer += errors
    return result
}

/**
 * Получает подраздел текущего раздела.
 *
 * @param path Путь к разделу в конфигурации.
 * @param errorContainer Контейнер для сбора ошибок.
 * @param useCfgDefault Если `true`, использовать значения по умолчанию из конфигурации.
 * @param default Значение по умолчанию, если раздел не найден.
 * @return [ConfigurationSection] или `null`, если раздел не найден и не указан [default].
 */
fun ConfigurationSection.getConfigurationSection(
    path: String,
    errorContainer: TreeNode,
    useCfgDefault: Boolean = true,
    default: ConfigurationSection? = null
): ConfigurationSection? {
    val def = default ?: if (useCfgDefault) defaultSection?.getConfigurationSection(path) else null
    return if (!contains(path)) {
        errorContainer += "Отсутствует раздел $path"
        def
    } else if (!isConfigurationSection(path)) {
        errorContainer += "$path {${get(path)!!::class.simpleName}} не является разделом"
        def
    } else getConfigurationSection(path)
}

/**
 * Получает значение типа [Double].
 *
 * @param path Путь к значению в конфигурации.
 * @param errorContainer Контейнер для сбора ошибок.
 * @param useCfgDefault Если `true`, использовать значения по умолчанию из конфигурации.
 * @param min Минимально допустимое значение.
 * @param max Максимально допустимое значение.
 * @param default Значение по умолчанию, если значение не найдено.
 * @return Значение типа [Double] или `null`, если значение не найдено и не указан [default].
 */
@Suppress("Unused") // TODO убрать
fun ConfigurationSection.getDouble(
    path: String,
    errorContainer: TreeNode,
    useCfgDefault: Boolean = true,
    min: Double = Double.NEGATIVE_INFINITY,
    max: Double = Double.POSITIVE_INFINITY,
    default: Double? = null
): Double? {
    val def = default ?: if (useCfgDefault) defaultSection?.getDouble(path) else null
    val d = getDouble(path)
    return if (!contains(path)) {
        errorContainer += "Отсутствует $path"
        def
    } else if (!(isDouble(path) || isInt(path))) {
        errorContainer += "$path {${get(path)!!::class.simpleName}} не является числом"
        def
    } else if (d in min..max) d
    else {
        errorContainer += "Значение $path ($d) находится вне допустимого диапазона: [$min;$max]"
        def
    }
}

/**
 * Получает значение типа [Short].
 *
 * @param path Путь к значению в конфигурации.
 * @param errorContainer Контейнер для сбора ошибок.
 * @param useCfgDefault Если `true`, использовать значения по умолчанию из конфигурации.
 * @param min Минимально допустимое значение.
 * @param max Максимально допустимое значение.
 * @param default Значение по умолчанию, если значение не найдено.
 * @return Значение типа [Short] или `null`, если значение не найдено и не указан [default].
 */
fun ConfigurationSection.getShort(
    path: String,
    errorContainer: TreeNode,
    useCfgDefault: Boolean = true,
    min: Short = Short.MIN_VALUE,
    max: Short = Short.MAX_VALUE,
    default: Short? = null
): Short? {
    val def = default ?: if (useCfgDefault) defaultSection?.getInt(path)?.toShort() else null
    val s = getInt(path)
    return if (!contains(path)) {
        errorContainer += "Отсутствует $path"
        def
    } else if (!isInt(path)) {
        errorContainer += "$path {${get(path)!!::class.simpleName}} не является целым числом"
        return def
    } else if (s in min..max) s.toShort()
    else {
        errorContainer += "Значение $path ($s) находится вне допустимого диапазона: [$min;$max]"
        def
    }
}

/**
 * Получает значение типа [Int].
 *
 * @param path Путь к значению в конфигурации.
 * @param errorContainer Контейнер для сбора ошибок.
 * @param useCfgDefault Если `true`, использовать значения по умолчанию из конфигурации.
 * @param min Минимально допустимое значение.
 * @param max Максимально допустимое значение.
 * @param default Значение по умолчанию, если значение не найдено.
 * @return Значение типа [Int] или `null`, если значение не найдено и не указан [default].
 */
fun ConfigurationSection.getInt(
    path: String,
    errorContainer: TreeNode,
    useCfgDefault: Boolean = true,
    min: Int = Int.MIN_VALUE,
    max: Int = Int.MAX_VALUE,
    default: Int? = null
): Int? {
    val def = default ?: if (useCfgDefault) defaultSection?.getInt(path) else null
    val s = getInt(path)
    return if (!contains(path)) {
        errorContainer += "Отсутствует $path"
        def
    } else if (!isInt(path)) {
        errorContainer += "$path {${get(path)!!::class.simpleName}} не является целым числом"
        return def
    } else if (s in min..max) s
    else {
        errorContainer += "Значение $path ($s) находится вне допустимого диапазона: [$min;$max]"
        def
    }
}

/**
 * Получает значение типа [Long].
 *
 * @param path Путь к значению в конфигурации.
 * @param errorContainer Контейнер для сбора ошибок.
 * @param useCfgDefault Если `true`, использовать значения по умолчанию из конфигурации.
 * @param min Минимально допустимое значение.
 * @param max Максимально допустимое значение.
 * @param default Значение по умолчанию, если значение не найдено.
 * @return Значение типа [Long] или `null`, если значение не найдено и не указан [default].
 */
fun ConfigurationSection.getLong(
    path: String,
    errorContainer: TreeNode,
    useCfgDefault: Boolean = true,
    min: Long = Long.MIN_VALUE,
    max: Long = Long.MAX_VALUE,
    default: Long? = null
): Long? {
    val def = default ?: if (useCfgDefault) defaultSection?.getLong(path) else null
    val s = getLong(path)
    return if (!contains(path)) {
        errorContainer += "Отсутствует $path"
        def
    } else if (!isLong(path) && !isInt(path)) {
        errorContainer += "$path {${get(path)!!::class.simpleName}} не является целым числом"
        return def
    } else if (s in min..max) s
    else {
        errorContainer += "Значение $path ($s) находится вне допустимого диапазона: [$min;$max]"
        def
    }
}

/**
 * Получает строковое значение.
 *
 * @param path Путь к строке в конфигурации.
 * @param errorContainer Контейнер для сбора ошибок.
 * @param useCfgDefault Если `true`, использовать значения по умолчанию из конфигурации.
 * @param pattern Регулярное выражение для проверки строки.
 * @param default Значение по умолчанию, если строка не найдена.
 * @return Строковое значение или `null`, если строка не найдена и не указан [default].
 */
fun ConfigurationSection.getString(
    path: String,
    errorContainer: TreeNode,
    useCfgDefault: Boolean = true,
    pattern: Regex = Regex(".*"),
    default: String? = null,
    forceString: Boolean = false
): String? {
    val def = default ?: if (useCfgDefault) defaultSection?.getString(path) else null
    val s = getString(path)
    return if (!contains(path)) {
        errorContainer += "Отсутствует $path"
        def
    } else if (!(forceString || isString(path))) {
        errorContainer += "$path {${get(path)!!::class.simpleName}} не является строкой"
        def
    } else if (s!!.matches(pattern)) s
    else {
        errorContainer += "Значение $path ($s) не соответствует шаблону: ${pattern.pattern}"
        def
    }
}