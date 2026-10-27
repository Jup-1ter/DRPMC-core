@file:Suppress("UnstableApiUsage")

package ru.darkrpmc.text

import io.papermc.paper.math.Position
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import ru.darkrpmc.data.profession

val Player.logRepresentation get() = {
    "<${profession.info.displayName}> $name ${location.logRepresentation}"
}

val ItemStack.logRepresentation get() = "${displayName()} <$type>"

val Position.logRepresentation get() = "(${x()}; ${y()}; ${z()})"

val Location.logRepresentation get() = "${(this as Position).logRepresentation} {$pitch; $yaw}"
