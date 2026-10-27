package ru.darkrpmc.professions

import org.bukkit.entity.Player
import ru.darkrpmc.data.config.Settings

abstract class Civilian(
    info: ProfessionInfo,
    source: ProfessionSource,
    player: Player
) : Profession(info, source, player)

abstract class Criminal(
    info: ProfessionInfo,
    source: ProfessionSource,
    player: Player
) : Profession(info, source, player)

abstract class Government(
    info: ProfessionInfo,
    source: ProfessionSource,
    player: Player
) : Profession(info, source, player), WithCustomSpawn {
    override val spawn = Settings.Professions.cityHallSpawn
}

abstract class Army(
    info: ProfessionInfo,
    source: ProfessionSource,
    player: Player
) : Profession(info, source, player), WithCustomSpawn {
    override val spawn = Settings.Professions.armySpawn
}

abstract class LawEnforcer(
    info: ProfessionInfo,
    source: ProfessionSource,
    player: Player
) : Profession(info, source, player), WithCustomSpawn {
    override val spawn = Settings.Professions.policeSpawn
}