package ru.darkrpmc.data.database

import org.bukkit.inventory.ItemStack
import org.jetbrains.exposed.sql.ColumnType
import org.jetbrains.exposed.sql.vendors.currentDialect
import java.io.*

class InventoryColumnType : ColumnType<HashMap<Int, ItemStack>>() {
    override fun sqlType() = currentDialect.dataTypeProvider.blobType()

    override fun valueFromDB(value: Any): HashMap<Int, ItemStack> {
        val contents = hashMapOf<Int, ItemStack>()
        DataInputStream((value as ByteArray).inputStream()).use {
            while (it.available() > 0) {
                val slot = it.readInt()
                val dataSize = it.readInt()
                val data = ByteArray(dataSize)
                it.readFully(data)

                val item = ItemStack.deserializeBytes(data)
                contents[slot] = item
            }
        }
        return contents
    }

    override fun notNullValueToDB(value: HashMap<Int, ItemStack>): ByteArray {
        val byteStream = ByteArrayOutputStream()
        DataOutputStream(byteStream).use {
            value.forEach { (slot, item) ->
                val data = item.serializeAsBytes()

                it.writeInt(slot)
                it.writeInt(data.size)
                it.write(data)
            }
        }
        val ba = byteStream.toByteArray()
        return ba
    }
}