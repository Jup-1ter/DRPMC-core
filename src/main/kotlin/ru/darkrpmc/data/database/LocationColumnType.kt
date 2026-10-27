package ru.darkrpmc.data.database

import org.bukkit.Location
import org.jetbrains.exposed.sql.ColumnType
import ru.darkrpmc.text.logRepresentation
import ru.darkrpmc.world
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.DataInputStream
import java.io.DataOutputStream

class LocationColumnType : ColumnType<Location>() {
    override fun sqlType(): String = "BINARY(32)"

    override fun valueFromDB(value: Any) = DataInputStream(ByteArrayInputStream(value as ByteArray)).run {
        Location(
            world,
            readDouble(), readDouble(), readDouble(), // X, Y, Z
            readFloat(), readFloat() // Yaw, Pitch
        )
    }

    override fun notNullValueToDB(loc: Location): ByteArray {
        val byteStream = ByteArrayOutputStream()
        DataOutputStream(byteStream).use {
            it.writeDouble(loc.x)
            it.writeDouble(loc.y)
            it.writeDouble(loc.z)
            it.writeFloat(loc.yaw)
            it.writeFloat(loc.pitch)
        }
        return byteStream.toByteArray()
    }

    override fun nonNullValueToString(loc: Location) = loc.logRepresentation
}