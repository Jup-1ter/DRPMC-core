package ru.darkrpmc.math.geometry

import io.papermc.paper.math.Position

@Suppress("UnstableApiUsage")
val Position.point get () = Point(x(), y(), z())