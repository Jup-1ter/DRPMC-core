package ru.darkrpmc.world

import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.*
import net.minecraft.world.level.block.Block.getId
import net.minecraft.world.level.block.LevelEvent.PARTICLES_DESTROY_BLOCK
import net.minecraft.world.level.block.LevelEvent.SOUND_EXTINGUISH_FIRE
import org.bukkit.GameMode.CREATIVE
import org.bukkit.block.data.Bisected.Half.BOTTOM
import org.bukkit.block.data.Bisected.Half.TOP
import org.bukkit.block.data.Powerable
import org.bukkit.block.data.type.Bed
import org.bukkit.block.data.type.Door
import org.bukkit.craftbukkit.block.CraftBlock
import org.bukkit.entity.Player
import org.bukkit.event.Event.Result.DENY
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority.LOWEST
import org.bukkit.event.EventPriority.MONITOR
import org.bukkit.event.Listener
import org.bukkit.event.block.*
import org.bukkit.event.player.*
import org.bukkit.scheduler.BukkitTask
import ru.darkrpmc.*
import ru.darkrpmc.data.config.Settings
import ru.darkrpmc.data.session
import ru.darkrpmc.events.world.regions.PlayerRegionEntryEvent
import ru.darkrpmc.events.world.regions.PlayerRegionLeaveEvent
import ru.darkrpmc.math.geometry.*
import ru.darkrpmc.world.regions.Region
import java.lang.ref.WeakReference

@Suppress("unused")
object WorldEvHandler : Listener {
    val regions = HashSet<WeakReference<Region>>()

    @EventHandler(priority = LOWEST, ignoreCancelled = true)
    fun onPlayerMove(event: PlayerMoveEvent) {
        for (regionRef in regions) {
            val region = regionRef.get() ?: continue
            if (event.from !in region && event.to in region) {
                if (!PlayerRegionEntryEvent(region, event).callEvent())
                    event.isCancelled = true
            } else if (event.from in region && event.to !in region) {
                if (!PlayerRegionLeaveEvent(region, event).callEvent())
                    event.isCancelled = true
            }
        }
    }

    @EventHandler(priority = LOWEST, ignoreCancelled = true)
    fun onRegionLeave(event: PlayerRegionLeaveEvent) {
        val region = event.region
        if (region == Settings.World.playZone) {
            val move = event.move
            val player = event.player
            val toLoc = move.to
            val to = toLoc.point
            val from = move.from.point
            val (r, v) = region.calculateExitNormal(from, to)
            //move.to = r.location(to.yaw, to.pitch)
            val vel = player.velocity
            scheduler.runTask(plugin) { ->
                player.teleport(r.location(toLoc.yaw, toLoc.pitch))
                player.velocity = vel - v * 3 + (to - from)
            }
        }
    }

    @EventHandler(priority = LOWEST)
    fun onPlayerLeave(event: PlayerQuitEvent) = event.player.clearBlocks()

    @EventHandler(priority = MONITOR, ignoreCancelled = true)
    fun onBlockPlace(event: BlockPlaceEvent) {
        val player = event.player
        if (player.gameMode == CREATIVE) return
        if (!event.blockReplacedState.type.isAir) {
            event.isCancelled = true
            return
        }
//        val used = event.itemInHand
        val block = event.blockPlaced
        val loc = block.location
        val point = loc.point
//        if (used.type == Material.STRUCTURE_BLOCK) {
//            val meta = used.itemMeta
//            val printerType = MoneyPrinterType.entries.find { it.model.id == meta.customModelData } ?: return
//            block.type = Material.BARRIER
//            world.spawn(loc, ItemDisplay::class.java) { it.setItemStack(printerType.item) }
//            return
//        } else if (event.isCancelled) return
        val blocks = player.session.blocks
        blocks += point
        when (val data = block.blockData) {
            is Door -> blocks += point.plus(dy = 1.0)
            is Bed -> blocks += point + data.facing.direction
        }
    }

    private val breaking = hashMapOf<Player, BukkitTask>()

    @EventHandler(ignoreCancelled = true)
    fun onBlockDamage(event: BlockDamageEvent) {
        val block = event.block
        val player = event.player
        val blocks = player.session.blocks
        val point = block.location.point
        if (point in blocks)
            breaking[player] = scheduler.runTaskLater(plugin, { ->
                (block as CraftBlock).breakNaturallyWithoutDrop()
                blocks -= point
            }, 10)
        else event.isCancelled = true
    }

    @EventHandler
    fun onBlockDamageAbort(event: BlockDamageAbortEvent) {
        breaking.remove(event.player)?.cancel()
    }

    @EventHandler(ignoreCancelled = true)
    fun onBlockBreak(event: BlockBreakEvent) {
        val point = event.block.location.point
        server.onlinePlayers.forEach { it.session.blocks -= point }
    }

    @EventHandler(ignoreCancelled = true)
    fun onBlockInteract(event: PlayerInteractEvent) {
        val player = event.player
        if (player.gameMode == CREATIVE) return
        val block = event.clickedBlock ?: return
        if (block.blockData is Powerable && block.location.point !in player.session.blocks)
            event.setUseInteractedBlock(DENY)

    }

    @EventHandler(ignoreCancelled = true)
    fun onBlockPhysics(event: BlockPhysicsEvent) {
//        event.isCancelled = true
//        if (event.changedBlockData is Door) {
//            val block = event.block
//            val blockData = block.blockData
//            if (blockData is Door) {
//                val otherHalf = world.getBlockAt(
//                    when (blockData.half) {
//                        TOP -> block.location.subtract(.0, 1.0, .0)
//                        BOTTOM -> block.location.add(.0, 1.0, .0)
//                    }
//                )
//                val otherData = otherHalf.blockData
//                if (otherData is Door) {
//                    otherData.isOpen = blockData.isOpen
//                    otherHalf.blockData = otherData
//                }
//            }
//        }
        logger.info(event.block.type.name)
        logger.info(event.changedBlockData.isSupported(event.block.location).toString())
    }

    private fun CraftBlock.breakNaturallyWithoutDrop() {
        val level = craftWorld.handle
        val levelBlock = nms
        val block = levelBlock.block
//        if (withCheck) {
//            when (val d = blockData) {
//                is Door -> ((
//                        if (d.half == TOP) world.getBlockAt(location.subtract(.0, 1.0, .0))
//                        else world.getBlockAt(location.add(.0, 1.0, .0))
//                        ) as CraftBlock).breakNaturallyWithoutDrop(false)
//                is Bed -> (world.getBlockAt(location.add(d.facing.direction)) as CraftBlock)
//                    .breakNaturallyWithoutDrop(false)
//            }
////            val topBlock = world.getBlockAt(position.x, position.y + 1, position.z)
////            val botBlock = world.getBlockAt()
////            val tobBlockData = topBlock.blockData
////            if (tobBlockData is Door && tobBlockData.half == TOP)
////                (topBlock as CraftBlock).breakNaturallyWithoutDrop(false)
////            val botBlockData = botBlock.blockData
////            if (botBlockData is Door && botBlockData.half == BOTTOM)
////                (botBlock as CraftBlock).breakNaturallyWithoutDrop(false)
//        }
        if (block != Blocks.AIR) {
            if (block is BaseFireBlock) level.levelEvent(SOUND_EXTINGUISH_FIRE, position, 0)
            else level.levelEvent(PARTICLES_DESTROY_BLOCK, this.position, getId(levelBlock))

            if (level.removeBlock(position, false)) block.destroy(level, position, levelBlock)

            when (block) {
                is IceBlock -> block.afterDestroy(level, position, ItemStack.EMPTY)
                is TurtleEggBlock -> block.decreaseEggs(level, position, levelBlock)
            }
        }
    }
}