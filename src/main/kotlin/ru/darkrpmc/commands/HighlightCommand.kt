package ru.darkrpmc.commands

import com.mojang.brigadier.arguments.StringArgumentType.getString
import com.mojang.brigadier.arguments.StringArgumentType.word
import com.mojang.brigadier.exceptions.CommandSyntaxException.BUILT_IN_EXCEPTIONS
import io.papermc.paper.command.brigadier.Commands.argument
import io.papermc.paper.command.brigadier.Commands.literal
import io.papermc.paper.command.brigadier.argument.ArgumentTypes.entities
import io.papermc.paper.command.brigadier.argument.resolvers.selector.EntitySelectorArgumentResolver
import net.minecraft.commands.CommandSourceStack.ERROR_NOT_PLAYER
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket
import net.minecraft.network.syncher.EntityDataAccessor
import net.minecraft.network.syncher.EntityDataSerializers
import org.bukkit.Particle
import org.bukkit.craftbukkit.entity.CraftEntity
import org.bukkit.craftbukkit.entity.CraftPlayer
import org.bukkit.entity.Player
import ru.darkrpmc.*
import ru.darkrpmc.data.config.Settings
import ru.darkrpmc.world.particles.drawCuboid
import ru.darkrpmc.world.regions.CuboidRegion
import kotlin.experimental.or

val regionHighlighting = hashMapOf<String, HashSet<CuboidRegion>>()

// @formatter:off
@Suppress("UnstableApiUsage")
val highlightCommand = literal("highlight")
    .requires {
        it.sender.checkUnsafe(Permission.HighlightEntities) || it.sender.checkUnsafe(Permission.HighlightRegions)
    }
    .then(literal("region")
        .requires { it.sender.checkUnsafe(Permission.HighlightRegions) }
        .then(argument("id", word())
            .suggests { _, builder ->
                builder
                    .suggest("spawn-zone").suggest("play-zone")
                    .suggest("bank-zone").suggest("city-hall-zone")
                    .buildFuture()
            }
            .executes { ctx ->
                val player = ctx.source.sender
                if (player !is Player) return@executes 0
                regionHighlighting.computeIfAbsent(player.name) { hashSetOf() }.add(
                    when (getString(ctx, "id")) {
                        "spawn-zone" -> Settings.World.spawnZone
                        "play-zone" -> Settings.World.playZone
                        "bank-zone" -> Settings.World.bank
                        "city-hall-zone" -> Settings.World.cityHall
                        else -> throw BUILT_IN_EXCEPTIONS.dispatcherUnknownArgument().create()
                    }
                )
                return@executes 1
            }
        )
    )
    .then(literal("entities")
        .requires { it.sender.checkUnsafe(Permission.HighlightEntities) }
        .then(argument("подсвечиваемые сущности", entities())
            .executes { ctx ->
                val src = ctx.source
                val sender = src.sender
                if (sender !is CraftPlayer) throw ERROR_NOT_PLAYER.create()
                val serverSender = sender.handle
                val targets = ctx.getArgument(
                    "подсвечиваемые сущности", EntitySelectorArgumentResolver::class.java
                ).resolve(src).filter { sender.canSee(it) }
                val entityFlags = EntityDataAccessor(0, EntityDataSerializers.BYTE)
                targets.forEach {
                    val serverTarget = (it as CraftEntity).handle
                    val data = serverTarget.entityData
                    data.set(entityFlags, data.get(entityFlags) or 0x40)
                    serverSender.connection.sendPacket(
                        ClientboundSetEntityDataPacket(serverTarget.id, data.nonDefaultValues!!)
                    )
                }
                return@executes targets.size
            }
        )
    ).build()
// @formatter:on

fun runRegHighlighter() {
    scheduler.runTaskTimer(plugin, { ->
        val iterator = regionHighlighting.iterator()
        while (iterator.hasNext()) {
            val entry = iterator.next()
            val name = entry.key
            val player = server.getPlayer(name)
            if (player == null) regionHighlighting.remove(name)
            else entry.value.forEach {
                drawCuboid(it, Particle.FLAME, player)
            }
        }
    }, 20, 20)
}