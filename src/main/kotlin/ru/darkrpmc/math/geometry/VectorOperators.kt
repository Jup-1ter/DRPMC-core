package ru.darkrpmc.math.geometry

import org.bukkit.util.Vector
import kotlin.random.Random

operator fun Vector.unaryMinus() = Vector(-x, -y, -z)

infix fun Vector.projectionOn(o: Vector): Vector {
    val square = square
    return if (square == 0.0) Vector(0.0, 0.0, 0.0)
    else return o * (this dot o) / square
}

infix fun Vector.dot(o: Vector) = x * o.x + y * o.y + z * o.z
val Vector.square get() = dot(this)
infix fun Vector.cross(o: Vector) = Vector(
    y * o.z - z * o.y,
    z * o.x - x * o.z,
    x * o.y - y * o.x
)

operator fun Vector.plus(o: Vector) = Vector(x + o.x, y + o.y, z + o.z)
operator fun Vector.plusAssign(o: Vector) {
    x += o.x
    y += o.y
    z += o.z
}

operator fun Vector.minus(o: Vector) = Vector(x - o.x, y - o.y, z - o.z)
operator fun Vector.minusAssign(o: Vector) {
    x -= o.x
    y -= o.y
    z -= o.z
}

operator fun Vector.times(d: Double) = Vector(x * d, y * d, z * d)
operator fun Vector.timesAssign(d: Double) {
    x *= d
    y *= d
    z *= d
}

operator fun Vector.times(f: Float) = Vector(x * f, y * f, z * f)
operator fun Vector.timesAssign(f: Float) {
    x *= f
    y *= f
    z *= f
}

operator fun Vector.times(i: Int) = Vector(x * i, y * i, z * i)
operator fun Vector.timesAssign(d: Int) {
    x *= d
    y *= d
    z *= d
}

operator fun Vector.div(d: Double) = Vector(x / d, y / d, z / d)
operator fun Vector.divAssign(d: Double) {
    x /= d
    y /= d
    z /= d
}

operator fun Vector.div(i: Int) = Vector(x / i, y / i, z / i)
operator fun Vector.divAssign(d: Int) {
    x /= d
    y /= d
    z /= d
}

operator fun Point.minus(other: Point) = Vector(x - other.x, y - other.y, z - other.z)
operator fun Point.plus(v: Vector) = Point(x + v.x, y + v.y, z + v.z)
operator fun Point.minus(v: Vector) = Point(x - v.x, y - v.y, z - v.z)

val randomVector: Vector get() {
    val v = Vector(Random.nextDouble(-1.0, 1.0), Random.nextDouble(-1.0, 1.0), Random.nextDouble(-1.0, 1.0))
    return v / v.length()
}