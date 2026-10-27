package ru.darkrpmc.professions

import net.kyori.adventure.text.format.NamedTextColor.GOLD
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import ru.darkrpmc.text.colored
import ru.darkrpmc.weapon.AmmoType

/** Абстрактный класс, описывающий общие данные о профессии игрока
 * @property info Базовая информация о профессии
 * @property source Способ получения профессии
 * @property obtained Момент приобретения профессии
 * @property shopItems Предметы, доступные данной профессии для покупки
 * @property startItems Предметы, выдаваемые игроку при возрождении
 * @see ProfessionInfo */
sealed class Profession(
    val info: ProfessionInfo,
    val source: ProfessionSource,
    val player: Player
) {
    val obtained = System.currentTimeMillis()

    open val shopItems = listOf(
        AmmoType.PISTOL.item.asQuantity(15) to 2,
        AmmoType.SUBMACHINE.item.asQuantity(30) to 2,
        AmmoType.RIFLE.item.asQuantity(30) to 2,
        AmmoType.SHOTGUN.item.asQuantity(10) to 2,
        AmmoType.SNIPER.item.asQuantity(5) to 2,
    )
    val sampleItems by lazy {
        shopItems.map { itemPrice ->
            ItemStack.of(itemPrice.first.type).apply {
                itemMeta = itemPrice.first.itemMeta.apply {
                    amount = itemPrice.first.amount
                    lore((lore() ?: emptyList()) + "Цена: $${itemPrice.second}".colored(GOLD))
                }
            }
        }
    }

    open val startItems = emptyList<ItemStack>()
}