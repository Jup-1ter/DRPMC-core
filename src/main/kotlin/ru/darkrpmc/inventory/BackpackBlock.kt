package ru.darkrpmc.inventory

import net.kyori.adventure.text.format.NamedTextColor.GRAY
import net.kyori.adventure.text.format.NamedTextColor.RED
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import ru.darkrpmc.advancements.invAdv
import ru.darkrpmc.data.serverData
import ru.darkrpmc.data.writeOff
import ru.darkrpmc.plugin
import ru.darkrpmc.text.colored
import ru.darkrpmc.text.miniMessage

private const val stLvlPrice = 250
private const val stLvlSlots = 9
private const val ndLvlPrice = 2750
private const val ndLvlSlots = 4
private const val rdLvlPrice = 10500
private const val rdLvlSlots = 5

private const val advSlots = 4
private const val caseSlots = 5

private val notAvailableBlock = item(
    Material.BARRIER, "Слот заблокирован".colored(RED), 1, null,
    listOf("Для разблокировки сначала", "разблокируйте предыдущие слоты").colored(RED),
    CustomFlags(true, true)
)

private val caseBlock = item(
    Material.BARRIER, "Слот заблокирован".colored(RED), 1, null,
    listOf("Для разблокировки откройте кейс".colored(GRAY)), // TODO название кейса
    CustomFlags(true, true)
)
private val advBlock = item(
    Material.BARRIER, "Слот заблокирован".colored(RED), 1, null,
    listOf("Для разблокировки выполните достижение".colored(GRAY)), // TODO название достижения
    CustomFlags(true, true)
)

private val rdLvlBlock = item(
    Material.BARRIER, "Слот заблокирован".colored(RED), 1, null,
    listOf(
        miniMessage("<gray>Для разблокировки <gold>$rdLvlSlots</gold> слотов"),
        miniMessage("<gray>заплатите <gold>$rdLvlPrice<gold>")
    ),
    CustomFlags(true, true), InventoryAction.Buy3bpLvl
)

fun buy3bpLvl(event: InventoryClickEvent) {
    val player = event.whoClicked as? Player ?: return
    val sd = player.serverData ?: return
    if (sd.backpackLvl != 2) return
    if (!player.writeOff(rdLvlPrice)) return
    sd.backpackLvl = 3
    player.updateBpBlock(event.inventory)
}

private val ndLvlBlock = item(
    Material.BARRIER, "Слот заблокирован".colored(RED), 1, null,
    listOf(
        miniMessage("<gray>Для разблокировки первых <gold>$ndLvlSlots</gold> слотов"),
        miniMessage("<gray>заплатите <gold>$ndLvlPrice<gold>")
    ),
    CustomFlags(true, true), InventoryAction.Buy2bpLvl
)

fun buy2bpLvl(event: InventoryClickEvent) {
    val player = event.whoClicked as? Player ?: return
    val sd = player.serverData ?: return
    if (sd.backpackLvl != 1) return
    if (!player.writeOff(ndLvlPrice)) return
    sd.backpackLvl = 2
    player.updateBpBlock(event.inventory)
}

private val stLvlBlock = item(
    Material.BARRIER, "Слот заблокирован".colored(RED), 1, null,
    listOf(
        miniMessage("<gray>Для разблокировки первых <gold>$stLvlSlots</gold> слотов"),
        miniMessage("<gray>заплатите <gold>$stLvlPrice$<gold>")
    ),
    CustomFlags(true, true), InventoryAction.Buy1bpLvl
)

fun buy1bpLvl(event: InventoryClickEvent) {
    val player = event.whoClicked as? Player ?: return
    val sd = player.serverData ?: return
    if (sd.backpackLvl != 0) return
    if (!player.writeOff(stLvlPrice)) return
    sd.backpackLvl = 1
    player.updateBpBlock(event.inventory)
}

private val Player.blockBpItems: List<ItemStack>
    get() {
        val bpLvl = serverData?.backpackLvl
        if (bpLvl == null) {
            ru.darkrpmc.scheduler.runTask(plugin) { ->
                kick("Не удалось загрузить игровые данные".colored(RED))
            }
            return emptyList()
        }
        val items = arrayListOf<ItemStack>()
        if (true) // TODO проверка покупки кейса
            repeat(caseSlots) { items += caseBlock }
        if (!getAdvancementProgress(invAdv).isDone) repeat(advSlots) { items += advBlock } // TODO изменить достижение

        if (bpLvl == 2) repeat(rdLvlSlots) { items += rdLvlBlock }
        else if (bpLvl < 2) repeat(rdLvlSlots) { items += notAvailableBlock }

        if (bpLvl == 1) repeat(ndLvlSlots) { items += ndLvlBlock }
        else if (bpLvl < 1) {
            repeat(ndLvlSlots) { items += notAvailableBlock }
            repeat(stLvlSlots) { items += stLvlBlock }
        }

        return items
    }

fun Player.updateBpBlock(inv: Inventory) {
    inv.remove(Material.BARRIER)
    blockBpItems.forEachIndexed(inv::setItem)
}

val Player.blockedBpSlots get() = blockBpItems.size