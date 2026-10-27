package ru.darkrpmc.world

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor.*
import org.bukkit.*
import org.bukkit.entity.ItemDisplay
import org.bukkit.entity.Player
import ru.darkrpmc.*
import ru.darkrpmc.data.give
import ru.darkrpmc.inventory.item
import ru.darkrpmc.text.colored

val moneyPrinters = hashSetOf<MoneyPrinter>()

class MoneyPrinter(
    location: Location,
    val owner: Player,
    val type: MoneyPrinterType
) {
    private val block = world.getBlockAt(location)
    private val entity = world.spawn(location.clone().add(.0, .5, .0), ItemDisplay::class.java) {
        it.setItemStack(type.item)
    }
    private val task = scheduler.runTaskTimer(plugin, { -> stored += type.income }, 20 * 60, 20 * 60)
    var stored = 0

    init {
        moneyPrinters += this
        block.type = Material.BARRIER
    }

    fun destroy(by: Player) {
        block.type = Material.AIR
        entity.remove()
        task.cancel()
        by.give(300)
    }
}

enum class MoneyPrinterType(val income: Int, val displayName: Component) {
    USER(5, "Принтер".colored(GRAY)),
    VIP(8, "Принтер ".colored(GRAY).append("VIP".colored(GOLD))),
    PREMIUM(10, "Принтер ".colored(GRAY).append("Premium".colored(AQUA))),
    DELUX(15, "Принтер ".colored(GRAY).append("Delux".colored(GREEN))),
    LEGEND(20, "Принтер ".colored(GRAY).append("Legend".colored(RED)));

    val item =
        item(Material.EMERALD_BLOCK, displayName, model = NamespacedKey(plugin, "printers/${name.lowercase()}"))
}