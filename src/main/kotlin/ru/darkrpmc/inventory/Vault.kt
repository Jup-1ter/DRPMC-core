package ru.darkrpmc.inventory

import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.inventory.InventoryHolder
import org.bukkit.inventory.ItemStack
import ru.darkrpmc.data.serverData
import ru.darkrpmc.logger
import ru.darkrpmc.text.colored

private val itemBuffer = hashMapOf<String, ArrayList<ItemStack>>()

class Vault(val player: Player) : InventoryHolder {
    private val inventory = Bukkit.createInventory(this, 9 * 3, "Хранилище".colored(NamedTextColor.DARK_AQUA))
    override fun getInventory() = inventory

    init {
        val sd = player.serverData
        if (sd == null) {
            logger.severe("Не удалось открыть хранилище для ${player.name} т.к. отсутствует ServerData")
        } else {
            player.updateVltBlock(inventory)
            val vltContent = sd.vault
            val size = inventory.size
            val blocked = player.blockedVltSlots

            val iterator = vltContent.iterator()
            while (iterator.hasNext()) {
                val (slot, item) = iterator.next()
                if (slot < blocked || slot >= size) {
                    itemBuffer.getOrPut(player.name) { arrayListOf() }.add(item)
                    iterator.remove() // Удаляем из исходной карты
                }
            }

            // Заполнение инвентаря оставшимися элементами
            vltContent.forEach(inventory::setItem)

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