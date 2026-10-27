package ru.darkrpmc.data.persistent

import org.bukkit.NamespacedKey
import org.bukkit.persistence.PersistentDataType
import org.bukkit.persistence.PersistentDataContainer as PDC

abstract class PDT<P : Any, C : Any> : PersistentDataType<P, C> {
    protected abstract val key: NamespacedKey
    fun write(pdc: PDC, value: C) = pdc.set(key, this, value)
    fun read(pdc: PDC) = if (pdc.has(key, this)) pdc.get(key, this) else null
}