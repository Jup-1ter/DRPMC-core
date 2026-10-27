package ru.darkrpmc.data.persistent

import org.bukkit.persistence.*
import ru.darkrpmc.*
import ru.darkrpmc.text.jsonSerializer
import ru.darkrpmc.weapon.*

object FirearmDataType : PDT<PersistentDataContainer, Firearm>() {
    override val key = firearmKey
    override fun getPrimitiveType() = PersistentDataContainer::class.java
    override fun getComplexType() = Firearm::class.java

    override fun toPrimitive(complex: Firearm, ctx: PersistentDataAdapterContext) = complex.serialize(ctx)

    override fun fromPrimitive(primitive: PersistentDataContainer, ctx: PersistentDataAdapterContext) =
        if (primitive.has(boundKey, PersistentDataType.STRING)) StandardFirearm(
            try {
                StandardWeapon.valueOf(primitive.getOrDefault(boundKey, PersistentDataType.STRING, "G17"))
            } catch (e: IllegalArgumentException) {
                StandardWeapon.G17
            }
        )
        else CustomFirearm(
            jsonSerializer.deserialize(
                primitive.getOrDefault(displayNameKey, PersistentDataType.STRING, "{'text':'некорректное оружие'}")
            ),
            primitive.getOrDefault(startDamageKey, PersistentDataType.FLOAT, 1f),
            primitive.getOrDefault(damageLossStartKey, PersistentDataType.FLOAT, 1f),
            primitive.getOrDefault(damageLossEndKey, PersistentDataType.FLOAT, 1f),
            primitive.getOrDefault(endDamageKey, PersistentDataType.FLOAT, 1f),
            primitive.getOrDefault(bulletsKey, PersistentDataType.SHORT, 1),
            primitive.getOrDefault(headshotKey, PersistentDataType.FLOAT, 1f),
            primitive.getOrDefault(fireRateKey, PersistentDataType.SHORT, 120),
            primitive.getOrDefault(coolingKey, PersistentDataType.FLOAT, 1f),
            primitive.getOrDefault(spreadLensDistanceKey, PersistentDataType.FLOAT, 3f),
            primitive.getOrDefault(spreadLensRadiusKey, PersistentDataType.FLOAT, 1f),
            primitive.getOrDefault(smallSpreadLensRadiusKey, PersistentDataType.FLOAT, 1f),
            primitive.getOrDefault(scopeKey, PersistentDataType.BOOLEAN, false),
            primitive.getOrDefault(magazineKey, PersistentDataType.SHORT, 30),
            primitive.getOrDefault(rechargeKey, PersistentDataType.FLOAT, 2f),
            primitive.getOrDefault(singleRechargingKey, PersistentDataType.BOOLEAN, false),
            AmmoType.valueOf(primitive.getOrDefault(ammoTypeKey, PersistentDataType.STRING, AmmoType.RIFLE.name)),
            primitive.getOrDefault(deployKey, PersistentDataType.FLOAT, 1f),
            primitive.getOrDefault(rayLengthKey, PersistentDataType.FLOAT, 400f),
            primitive.get(functionKey, PersistentDataType.STRING)
        )
}