@file:Suppress("Unused") // TODO убрать

package ru.darkrpmc.professions.enforcers

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor.DARK_AQUA
import net.kyori.adventure.text.format.NamedTextColor.GRAY
import net.kyori.adventure.title.Title
import org.bukkit.*
import org.bukkit.entity.HumanEntity
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitTask
import ru.darkrpmc.*
import ru.darkrpmc.data.config.Settings
import ru.darkrpmc.data.profession
import ru.darkrpmc.inventory.AttackAction
import ru.darkrpmc.inventory.item
import ru.darkrpmc.professions.NonRP
import ru.darkrpmc.professions.criminal.Raider
import ru.darkrpmc.text.colored

val cuffs = hashMapOf<String, CuffType>()
val stunned = hashMapOf<String, BukkitTask>()

fun handcuff(player: Player, victim: Player) {
//    if (player.profession !is PowerStructures && player.profession !is NonRP) {
//        player.sendRichMessage("<dark_aqua>Вы <red>не можете <gray>взять в наручники данного <dark_aqua>игрока")
//        return
//    }
    if (isUnable(player, victim)) return
    when (cuffs[victim.name]) {
        CuffType.HANDCUFFS -> {
            player.sendRichMessage("<gay>Данный <dark_aqua>человек</dark_aqua> уже в наручниках")
            return
        }

        CuffType.TIES -> player.sendRichMessage("<dark_aqua>Вы <gray>заменили стяжки на наручники")
        null -> player.sendRichMessage("<dark_aqua>Вы <gray>надели наручники на <dark_aqua>человека")
    }
    // victim.sendMessage("<gray>На <dark_aqua>вас</dark_aqua> надели наручники")
    victim.showTitle(
        Title.title(Component.empty(), player.name.colored(DARK_AQUA).append(" надел на вас наручники".colored(GRAY)))
    )
    cuffs[victim.name] = CuffType.HANDCUFFS
}

fun tie(player: Player, victim: Player) {
    if (player.profession !is Raider && player.profession !is NonRP) {
        player.sendRichMessage("<red>Вы не можете использовать стяжки")
        return
    }
    if (isUnable(player, victim)) return
    if (victim.name in cuffs) player.sendRichMessage("<gay>Данный <dark_aqua>человек</dark_aqua> уже в стяжках")
    else {
        cuffs[victim.name] = CuffType.TIES
        player.sendRichMessage("<dark_aqua>Вы <gray>надели стяжки на <dark_aqua>игрока")
        //victim.sendRichMessage("<gray>На <dark_aqua>вас</dark_aqua> надели стяжки")
        victim.showTitle(
            Title.title(Component.empty(), player.name.colored(DARK_AQUA).append(" надел на вас стяжки".colored(GRAY)))
        )
    }
}

fun stun(victim: Player) {
    if (victim.profession is NonRP) return
    victim.playSound(victim.location, Sound.ENTITY_MOOSHROOM_CONVERT, 1f, 2f)
    stunned.put(
        victim.name, scheduler.runTaskLater(
            plugin, { -> stunned.remove(victim.name) }, Settings.PlayerInteract.shockerDuration
        )
    )?.cancel()
}

fun isUnable(player: HumanEntity, victim: Player) =
    player.location.distance(victim.location) > Settings.PlayerInteract.cuffDistance ||
            player.name in cuffs || player.name in stunned || victim.profession is NonRP

fun startEffectCycle() {
    scheduler.runTaskTimer(plugin, { ->
        server.onlinePlayers.forEach { player ->
            if (player.name in cuffs) Settings.PlayerInteract.cuffEffects.forEach(player::addPotionEffect)
            if (player.name in stunned) Settings.PlayerInteract.stunEffects.forEach(player::addPotionEffect)
        }
    }, 20 * 5, 20)
}

fun release(player: HumanEntity, victim: Player) {
    if (isUnable(player, victim)) return
    player.sendRichMessage("<dark_aqua>Вы <gray>освободили <dark_aqua>игрока")
    // victim.sendRichMessage("<dark_aqua>Вас <gray> освободили")
    cuffs -= victim.name
}

val handcuffs = item(
    Material.CLAY_BALL, "Наручники".colored(GRAY), 1, NamespacedKey(plugin, "handcuffs"),
    attackAction = AttackAction.HANDCUFF
)
val tie = item(
    Material.CLAY_BALL, "Стяжки".colored(GRAY), 8, NamespacedKey(plugin, "tie"),
    attackAction = AttackAction.TIE
)