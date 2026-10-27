package ru.darkrpmc.weapon

import net.kyori.adventure.text.Component
import org.bukkit.persistence.PersistentDataAdapterContext
import org.bukkit.persistence.PersistentDataContainer

interface Firearm {
    val displayName: Component
    val startDamage: Float
    val damageLossStart: Float
    val damageLossEnd: Float
    val endDamage: Float
    val bullets: Short
    val headshotMultiplier: Float
    val fireRate: Short
    val coolingTime: Float
    val spreadLensDistance: Float
    val spreadLensRadius: Float
    val smallSpreadLensRadius: Float
    val scope: Boolean
    val magazine: Short
    val recharge: Float
    val singleRecharging: Boolean
    val ammoType: AmmoType
    val deploy: Float
    val rayLength: Float
    val function: String?

    fun serialize(ctx: PersistentDataAdapterContext): PersistentDataContainer
}