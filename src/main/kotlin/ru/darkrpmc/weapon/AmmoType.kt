package ru.darkrpmc.weapon

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor.YELLOW
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import ru.darkrpmc.ammoTypeKey
import ru.darkrpmc.inventory.item
import ru.darkrpmc.plugin
import ru.darkrpmc.text.colored

enum class AmmoType(val displayName: Component, model: NamespacedKey) {
    PISTOL("Пистолетный патрон".colored(YELLOW), NamespacedKey(plugin, "weapon/firearms/ammo/pistol")),
    SUBMACHINE("Патрон пистолета-пулемёта".colored(YELLOW), NamespacedKey(plugin, "weapon/firearms/ammo/submachine")),
    RIFLE("Винтовочный патрон".colored(YELLOW), NamespacedKey(plugin, "weapon/firearms/ammo/rifle")),
    SHOTGUN("Ружейный патрон".colored(YELLOW), NamespacedKey(plugin, "weapon/firearms/ammo/shotgun")),
    SNIPER("Снайперский патрон".colored(YELLOW), NamespacedKey(plugin, "weapon/firearms/ammo/sniper")),
    TASER("Заряд шокера".colored(YELLOW), NamespacedKey(plugin, "weapon/taser_ammo"));

    val item = item(Material.CLAY_BALL, displayName, 90, model).apply {
        editMeta {
            it.persistentDataContainer.set(ammoTypeKey, PersistentDataType.STRING, name)
        }
    }

    // @formatter:off
    companion object {
        fun getFor(item: ItemStack): AmmoType? {
            return try { valueOf(
                item.itemMeta.persistentDataContainer.get(ammoTypeKey, PersistentDataType.STRING) ?: return null
            ) } catch (e: Exception) { null }
        }
    }
}