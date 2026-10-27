package ru.darkrpmc.weapon

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor.GRAY
import net.minecraft.core.particles.BlockParticleOption
import net.minecraft.core.particles.ParticleType
import net.minecraft.core.registries.Registries
import org.bukkit.*
import org.bukkit.craftbukkit.CraftParticle.CraftParticleRegistry
import org.bukkit.craftbukkit.CraftRegistry.getMinecraftRegistry
import org.bukkit.craftbukkit.CraftWorld
import org.bukkit.craftbukkit.block.CraftBlock
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.util.Vector
import ru.darkrpmc.math.geometry.*
import ru.darkrpmc.text.colored
import kotlin.math.max
import kotlin.math.min
import kotlin.random.Random

private val particleRegistry = CraftParticleRegistry(getMinecraftRegistry(Registries.PARTICLE_TYPE))

fun Player.shoot(firearm: Firearm, state: FirearmState) {
    val now = System.currentTimeMillis()
    val sinceLastShot = now - state.lastShot
    state.lastShot = now

    state.ammo = (state.ammo - 1).toShort()
    world.playSound(location, Sound.ENTITY_FIREWORK_ROCKET_BLAST, 7f, .5f)
    world.playSound(location, Sound.ENTITY_FIREWORK_ROCKET_BLAST, 7f, .6f)
    world.playSound(location, Sound.ITEM_TRIDENT_HIT, 3f, 1f)
    val stable = isFlying || world.getBlockAt(location.subtract(.0, .05, .0)).isSolid

    if (firearm.bullets > 1) repeat(firearm.bullets.toInt()) {
        shoot(
            firearm.spreadLensDistance,
            if (stable && isSneaking) firearm.smallSpreadLensRadius else firearm.spreadLensRadius,
            firearm
        )
    } else shoot(
        firearm.spreadLensDistance,
        if (stable) {
            val f = 60f / firearm.fireRate
            (if (isSneaking) firearm.smallSpreadLensRadius else firearm.spreadLensRadius) *
                    max(0f, min(1 - (sinceLastShot / 1000f - f) / (firearm.coolingTime - f), 1f))
        } else firearm.spreadLensRadius,
        firearm
    )
}

private fun Player.shoot(lensDistance: Float, lensRadius: Float, firearm: Firearm) {
    val eyes = eyeLocation.point
    val dir = eyeLocation.direction
    val pointOnLens = eyes + dir * lensDistance +
            (dir cross randomVector).normalize() * lensRadius * Random.nextDouble()
    val stPart = pointOnLens - eyes
    if (damageRayCast(firearm, eyeLocation, stPart))
        damageRayCast(firearm, pointOnLens.location, dir * firearm.rayLength)
}

/**
 * @return true если луч ни во что не попал, иначе - false
 */
@Suppress("UNCHECKED_CAST")
private fun Player.damageRayCast(firearm: Firearm, location: Location, direction: Vector): Boolean {
    val result = world.rayTrace(
        location, direction, direction.length(), FluidCollisionMode.NEVER, true, 0.1
    ) { entity -> entity != this } ?: return true
    val hitLoc = result.hitPosition.toLocation(world)
    val entity = result.hitEntity
    val block = result.hitBlock
    if (entity != null && entity is LivingEntity && !entity.isDead) {
        entity.noDamageTicks = 0
        entity.damage(firearm.calcDamage(location.distance(hitLoc), hitLoc.y > entity.eyeLocation.y - .3), this)
        entity.velocity = entity.velocity / 7
    }
    if (block != null) {
        val level = (world as CraftWorld).handle
        // Аргументы функции:
        // 1 - игроки, которым будет показана частица,
        // 2 - отправитель частицы
        // 3 - данные частицы
        // 4 - помечает частицу важной?
        // 5 - показывать частицу независимо от количества частиц на экране
        // 6, 7, 8 - координаты частицы
        // 9 - количество частиц
        // 10, 11, 12 - разлёт частиц
        // 13 - скорость?
        level.sendParticlesSource(
            level.players(), null, BlockParticleOption(
                particleRegistry.get(Particle.BLOCK.key)!!.handle as ParticleType<BlockParticleOption>,
                (block as CraftBlock).nms
            ), false, true, hitLoc.x, hitLoc.y, hitLoc.z, 5, .0, .0, .0, 1.0
        )
        world.playSound(hitLoc, Sound.BLOCK_STONE_HIT, 2f, 2f)
    }
    return false
}

private fun Firearm.calcDamage(distance: Double, head: Boolean) = max(endDamage.toDouble(), min(
    (distance - damageLossStart) * (startDamage - endDamage) / (damageLossStart - damageLossEnd) + startDamage,
    startDamage.toDouble()
)) * (if (head) headshotMultiplier else 1f)

fun fullName(base: Component, ammo: Short) = base.append(" [$ammo]".colored(GRAY))