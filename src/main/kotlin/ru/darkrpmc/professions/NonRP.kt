package ru.darkrpmc.professions

import org.bukkit.entity.Player

class NonRP(
    player: Player, source: ProfessionSource = ProfessionSource.SYSTEM
) : Profession(ProfessionInfo.NONRP, source, player)