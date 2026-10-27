package ru.darkrpmc.data.persistent

import org.bukkit.NamespacedKey
import ru.darkrpmc.inventory.InventoryAction
import ru.darkrpmc.plugin
import org.bukkit.persistence.PersistentDataAdapterContext as PDac

object InvActionDataType : PDT<String, InventoryAction>() {
    override val key = NamespacedKey(plugin, "inv-action")
    override fun getPrimitiveType() = String::class.java
    override fun getComplexType() = InventoryAction::class.java
    override fun fromPrimitive(primitive: String, context: PDac) = InventoryAction.valueOf(primitive)
    override fun toPrimitive(complex: InventoryAction, context: PDac) = complex.name
}