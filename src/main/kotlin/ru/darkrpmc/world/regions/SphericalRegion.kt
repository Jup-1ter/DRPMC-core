package ru.darkrpmc.world.regions

import io.papermc.paper.math.Position
import org.bukkit.util.Vector
import ru.darkrpmc.math.geometry.Point
import ru.darkrpmc.math.geometry.intersectSphere
import ru.darkrpmc.math.geometry.minus
import ru.darkrpmc.math.geometry.projectionOn
import ru.darkrpmc.text.logRepresentation
import ru.darkrpmc.world.WorldEvHandler.regions
import java.lang.ref.WeakReference

@Suppress("UnstableApiUsage")
class SphericalRegion(
    centerX: Double, centerY: Double, centerZ: Double,
    val radius: Double
) : Region() {
    constructor(center: Position, radius: Double) : this(center.x(), center.y(), center.z(), radius)

    val center = Point(centerX, centerY, centerZ)

    init {
        regions += WeakReference(this)
    }

    override operator fun contains(p: Point) = p distanceTo center <= radius

    override fun contains(other: Region): Boolean = when (other) {
        is SphericalRegion -> (center distanceTo other.center) + other.radius <= radius
        is CuboidRegion -> other.from in this && other.to in this
        else -> false
    }

    override fun calculateExitNormal(f: Point, t: Point): Pair<Point, Vector> {
        val r = intersectSphere(f, t, center, radius)!!
        // val cr = (r - center).normalize()
        return r to (t - f projectionOn r - center)
    }

    override fun toString() = "${center.logRepresentation}, r=$radius"

    override fun equals(other: Any?) = this === other || other is SphericalRegion &&
            center == other.center && radius == other.radius

    override fun hashCode() = center.hashCode() * 31 + radius.hashCode()
}