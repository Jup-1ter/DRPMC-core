package ru.darkrpmc.inventory

import net.kyori.adventure.text.format.NamedTextColor.GRAY
import net.kyori.adventure.text.format.NamedTextColor.RED
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack
import ru.darkrpmc.advancements.invAdv
import ru.darkrpmc.data.serverData
import ru.darkrpmc.data.writeOff
import ru.darkrpmc.plugin
import ru.darkrpmc.text.colored
import ru.darkrpmc.text.miniMessage

private const val stLvlPrice = 500
private const val stLvlSlots = 5
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
    CustomFlags(true, true), InventoryAction.Buy3invLvl
)

fun buy3invLvl(event: InventoryClickEvent) {
    val player = event.whoClicked as? Player ?: return
    val sd = player.serverData ?: return
    if (sd.invLvl != 2) return
    if (!player.writeOff(rdLvlPrice)) return
    sd.invLvl = 3
    player.updateInvBlock()
}

private val ndLvlBlock = item(
    Material.BARRIER, "Слот заблокирован".colored(RED), 1, null,
    listOf(
        miniMessage("<gray>Для разблокировки первых <gold>$ndLvlSlots</gold> слотов"),
        miniMessage("<gray>заплатите <gold>$ndLvlPrice<gold>")
    ),
    CustomFlags(true, true), InventoryAction.Buy2invLvl
)

fun buy2invLvl(event: InventoryClickEvent) {
    val player = event.whoClicked as? Player ?: return
    val sd = player.serverData ?: return
    if (sd.invLvl != 1) return
    if (!player.writeOff(ndLvlPrice)) return
    sd.invLvl = 2
    player.updateInvBlock()
}

private val stLvlBlock = item(
    Material.BARRIER, "Слот заблокирован".colored(RED), 1, null,
    listOf(
        miniMessage("<gray>Для разблокировки первых <gold>$stLvlSlots</gold> слотов"),
        miniMessage("<gray>заплатите <gold>$stLvlPrice$<gold>")
    ),
    CustomFlags(true, true), InventoryAction.Buy1invLvl
)

fun buy1invLvl(event: InventoryClickEvent) {
    val player = event.whoClicked as? Player ?: return
    val sd = player.serverData ?: return
    if (sd.invLvl != 0) return
    if (!player.writeOff(stLvlPrice)) return
    sd.invLvl = 1
    player.updateInvBlock()
}

private val Player.blockInvItems: List<ItemStack>
    get() {
        val invLvl = serverData?.invLvl
        if (invLvl == null) {
            ru.darkrpmc.scheduler.runTask(plugin) { ->
                kick("Не удалось загрузить игровые данные".colored(RED))
            }
            return emptyList()
        }
        val items = arrayListOf<ItemStack>()
        if (true) // TODO проверка покупки кейса
            repeat(caseSlots) { items += caseBlock }
        if (!getAdvancementProgress(invAdv).isDone) repeat(advSlots) { items += advBlock }

        if (invLvl == 2) repeat(rdLvlSlots) { items += rdLvlBlock }
        else if (invLvl < 2) repeat(rdLvlSlots) { items += notAvailableBlock }

        if (invLvl == 1) repeat(ndLvlSlots) { items += ndLvlBlock }
        else if (invLvl < 1) {
            repeat(ndLvlSlots) { items += notAvailableBlock }
            repeat(stLvlSlots) { items += stLvlBlock }
        }

        return items
    }

fun Player.updateInvBlock() {
    inventory.remove(Material.BARRIER)
    blockInvItems.forEachIndexed { i, it -> inventory.setItem(i + 9, it) }
}