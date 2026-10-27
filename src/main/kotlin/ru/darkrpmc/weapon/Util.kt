package ru.darkrpmc.weapon

import net.kyori.adventure.text.format.NamedTextColor.DARK_AQUA
import net.kyori.adventure.text.format.NamedTextColor.GRAY
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.persistence.PersistentDataContainer
import org.bukkit.scheduler.BukkitTask
import ru.darkrpmc.data.persistent.FirearmDataType
import ru.darkrpmc.data.persistent.FirearmStateDataType
import ru.darkrpmc.inventory.CustomFlags
import ru.darkrpmc.inventory.item
import ru.darkrpmc.logger
import ru.darkrpmc.text.colored

/** Верхний порог времени между кликами при автоматической стрельбе */
const val THRESHOLD = 220

val automaticTasks = hashMapOf<String, BukkitTask>()
val lastRMBs = hashMapOf<String, Long>()
val recharges = hashMapOf<String, Pair<ItemStack, BukkitTask>>()

val projectile = ItemStack.of(Material.NETHER_STAR)
    .apply { editMeta { it.itemName("Патрон".colored(GRAY)) } }

val buffer = hashMapOf<String, ItemStack>()

fun Player.returnItem() {
    inventory.removeItemAnySlot(scopeItem)
    val buffered = buffer.remove(name) ?: return
    if (!inventory.itemInOffHand.isEmpty) logger.warning("Инвентарь игрока $name перезаписан")
    inventory.setItemInOffHand(buffered)
}

val scopeItem =
    item(Material.SPYGLASS, "Прицел".colored(DARK_AQUA), flags = CustomFlags(true, true), hideTooltip = true)

fun ItemStack.checkSerialNumber(number: Long): Boolean {
    return FirearmStateDataType.read((itemMeta ?: return false).persistentDataContainer)?.serialNumber == number
}

fun ItemStack.isTheSameWeapon(weapon: ItemStack): Boolean {
    return checkSerialNumber(
        FirearmStateDataType.read((weapon.itemMeta ?: return false).persistentDataContainer)?.serialNumber ?: 0
    )
}

val ItemStack.firearm get() = itemMeta.firearm
val ItemMeta?.firearm get() = this?.persistentDataContainer?.firearm

val ItemStack.state get() = itemMeta.state
val ItemMeta?.state get() = this?.persistentDataContainer?.state

val PersistentDataContainer.firearm get() = FirearmDataType.read(this)
val PersistentDataContainer.state get() = FirearmStateDataType.read(this)