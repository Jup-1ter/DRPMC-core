package ru.darkrpmc.data.persistent

import org.bukkit.NamespacedKey
import ru.darkrpmc.inventory.BlockInteractAction
import ru.darkrpmc.plugin
import org.bukkit.persistence.PersistentDataAdapterContext as PDac

object BlockInteractActionDataType : PDT<String, BlockInteractAction>() {
    override val key = NamespacedKey(plugin, "block-interact-action")
    override fun getPrimitiveType() = String::class.java
    override fun getComplexType() = BlockInteractAction::class.java
    override fun fromPrimitive(primitive: String, context: PDac) = BlockInteractAction.valueOf(primitive)
    override fun toPrimitive(complex: BlockInteractAction, context: PDac) = complex.name
}