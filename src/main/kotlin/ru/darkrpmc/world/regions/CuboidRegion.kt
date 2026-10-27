package ru.darkrpmc.world.regions

import io.papermc.paper.math.Position
import org.bukkit.util.Vector
import ru.darkrpmc.math.geometry.Point
import ru.darkrpmc.math.geometry.minus
import ru.darkrpmc.text.logRepresentation
import ru.darkrpmc.world.WorldEvHandler.regions
import java.lang.ref.WeakReference
import kotlin.math.max
import kotlin.math.min

@Suppress("UnstableApiUsage")
class CuboidRegion(
    x1: Double, y1: Double, z1: Double,
    x2: Double, y2: Double, z2: Double
) : Region() {
    constructor(from: Position, to: Position) : this(from.x(), from.y(), from.z(), to.x(), to.y(), to.z())

    val minX = min(x1, x2)
    val minY = min(y1, y2)
    val minZ = min(z1, z2)
    val from = Point(minX, minY, minZ)

    val maxX = max(x1, x2)
    val maxY = max(y1, y2)
    val maxZ = max(z1, z2)
    val to = Point(maxX, maxY, maxZ)

    init {
        regions += WeakReference(this)
    }

    override operator fun contains(p: Point) =
        p.x in from.x..to.x &&
                p.y in from.y..to.y &&
                p.z in from.z..to.z

    override fun contains(other: Region) = when (other) {
        is CuboidRegion -> other.from in this && other.to in this
        else -> false
    }

    override fun calculateExitNormal(from: Point, to: Point): Pair<Point, Vector> {
        val ft = to - from

        fun intersect(t: Double) = Point(from.x + t * ft.x, from.y + t * ft.y, from.z + t * ft.z)

        val inX = to.x in minX..maxX
        val inY = to.y in minY..maxY
        val inZ = to.z in minZ..maxZ

        return when {
            to.x > maxX && inY && inZ -> intersect((maxX - from.x) / ft.x) to Vector(ft.x, .0, .0)
            to.x < minX && inY && inZ -> intersect((minX - from.x) / ft.x) to Vector(ft.x, .0, .0)
            to.y > maxY && inX && inZ -> intersect((maxY - from.y) / ft.y) to Vector(.0, ft.y, .0)
            to.y < minY && inX && inZ -> intersect((minY - from.y) / ft.y) to Vector(.0, ft.y, .0)
            to.z > maxZ && inX && inY -> intersect((maxZ - from.z) / ft.z) to Vector(.0, .0, ft.z)
            to.z < minZ && inX && inY -> intersect((minZ - from.z) / ft.z) to Vector(.0, .0, ft.z)
            else -> from to ft
        }
    }

    override fun toString() = "${from.logRepresentation} - ${to.logRepresentation}"
    override fun equals(other: Any?) = this === other || other is CuboidRegion &&
            minX == other.minX && minY == other.minY && minZ == other.minZ &&
            maxX == other.maxX && maxY == other.maxY && maxZ == other.maxZ

    override fun hashCode(): Int {
        var result = minX.hashCode()
        result = 31 * result + minY.hashCode()
        result = 31 * result + minZ.hashCode()
        result = 31 * result + maxX.hashCode()
        result = 31 * result + maxY.hashCode()
        result = 31 * result + maxZ.hashCode()
        return result
    }
}