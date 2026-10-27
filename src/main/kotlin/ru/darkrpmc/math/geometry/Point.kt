package ru.darkrpmc.math.geometry

import io.papermc.paper.math.FinePosition
import org.bukkit.Location
import org.bukkit.World
import ru.darkrpmc.world
import kotlin.math.pow
import kotlin.math.sqrt

@Suppress("UnstableApiUsage")
data class Point(val x: Double, val y: Double, val z: Double) : FinePosition {
    override fun x() = x
    override fun y() = y
    override fun z() = z
    val location get() = Location(world, x, y, z)
    fun location(yaw: Float, pitch: Float, w: World = world) = Location(w, x, y, z, yaw, pitch)
    infix fun distanceTo(o: Point) = sqrt((x-o.x).pow(2) + (y-o.y).pow(2) + (z-o.z).pow(2))
    fun plus(dx: Double = .0, dy: Double = .0, dz: Double = .0) = Point(x + dx, y + dy, z + dz)

    override fun toString() = "($x; $y; $z)"
}