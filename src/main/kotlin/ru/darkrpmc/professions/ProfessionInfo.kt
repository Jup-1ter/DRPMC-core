package ru.darkrpmc.professions

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor.*
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta
import org.bukkit.profile.PlayerTextures.SkinModel
import ru.darkrpmc.*
import ru.darkrpmc.data.config.Settings
import ru.darkrpmc.data.profession
import ru.darkrpmc.data.uptime
import ru.darkrpmc.professions.army.Soldier
import ru.darkrpmc.professions.civilian.Citizen
import ru.darkrpmc.professions.criminal.Robber
import ru.darkrpmc.professions.goverment.*
import ru.darkrpmc.professions.enforcers.*
import ru.darkrpmc.text.*
import java.net.URI
import java.util.*

/** Регистр профессий, содержащий базовую информацию о каждой профессии
 * @property displayName Отображаемое название профессии
 * @property salary Базовая зарплата профессии
 * @property slots Максимальное количество игроков, которые могут иметь эту профессию
 * @property description описание профессии */
enum class ProfessionInfo(
    val displayName: String,
    val salary: Long,
    val slots: Int,
    val description: String,
    val category: ProfessionCategory?,
    val constructor: (Player, ProfessionSource) -> Profession,
    val skin: String,
    val skinModel: SkinModel = SkinModel.CLASSIC,
    val pickProtocol: ProfessionInfo.(Player) -> Unit = ProfessionInfo::defaultPickProtocol,
    val loreAdder: ProfessionInfo.(Player) -> List<Component> = ProfessionInfo::defaultLoreAdder,
    val resetAfterDeath: Boolean = false
) {
    // @formatter:off
    NONRP("NonRP", 33_30, 64, "Администратор", null, ::NonRP,
        "http://textures.minecraft.net/texture/b4473ca26feef9558369d58633b7da2274087028ac9b3e4ee21fc08a687c92bd",
        SkinModel.SLIM, pickProtocol = ProfessionInfo::nonrpPickProtocol, loreAdder = { emptyList() }
    ), CITIZEN("Гражданин", 3_30, 64, "Мирный житель", ProfessionCategory.Civilian, ::Citizen,
        "http://textures.minecraft.net/texture/6c160fbd16adbc4bff2409e70180d911002aebcfa811eb6ec3d1040761aea6dd",
        SkinModel.SLIM
    ), ROBBER("Вор", 2_00, 6, "Воришка", ProfessionCategory.Criminal, ::Robber,
        "https://textures.minecraft.net/texture/933b9f03a8fb9a3f3d3cc35bf11b306a8de0b0476e6eab54a797314fe7885034"
    ), SOLDIER("Рядовой", 0, 64, "Единица армии", ProfessionCategory.Army, ::Soldier,
        "http://textures.minecraft.net/texture/c710d07bdeb0d97a3a95d7ecbd98cf146e6db6fd0e44059809ace766ab3d32b4"
    ), POLICEMAN("Полицейский", 18_30, 12, "Доблестный защитник правопорядка", ProfessionCategory.LawEnforcement, ::Policeman,
        "https://textures.minecraft.net/texture/846f5440b59d28eb5c99d0fd1fda467c3e4bb502eac3b3ecb22969dd235914fc"
    ), MAYOR("Мэр", 8_30, 1, "Руководитель города", ProfessionCategory.Government, ::Mayor,
        "https://textures.minecraft.net/texture/13f477ffff5c17e1f87d64481ab0b2d8371a6768bb18417ca099cc0535939bf3",
        pickProtocol = ProfessionInfo::pickMayor, loreAdder = ProfessionInfo::mayorLoreAdder, resetAfterDeath = true
    );
    // @formatter:on TODO скины

    private val profile = server.createProfile(UUID.randomUUID()).apply {
        setTextures(textures.apply { skin = URI(this@ProfessionInfo.skin).toURL() })
    }

    fun genItemFor(player: Player) = ItemStack.of(Material.PLAYER_HEAD).apply {
        editMeta {
            it.itemName(displayName.colored(DARK_AQUA))
            it.setMaxStackSize(1)
            it.lore(listOf(description.colored(GRAY)) + loreAdder(player))
            (it as SkullMeta).playerProfile = profile
        }
    }

    private fun defaultLoreAdder(player: Player): List<Component> {
        val end = player.uptime.lastProfChange + Settings.Professions.changeCooldown
        val now = System.currentTimeMillis()
        val onProf = server.onlinePlayers.count { it.profession.info == this }
        val color = if (onProf == slots) "red" else "dark_aqua"
        val toAdd = arrayListOf(miniMessage("<gray>Занято: <$color>$onProf</$color>/<dark_aqua>$slots"))
        val c = cuffs[player.name]
        when {
            player.profession.info == this -> toAdd += "Вы уже имеете эту профессию".colored(RED)
            c != null -> toAdd += "Вы в ${if (c == CuffType.HANDCUFFS) "наручниках" else "стяжках"}".colored(RED)
            player.name in stunned -> toAdd += "Вы оглушены шокером".colored(RED)
            end > now -> toAdd += "До смены профессии: ${minutesAndSeconds(end - now)}".colored(RED)
        }
        return toAdd
    }

    private fun defaultPickProtocol(player: Player) {
        if (System.currentTimeMillis() - player.uptime.lastProfChange > Settings.Professions.changeCooldown &&
            server.onlinePlayers.count { it.profession.info == this } < slots && player.name !in cuffs &&
            player.name !in stunned && player.setProfession(this, ProfessionSource.TAKEN)
        ) player.closeInventory()
    }

    private fun nonrpPickProtocol(player: Player) {
        if (player !in Permission.NonRPProfession) return
        player.setProfession(this, ProfessionSource.TAKEN)
        player.closeInventory()
        cuffs -= player.name
        stunned.remove(player.name)?.cancel()
    }
}