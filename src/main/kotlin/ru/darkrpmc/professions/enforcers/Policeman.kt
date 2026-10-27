package ru.darkrpmc.professions.enforcers

import org.bukkit.entity.Player
import ru.darkrpmc.professions.*

class Policeman(player: Player, source: ProfessionSource = ProfessionSource.SYSTEM) : LawEnforcer(
    ProfessionInfo.POLICEMAN, source, player
)