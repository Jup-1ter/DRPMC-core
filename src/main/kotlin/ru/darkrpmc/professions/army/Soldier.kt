package ru.darkrpmc.professions.army

import org.bukkit.entity.Player
import ru.darkrpmc.professions.*

class Soldier(player: Player, source: ProfessionSource = ProfessionSource.SYSTEM) : Army(
    ProfessionInfo.SOLDIER, source, player
)