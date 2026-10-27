package ru.darkrpmc

import com.google.common.io.ByteStreams
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor.GRAY
import org.bukkit.GameMode
import org.bukkit.Material.AIR
import org.bukkit.NamespacedKey
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.persistence.PersistentDataType
import org.bukkit.profile.PlayerTextures.SkinModel
import ru.darkrpmc.data.*
import ru.darkrpmc.data.config.Settings
import ru.darkrpmc.data.persistent.FirearmDataType
import ru.darkrpmc.data.persistent.FirearmStateDataType
import ru.darkrpmc.events.professions.PlayerChangeProfessionEvent
import ru.darkrpmc.professions.*
import ru.darkrpmc.text.colored
import ru.darkrpmc.text.miniMessage
import ru.darkrpmc.weapon.fullName
import java.net.URI

val Player.ip get() = address!!.address
val Player.profSpawn
    get() = if (profession is WithCustomSpawn) (profession as WithCustomSpawn).spawn else Settings.World.spawn

fun Player.tpToSpawnAsync() = teleportAsync(Settings.World.spawn)
fun Player.tpToProfSpawnAsync() = teleportAsync(profSpawn)
fun Player.transferViaProxy(server: String) {
    val out = ByteStreams.newDataOutput()
    out.writeUTF("Connect")
    out.writeUTF(server)
    sendPluginMessage(plugin, "BungeeCord", out.toByteArray())
}

fun Player.toLobby() = transferViaProxy("lobby")
fun isOnline(name: String) = server.onlinePlayers.any { it.name == name }
fun Player.clearBlocks() = session.blocks.forEach { world.getBlockAt(it.location).setType(AIR, false) }
val Player.fullName: Component
    get() {
        val acc = account ?: return displayName()
        val display =
            Component.text().append(acc.donateGroup.displayName).append(Component.text(" ")).append(displayName())

        if (acc.rank != Rank.USER) display.append(Component.text(" ")).append(acc.rank.displayName)
        return display.build()
    }

fun Player.updateTabName() = playerListName(fullName)

// @formatter:off
fun Player.setProfession(prof: ProfessionInfo, source: ProfessionSource = ProfessionSource.SYSTEM): Boolean {
    if (!PlayerChangeProfessionEvent(this, profession, prof, source).callEvent())
        return false
    if (prof == ProfessionInfo.NONRP) nonRPTeam.addPlayer(this)
    else {
        if (source == ProfessionSource.TAKEN) uptime.lastProfChange = System.currentTimeMillis()
        RPTeam.addPlayer(this)
        walkSpeed = .2f
    }
    profession = prof.constructor(this, source)
    setSkin(prof.skin, prof.skinModel)
    gameMode = GameMode.SURVIVAL
    tpToProfSpawnAsync()
    return true
}
// @formatter:on

fun Player.setSkin(skin: String, model: SkinModel = SkinModel.CLASSIC) {
    playerProfile = playerProfile.apply {
        setTextures(textures.apply { setSkin(URI(skin).toURL(), model) })
    }
}

private val displayKey = NamespacedKey(plugin, "display")
fun Player.updateGUI() {
    sendPlayerListFooter(miniMessage("<dark_gray>Текущий онлайн: <gold>${server.onlinePlayers.size}"))
    sendActionBar(
        miniMessage("<gray>Профессия: <dark_aqua>${profession.info.displayName} <gold>$balance$")
    )
    val item = inventory.itemInMainHand
    val meta = item.itemMeta ?: return
    val pdc = meta.persistentDataContainer
    val firearm = FirearmDataType.read(pdc) ?: return
    val state = FirearmStateDataType.read(pdc) ?: return
    val dsp = pdc.get(displayKey, PersistentDataType.BOOLEAN) ?: false
    pdc.set(displayKey, PersistentDataType.BOOLEAN, !dsp)
    val fn = fullName(firearm.displayName, state.ammo)
    item.itemMeta = meta.apply {
        itemName(if (dsp) fn else fn.append("".colored(GRAY)))
    }
}

fun CommandSender.checkUnsafe(permission: Permission) =
    if (isOp) true
    else if (this !is Player) false
    else {
        val acc = accounts[name]
        acc != null && acc.rank in permission
    }