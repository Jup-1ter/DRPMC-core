package ru.darkrpmc.professions.civilian

import org.bukkit.entity.Player
import ru.darkrpmc.professions.*

/** Класс, соответствующий профессии гражданина
 * @see Civilian */
class Citizen(player: Player, source: ProfessionSource = ProfessionSource.SYSTEM) : Civilian(
    ProfessionInfo.CITIZEN, source, player
)