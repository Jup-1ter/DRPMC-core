package ru.darkrpmc.math.geometry

import kotlin.math.sqrt

fun intersectSphere(from: Point, to: Point, center: Point, radius: Double): Point? {
    val ft = to - from
    val cf = from - center

    val a = ft.square
    val b = 2 * (cf.dot(ft))
    val c = cf.square - radius * radius

    val d = b * b - 4 * a * c
    if (d < 0) return null

    val dSqrt = sqrt(d)
    val t2 = (-b + dSqrt) / (2 * a)

    return if (t2 in 0.0..1.0) from + ft * t2 else return null
}