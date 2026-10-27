package ru.darkrpmc.weapon

import kotlin.random.Random

class FirearmState(
    val serialNumber: Long = Random.nextLong(),
    var ammo: Short = 0,
    var lastShot: Long = 0
)