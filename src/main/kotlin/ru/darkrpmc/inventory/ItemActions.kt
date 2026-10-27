package ru.darkrpmc.inventory

import org.bukkit.entity.Player
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.player.PlayerInteractEntityEvent
import org.bukkit.event.player.PlayerInteractEvent
import ru.darkrpmc.inventory.menu.MainMenu
import ru.darkrpmc.plugin
import ru.darkrpmc.professions.enforcers.*
import ru.darkrpmc.scheduler

// @formatter:off
enum class InventoryAction(val action: (InventoryClickEvent) -> Unit) {
    MENU({ event ->
        if (event.inventory.holder !is MainMenu && event.cursor.isEmpty) {
            scheduler.runTask(plugin) { -> MainMenu(event.whoClicked as Player) }
        }
    }),
    BACKPACK({ _ -> // TODO Рюкзак

    }),
    Buy3invLvl(::buy3invLvl),
    Buy2invLvl(::buy2invLvl),
    Buy1invLvl(::buy1invLvl),

    Buy3bpLvl(::buy3bpLvl),
    Buy2bpLvl(::buy2bpLvl),
    Buy1bpLvl(::buy1bpLvl),

    Buy3vltLvl(::buy3bpLvl),
    Buy2vltLvl(::buy2bpLvl),
    Buy1vltLvl(::buy1bpLvl)
}

enum class BlockInteractAction(val action: (PlayerInteractEvent) -> Unit) {
    MENU({ event -> MainMenu(event.player) }),
    BACKPACK({ event -> Backpack(event.player) })
}

enum class EntityInteractAction(val action: (PlayerInteractEntityEvent) -> Unit) {
    PASSPORT({ _ -> }) // TODO показ паспорта
}

enum class AttackAction(val action: (EntityDamageByEntityEvent) -> Boolean) {
    MENU({ event ->
        MainMenu(event.damager as Player)
        true
    }),
    HANDCUFF(a@{ event ->
        val damaged = event.entity as? Player ?: return@a true
        val damager = event.damager as Player
        if (cuffs[damaged.name] == CuffType.HANDCUFFS) release(damager, damaged)
        else handcuff(damager, damaged)
        true
    }),
    TIE(a@{ event ->
        val damaged = event.entity as? Player ?: return@a true
        val damager = event.damager as Player
        if (cuffs[damaged.name] == CuffType.TIES) release(damager, damaged)
        else tie(damager, damaged)
        true
    })
}