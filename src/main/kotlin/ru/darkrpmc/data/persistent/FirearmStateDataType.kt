package ru.darkrpmc.data.persistent

import org.bukkit.NamespacedKey
import org.bukkit.persistence.*
import ru.darkrpmc.plugin
import ru.darkrpmc.weapon.FirearmState
import kotlin.random.Random

private val numberKey = NamespacedKey(plugin, "serial-number")
private val ammoKey = NamespacedKey(plugin, "ammo")
private val lastShotKey = NamespacedKey(plugin, "last-shot")

object FirearmStateDataType : PDT<PersistentDataContainer, FirearmState>() {
    override val key = NamespacedKey(plugin, "firearm-state")
    override fun getPrimitiveType() = PersistentDataContainer::class.java
    override fun getComplexType() = FirearmState::class.java

    override fun toPrimitive(complex: FirearmState, context: PersistentDataAdapterContext) = context
        .newPersistentDataContainer().apply {
            set(numberKey, PersistentDataType.LONG, complex.serialNumber)
            set(ammoKey, PersistentDataType.SHORT, complex.ammo)
            set(lastShotKey, PersistentDataType.LONG, complex.lastShot)
        }

    override fun fromPrimitive(primitive: PersistentDataContainer, context: PersistentDataAdapterContext) =
        FirearmState(
            primitive.getOrDefault(numberKey, PersistentDataType.LONG, Random.nextLong()),
            primitive.getOrDefault(ammoKey, PersistentDataType.SHORT, 0),
            primitive.getOrDefault(lastShotKey, PersistentDataType.LONG, 0)
        )
}