package ru.darkrpmc.professions.criminal

import org.bukkit.entity.Player
import ru.darkrpmc.data.config.Settings
import ru.darkrpmc.professions.*

class Murderer(source: ProfessionSource = ProfessionSource.SYSTEM, player: Player) : Criminal(
    ProfessionInfo.ROBBER, source, player
), WithCustomSpawn {
    override val spawn = Settings.Professions.undergroundSpawn
}