package ru.darkrpmc.data.persistent

import org.bukkit.NamespacedKey
import ru.darkrpmc.inventory.AttackAction
import ru.darkrpmc.plugin
import org.bukkit.persistence.PersistentDataAdapterContext as PDac

object AttackActionDataType : PDT<String, AttackAction>() {
    override val key = NamespacedKey(plugin, "attack-action")
    override fun getPrimitiveType() = String::class.java
    override fun getComplexType() = AttackAction::class.java
    override fun fromPrimitive(primitive: String, context: PDac) = AttackAction.valueOf(primitive)
    override fun toPrimitive(complex: AttackAction, context: PDac) = complex.name
}