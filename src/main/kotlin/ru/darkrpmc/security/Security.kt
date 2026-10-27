package ru.darkrpmc.security

import net.kyori.adventure.text.Component
import org.bukkit.entity.Player
import ru.darkrpmc.data.config.Settings.Security.modVersion

fun Player.validateModVersion(version: String): Boolean {
    if (version == modVersion) return true
    kick(Component.text(
        "У вас установлен мод версии $version. Актуальная версия мода $modVersion."
    ).appendNewline().append(Component.text("Пожалуйста, установите мод актуальной версии")))
    return false
}