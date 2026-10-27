package ru.darkrpmc

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor.*
import ru.darkrpmc.text.colored
import ru.darkrpmc.world.MoneyPrinterType

/**
 * @param salaryBonus Множитель зарплаты
 * @param printerType Тип денежного принтера
 * @param displayName Отображаемое название группы
 */
enum class DonateGroup(
    val salaryBonus: Double,
    val printerType: MoneyPrinterType,
    val displayName: Component
) {
    USER(.0, MoneyPrinterType.USER, "Игрок".colored(GRAY)),
    VIP(.2, MoneyPrinterType.VIP, "VIP".colored(GOLD)),
    PREMIUM(.3, MoneyPrinterType.PREMIUM, "Premium".colored(AQUA)),
    DELUX(.4, MoneyPrinterType.DELUX, "Delux".colored(GREEN)),
    LEGEND(.5, MoneyPrinterType.LEGEND, "Legend".colored(RED));
}