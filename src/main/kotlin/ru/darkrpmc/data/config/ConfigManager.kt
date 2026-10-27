package ru.darkrpmc.data.config

import ru.darkrpmc.logger
import ru.darkrpmc.plugin
import ru.darkrpmc.text.logRepresentation
import java.net.URI

fun loadConfig() {
    val cfg = plugin.config
    val errors = TreeNode("config")

    /// Взаимодействия ///

    val itErrors = TreeNode("interactions")
    val itSection = cfg.getConfigurationSection("interactions", errors)!!

    Settings.PlayerInteract.cuffDistance = itSection.getDouble(
        "cuff-distance", itErrors, min = .0, max = 10.0, default = Settings.PlayerInteract.cuffDistance
    )!!

    Settings.PlayerInteract.shockerDuration = itSection.getLong(
        "shocker-duration", itErrors, min = 0, default = Settings.PlayerInteract.shockerDuration
    )!!

    Settings.PlayerInteract.robberyCooldown = itSection.getInt(
        "robbery-cooldown", itErrors, min = 0, default = Settings.PlayerInteract.robberyCooldown
    )!!

    if (!itErrors.isEmpty()) errors += itErrors

    /// Мир /// @formatter:off

    val wErrors = TreeNode("world")
    val wSection = cfg.getConfigurationSection("world", errors)!!

    Settings.World.playZone = wSection.getCuboidRegion("play-zone", wErrors, default = Settings.World.playZone)!!

    val sz = wSection.getCuboidRegion("spawn-zone", wErrors, default = Settings.World.spawnZone)!!
    if (sz !in Settings.World.playZone) wErrors += "Зона спавна должна находится в пределах основной игровой зоны"
    else Settings.World.spawnZone = sz

    val s = wSection.getLocation("spawn", wErrors, default = Settings.World.spawn)!!
    if (s !in Settings.World.spawnZone) wErrors +=
        "Точка спавна ${s.logRepresentation} должна находится в пределах зоны спавна $sz"
    else Settings.World.spawn = s

    val b = wSection.getCuboidRegion("bank-zone", wErrors, default = Settings.World.bank)!!
    if (b !in Settings.World.playZone) wErrors += "Банк должен находится в пределах основной игровой зоны"
    else Settings.World.bank = b

    val ch = wSection.getCuboidRegion("city-hall-zone", wErrors, default = Settings.World.cityHall)!!
    if (ch !in Settings.World.playZone) wErrors += "Зона мэрии должна находится в пределах основной игровой зоны"
    else Settings.World.cityHall = ch

    val pp = wSection.getLocation("payphone", wErrors, default = Settings.World.payphone)!!
    if (pp !in Settings.World.playZone) wErrors +=
        "Таксофон ${pp.logRepresentation} должен находится в пределах основной игровой зоны"
    else Settings.World.payphone = pp

    if (!wErrors.isEmpty()) errors += wErrors

    /// Профессии ///

    val pErrors = TreeNode("professions")
    val pSection = cfg.getConfigurationSection("professions", errors)!!

    val chs = pSection.getLocation("city-hall-spawn", pErrors, default = Settings.Professions.cityHallSpawn)!!
    if (chs !in Settings.World.cityHall) pErrors +=
        "Точка спавна сотрудников мэрии ${chs.logRepresentation} должна находится в пределах зоны мэрии $ch"
    else Settings.Professions.cityHallSpawn = chs

    val ps = pSection.getLocation("police-spawn", pErrors, default = Settings.Professions.policeSpawn)!!
    if (ps !in Settings.World.playZone) pErrors +=
        "Точка спавна сотрудников силовых структур ${ps.logRepresentation} должна находится в пределах игровой зоны"
    else Settings.Professions.policeSpawn = ps

    val us = pSection.getLocation("underground-spawn", pErrors, default = Settings.Professions.undergroundSpawn)!!
    if (us !in Settings.World.playZone) pErrors +=
        "Точка спавна в метро ${us.logRepresentation} должна находится в пределах игровой зоны"
    else Settings.Professions.undergroundSpawn = us

    val ars = pSection.getLocation("army-spawn", pErrors, default = Settings.Professions.armySpawn)!!
    if (ars !in Settings.World.playZone) pErrors +=
        "Точка спавна военных ${ars.logRepresentation} должна находится в пределах игровой зоны"
    else Settings.Professions.armySpawn = ars

    val ls = pSection.getLocation("loader-spawn", pErrors, default = Settings.Professions.loaderSpawn)!!
    if (ls !in Settings.World.playZone) pErrors +=
        "Точка спавна грузчика ${ls.logRepresentation} должна находится в пределах игровой зоны"
    else Settings.Professions.loaderSpawn = ls

    Settings.Professions.changeCooldown =
        pSection.getInt("change-cooldown", pErrors, default = Settings.Professions.changeCooldown)!! * 1000

    if (!pErrors.isEmpty()) errors += pErrors

    /// Предметы /// @formatter:on

    val iErrors = TreeNode("items")
    val iSection = cfg.getConfigurationSection("items", errors)!!

    Settings.Items.backpackTexture = URI(
        iSection.getString("backpack-texture", iErrors, default = Settings.Items.backpackTexture.toString())!!
    ).toURL()

    if (!iErrors.isEmpty()) errors += iErrors

    /// Безопасность ///

    val sErrors = TreeNode("security")
    val sSection = cfg.getConfigurationSection("security", errors)!!

    Settings.Security.opsIPs = sSection.getStringList(
        "op-ips", sErrors, default = Settings.Security.opsIPs.toList()
    )!!.toSet()

    if (!sErrors.isEmpty()) errors += sErrors

    ///-///

    if (errors.isEmpty()) logger.info("Параметры успешно загружены из файла")
    else logger.severe("Проблемы при чтении файла конфигурации\n$errors")
}