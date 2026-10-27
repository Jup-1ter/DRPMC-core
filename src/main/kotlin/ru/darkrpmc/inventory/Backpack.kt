package ru.darkrpmc.inventory

import net.kyori.adventure.text.format.NamedTextColor.DARK_AQUA
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.inventory.InventoryHolder
import org.bukkit.inventory.ItemStack
import ru.darkrpmc.data.serverData
import ru.darkrpmc.logger
import ru.darkrpmc.text.colored

private val itemBuffer = hashMapOf<String, ArrayList<ItemStack>>()

class Backpack(val player: Player) : InventoryHolder {
    private val inventory = Bukkit.createInventory(this, 9 * 3, "Рюкзак".colored(DARK_AQUA))
    override fun getInventory() = inventory

    init {
        val sd = player.serverData
        if (sd == null) {
            logger.severe("Не удалось открыть рюкзак для ${player.name} т.к. отсутствует ServerData")
        } else {
            player.updateBpBlock(inventory)
            val backpackContent = sd.backpack
            val size = inventory.size
            val blocked = player.blockedBpSlots

            val iterator = backpackContent.iterator()
            while (iterator.hasNext()) {
                val (slot, item) = iterator.next()
                if (slot < blocked || slot >= size) {
                    itemBuffer.getOrPut(player.name) { arrayListOf() }.add(item)
                    iterator.remove() // Удаляем из исходной карты
                }
            }

            // Заполнение инвентаря оставшимися элементами
            backpackContent.forEach(inventory::setItem)

            // Попытка добавить элементы из буфера
            loadBuffer()

            player.openInventory(inventory)
        }
    }

    fun loadBuffer() {
        itemBuffer[player.name] =
            ArrayList(inventory.addItem(*(itemBuffer[player.name] ?: return).toTypedArray()).values)
    }
}