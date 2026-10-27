package ru.darkrpmc.weapon

import net.kyori.adventure.text.Component
import org.bukkit.persistence.PersistentDataAdapterContext
import org.bukkit.persistence.PersistentDataType
import ru.darkrpmc.*
import ru.darkrpmc.text.jsonSerializer


class CustomFirearm(
    override val displayName: Component,
    override val startDamage: Float,
    override val damageLossStart: Float,
    override val damageLossEnd: Float,
    override val endDamage: Float,
    override val bullets: Short,
    override val headshotMultiplier: Float,
    override val fireRate: Short,
    override val coolingTime: Float,
    override val spreadLensDistance: Float,
    override val spreadLensRadius: Float,
    override val smallSpreadLensRadius: Float,
    override val scope: Boolean,
    override val magazine: Short,
    override val recharge: Float,
    override val singleRecharging: Boolean,
    override val ammoType: AmmoType,
    override val deploy: Float,
    override val rayLength: Float,
    override val function: String?
) : Firearm {
    override fun serialize(ctx: PersistentDataAdapterContext) = ctx.newPersistentDataContainer().apply {
        set(displayNameKey, PersistentDataType.STRING, jsonSerializer.serialize(displayName))
        set(startDamageKey, PersistentDataType.FLOAT, startDamage)
        set(damageLossStartKey, PersistentDataType.FLOAT, damageLossStart)
        set(damageLossEndKey, PersistentDataType.FLOAT, damageLossEnd)
        set(endDamageKey, PersistentDataType.FLOAT, endDamage)
        set(bulletsKey, PersistentDataType.SHORT, bullets)
        set(headshotKey, PersistentDataType.FLOAT, headshotMultiplier)
        set(fireRateKey, PersistentDataType.SHORT, fireRate)
        set(coolingKey, PersistentDataType.FLOAT, coolingTime)
        set(spreadLensDistanceKey, PersistentDataType.FLOAT, spreadLensDistance)
        set(spreadLensRadiusKey, PersistentDataType.FLOAT, spreadLensRadius)
        set(smallSpreadLensRadiusKey, PersistentDataType.FLOAT, smallSpreadLensRadius)
        set(scopeKey, PersistentDataType.BOOLEAN, scope)
        set(magazineKey, PersistentDataType.SHORT, magazine)
        set(rechargeKey, PersistentDataType.FLOAT, recharge)
        set(singleRechargingKey, PersistentDataType.BOOLEAN, singleRecharging)
        set(ammoTypeKey, PersistentDataType.STRING, ammoType.name)
        set(deployKey, PersistentDataType.FLOAT, deploy)
        set(rayLengthKey, PersistentDataType.FLOAT, rayLength)
        if (function != null) set(functionKey, PersistentDataType.STRING, function)
    }
}