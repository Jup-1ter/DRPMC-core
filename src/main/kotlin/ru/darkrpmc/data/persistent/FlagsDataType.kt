package ru.darkrpmc.data.persistent

import org.bukkit.NamespacedKey
import ru.darkrpmc.inventory.CustomFlags
import ru.darkrpmc.plugin
import org.bukkit.persistence.PersistentDataAdapterContext as PDac

@Suppress("PLATFORM_CLASS_MAPPED_TO_KOTLIN")
object FlagsDataType : PDT<Integer, CustomFlags>() {
    override val key = NamespacedKey(plugin, "flags")
    override fun getPrimitiveType() = Integer::class.java
    override fun getComplexType() = CustomFlags::class.java

    override fun fromPrimitive(primitive: Integer, context: PDac) = CustomFlags(
        (primitive.toInt() and 1) == 1,
        (primitive.toInt() shr 1 and 1) == 1,
        (primitive.toInt() shr 2 and 1) == 1
    )

    override fun toPrimitive(complex: CustomFlags, context: PDac) =
        (complex.immobile.i + (complex.invRestricted.i shl 1) + (complex.profession.i shl 2)) as Integer

    private val Boolean.i get() = if (this) 1 else 0
}