package ru.darkrpmc.professions.goverment

import org.bukkit.entity.Player
import ru.darkrpmc.professions.*

class Mayor(player: Player, source: ProfessionSource = ProfessionSource.SYSTEM) : Government(
    ProfessionInfo.MAYOR, source, player
)