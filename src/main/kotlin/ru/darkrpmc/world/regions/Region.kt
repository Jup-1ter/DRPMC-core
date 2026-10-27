package ru.darkrpmc.world.regions

import io.papermc.paper.math.Position
import org.bukkit.Location
import org.bukkit.block.Block
import org.bukkit.entity.Entity
import org.bukkit.util.Vector
import ru.darkrpmc.math.geometry.Point
import ru.darkrpmc.math.geometry.point

@Suppress("UnstableApiUsage")
abstract class Region {
    abstract operator fun contains(p: Point): Boolean
    operator fun contains(pos: Position) = contains(pos.point)
    operator fun contains(loc: Location) = contains(loc.point)
    abstract operator fun contains(other: Region): Boolean
    operator fun contains(entity: Entity) = entity.location in this
    operator fun contains(block: Block) = block.location in this
    abstract fun calculateExitNormal(from: Point, to: Point): Pair<Point, Vector>
    abstract override fun equals(other: Any?): Boolean
}