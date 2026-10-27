package ru.darkrpmc.world.particles

import org.bukkit.Location
import org.bukkit.Particle
import org.bukkit.entity.Player
import ru.darkrpmc.math.geometry.Point
import ru.darkrpmc.math.geometry.minus
import ru.darkrpmc.math.geometry.plus
import ru.darkrpmc.math.geometry.times
import ru.darkrpmc.math.step
import ru.darkrpmc.world.regions.CuboidRegion

fun drawCuboid(
    cuboid: CuboidRegion,
    particleType: Particle,
    viewer: Player
) {
    val step = 0.5
    val min = Point(cuboid.minX, cuboid.minY, cuboid.minZ)
    val max = Point(cuboid.maxX, cuboid.maxY, cuboid.maxZ)

    val corners = listOf(
        min,
        Point(max.x, min.y, min.z),
        Point(min.x, max.y, min.z),
        Point(min.x, min.y, max.z),
        Point(max.x, max.y, min.z),
        Point(min.x, max.y, max.z),
        Point(max.x, min.y, max.z),
        max
    )

    for (i in corners.indices)
        for (j in i + 1..<corners.size)
            spawnParticleLine(particleType, viewer, corners[i], corners[j], step)

}

private fun spawnParticleLine(particleType: Particle, viewer: Player, from: Point, to: Point, step: Double) {
    val direction = (to - from).normalize()

    for (i in .0..(from distanceTo to) step step) {
        val point = from + direction * i
        viewer.spawnParticle(particleType, Location(viewer.world, point.x, point.y, point.z), 1, 0.0, 0.0, 0.0, 0.0)
    }
}