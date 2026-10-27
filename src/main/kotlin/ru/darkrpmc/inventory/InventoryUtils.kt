package ru.darkrpmc.inventory

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.PlayerInventory
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.inventory.meta.SkullMeta
import ru.darkrpmc.data.config.Settings
import ru.darkrpmc.data.persistent.*
import ru.darkrpmc.server
import ru.darkrpmc.text.colored
import java.util.*

/** Функция для создания предмета с заданными параметрами
 * @param material Материал предмета
 * @param itemName Отображаемое имя предмета
 * @param model Значение custom_model_data
 * @param lore Описание предмета
 * @return Новый предмет
 */
fun item(
    material: Material,
    itemName: Component,
    stackSize: Int? = null,
    model: NamespacedKey? = null,
    lore: List<Component>? = null,
    flags: CustomFlags? = null,
    invAction: InventoryAction? = null,
    blockInteractAction: BlockInteractAction? = null,
    entityInteractAction: EntityInteractAction? = null,
    attackAction: AttackAction? = null,
    hideTooltip: Boolean = false
) = ItemStack.of(material).apply {
    editMeta {
        it.itemName(itemName)
        it.setMaxStackSize(stackSize)
        it.itemModel = model
        it.lore(lore)
        val pdc = it.persistentDataContainer
        if (flags != null) FlagsDataType.write(pdc, flags)
        if (invAction != null) InvActionDataType.write(pdc, invAction)
        if (blockInteractAction != null) BlockInteractActionDataType.write(pdc, blockInteractAction)
        if (entityInteractAction != null) EntityInteractActionDataType.write(pdc, entityInteractAction)
        if (attackAction != null) AttackActionDataType.write(pdc, attackAction)
        it.isHideTooltip = hideTooltip
    }
}

fun PlayerInventory.setBasicItems() = run {
    setItem(8, menuItem)
    setItem(7, backpackItem)
    setItem(6, passportItem)
}

// @formatter:off
val menuItem = item(
    Material.HEART_OF_THE_SEA, "Меню".colored(NamedTextColor.DARK_AQUA), 1, null, null,
    CustomFlags(true, true), InventoryAction.MENU, BlockInteractAction.MENU, attackAction = AttackAction.MENU
)

val passportItem = item(
    Material.BRICK, "Паспорт".colored(NamedTextColor.GOLD), 1,
    flags = CustomFlags(true, true), entityInteractAction = EntityInteractAction.PASSPORT
)

private val backpackUUID = UUID.fromString("81281281-2812-8128-1281-281281281281")
val backpackItem = item(
    Material.PLAYER_HEAD, "Рюкзак".colored(NamedTextColor.AQUA), 1, null, null,
    CustomFlags(true, true), InventoryAction.BACKPACK, BlockInteractAction.BACKPACK
).apply {
    editMeta {
        (it as SkullMeta).playerProfile = server.createProfile(backpackUUID).apply {
            setTextures(textures.apply { skin = Settings.Items.backpackTexture })
        }
    }
}

operator fun ItemStack.times(x: Int) = asQuantity(x)

// operator fun ItemStack.times(x: Long) = asQuantity(x.toInt())
val ItemStack.flags get() = itemMeta.flags
val ItemMeta?.flags
    get(): CustomFlags {
        return FlagsDataType.read((this ?: return CustomFlags()).persistentDataContainer) ?: return CustomFlags()
    }


fun distributeSymmetrically(items: List<ItemStack>) = when (items.size) {
    1 -> listOf(4)
    2 -> listOf(3, 5) // Два предмета - слева и справа от центра
    3 -> listOf(2, 4, 6) // Три предмета - равномерно по центру
    4 -> listOf(1, 3, 5, 7) // Четыре предмета
    5 -> listOf(0, 2, 4, 6, 8) // Пять предметов - полностью симметрично
    6 -> listOf(0, 2, 3, 5, 6, 8) // Шесть предметов
    7 -> listOf(0, 1, 2, 4, 6, 7, 8) // Семь предметов
    8 -> listOf(0, 1, 2, 3, 5, 6, 7, 8) // Восемь предметов
    9 -> listOf(0, 1, 2, 3, 4, 5, 6, 7, 8) // Девять предметов - полностью заполнено
    else -> throw IllegalArgumentException("Список должен содержать не более 9 предметов.")
}.zip(items).toMap()