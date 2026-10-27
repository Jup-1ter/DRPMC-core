package ru.darkrpmc.inventory.menu

import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.InventoryHolder

abstract class Menu(rows: Int, title: Component) : InventoryHolder {
    protected val inv = Bukkit.createInventory(@Suppress("LeakingThis") this, rows * 9, title)
    abstract fun handleClick(slot: Int, clickType: ClickType)
    override fun getInventory() = inv
}