package ru.darkrpmc.inventory.menu

import net.kyori.adventure.text.format.NamedTextColor.*
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.ItemStack
import org.bukkit.scheduler.BukkitTask
import ru.darkrpmc.*
import ru.darkrpmc.data.*
import ru.darkrpmc.inventory.times
import ru.darkrpmc.math.geometry.times
import ru.darkrpmc.professions.Profession
import ru.darkrpmc.text.colored
import kotlin.math.floor
import kotlin.math.min

class ShopMenu(private val player: Player) :
    Menu((player.profession.shopItems.size + 8) / 9, "Магазин".colored(DARK_AQUA)) {
    private var lastDeny: ItemStack? = null
    private var lastBal = -1.0

    init {
        player.profession.sampleItems.forEachIndexed(inv::setItem)
        player.openInventory(inv)
    }

    override fun handleClick(slot: Int, clickType: ClickType) {
        val prof = player.profession
        val (item, price) = prof.shopItems.getOrNull(slot) ?: return
        val bal = player.balance ?: .0

        val am = item.amount

        if (am > 1) {
            val drop = clickType == ClickType.DROP || clickType == ClickType.CONTROL_DROP
            if (player.writeOff(price)) {
                if (drop) {
                    val eye = player.eyeLocation.subtract(0.0, 0.3, 0.0)
                    world.dropItem(eye, item) {
                        it.thrower = player.uniqueId
                        it.velocity = eye.direction * 0.3
                    }
                } else player.inventory.addItem(item).forEach { (_, i) -> world.dropItem(player.location, i) }
            } else showDenyMessage(slot, bal, price, prof)
        } else {
            val (drop, wanted) = when (clickType) {
                ClickType.LEFT -> false to 1
                ClickType.SHIFT_LEFT -> false to item.maxStackSize
                ClickType.RIGHT -> false to (item.maxStackSize + 3) / 4
                ClickType.SHIFT_RIGHT -> false to (item.maxStackSize + 1) / 2
                ClickType.DROP -> true to 1
                ClickType.CONTROL_DROP -> true to item.maxStackSize
                else -> return
            }

            val toBuy = min(wanted, floor(bal / price).toInt())
            if (toBuy > 0 && player.writeOff(price * toBuy)) {
                val bought = item * toBuy
                if (drop) {
                    val eye = player.eyeLocation.subtract(0.0, 0.3, 0.0)
                    world.dropItem(eye, bought) {
                        it.owner = player.uniqueId
                        it.velocity = eye.direction * 0.3
                    }
                } else player.inventory.addItem(bought).forEach { (_, i) -> world.dropItem(player.location, i) }
            } else showDenyMessage(slot, bal, price, prof)
        }
    }

    private fun showDenyMessage(slot: Int, bal: Double, price: Int, prof: Profession) {
        val toShow = lastDeny.takeIf { bal == lastBal } ?: createDenyItem(bal, price)
        lastDeny = toShow
        lastBal = bal

        inv.setItem(slot, toShow)
        replacer.put(player.name, scheduler.runTaskLater(plugin, { ->
            inv.setItem(slot, prof.sampleItems[slot])
        }, 50))?.cancel()
    }

    // @formatter:off
    private fun createDenyItem(bal: Double, price: Int): ItemStack = ItemStack(Material.RED_STAINED_GLASS_PANE).apply {
        editMeta {
            it.itemName("Недостаточно средств".colored(DARK_RED))
            it.lore(listOf(
                "Вам не хватает ".colored(GRAY).append("${price - bal}$".colored(GOLD)),
                "для покупки данного предмета".colored(GRAY)
            ))
        }
    }
    // @formatter:on

    companion object {
        private val replacer = hashMapOf<String, BukkitTask>()
    }
}