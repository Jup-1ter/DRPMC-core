package ru.darkrpmc.data.persistent

import org.bukkit.NamespacedKey
import ru.darkrpmc.inventory.BlockInteractAction
import ru.darkrpmc.inventory.EntityInteractAction
import ru.darkrpmc.plugin
import org.bukkit.persistence.PersistentDataAdapterContext as PDac

object EntityInteractActionDataType : PDT<String, EntityInteractAction>() {
    override val key = NamespacedKey(plugin, "entity-interact-action")
    override fun getPrimitiveType() = String::class.java
    override fun getComplexType() = EntityInteractAction::class.java
    override fun fromPrimitive(primitive: String, context: PDac) = EntityInteractAction.valueOf(primitive)
    override fun toPrimitive(complex: EntityInteractAction, context: PDac) = complex.name
}