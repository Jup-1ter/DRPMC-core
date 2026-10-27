package ru.darkrpmc.weapon

import org.bukkit.persistence.PersistentDataAdapterContext
import org.bukkit.persistence.PersistentDataType
import ru.darkrpmc.boundKey

class StandardFirearm(private val standardWeapon: StandardWeapon): Firearm {
    override val displayName = standardWeapon.displayName
    override val startDamage = standardWeapon.startDamage
    override val damageLossStart = standardWeapon.damageLossStart
    override val damageLossEnd = standardWeapon.damageLossEnd
    override val endDamage = standardWeapon.endDamage
    override val bullets = standardWeapon.bullets
    override val headshotMultiplier = standardWeapon.headshotMultiplier
    override val fireRate = standardWeapon.fireRate
    override val coolingTime = standardWeapon.cooling
    override val spreadLensDistance = standardWeapon.spreadLensDistance
    override val spreadLensRadius = standardWeapon.spreadLensRadius
    override val smallSpreadLensRadius = standardWeapon.smallSpreadLensRadius
    override val scope = standardWeapon.scope
    override val magazine = standardWeapon.magazine
    override val recharge = standardWeapon.recharge
    override val singleRecharging = standardWeapon.singleRecharging
    override val ammoType = standardWeapon.ammoType
    override val deploy = standardWeapon.deploy
    override val rayLength = standardWeapon.rayLength
    override val function = standardWeapon.function

    override fun serialize(ctx: PersistentDataAdapterContext) = ctx.newPersistentDataContainer().apply {
        set(boundKey, PersistentDataType.STRING, standardWeapon.name)
    }
}