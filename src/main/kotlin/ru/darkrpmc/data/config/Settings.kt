package ru.darkrpmc.data.config

import org.bukkit.Location
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType.NAUSEA
import org.bukkit.potion.PotionEffectType.SLOWNESS
import ru.darkrpmc.world
import ru.darkrpmc.world.regions.CuboidRegion
import java.net.URI
import java.net.URL

/** Настройки плагина */
object Settings {
    /** Настройки отвечающие за профессии */
    object Professions {
        /** Точка появления игроков состоящих в силовых структурах */
        var policeSpawn = Location(world, 44.0, 75.0, 110.0)

        /** Точка появления игроков состоящих в правительстве */
        var cityHallSpawn = Location(world, 134.0, 77.0, 19.0)

        /** Точка появления маньяка */
        var undergroundSpawn = Location(world, -8.0, 66.0, 78.0)

        /** Точка появления грузчика */
        var loaderSpawn = Location(world, 127.0, 75.0, -30.0)

        /** Точка появления военных */
        var armySpawn = Location(world, -9.0, 75.0, 311.0)

        /** Кулдаун (мс) на смену профессии */
        var changeCooldown = 3 * 60 * 1000
    }

    /** Настройки отвечающие за игровой мир */
    object World {
        /** Точка появления игроков при заходе (и возрождении, если спавн не переопределён профессией) */
        var spawn = Location(world, .0, 75.0, .0)

        /** Зона спавна */
        var spawnZone = CuboidRegion(
            -6.0, 75.0, -6.0,
            5.0, 78.0, 8.0
        )

        /** Игровая зона */
        var playZone = CuboidRegion(
            -75.0, -64.0, -54.0,
            226.0, 128.0, 327.0
        )

        /** Зона банка */
        var bank = CuboidRegion(
            45.0, 75.0, -31.0,
            57.0, 82.0, -15.0
        )

        /** Зона мэрии */
        var cityHall = CuboidRegion(
            132.0, 77.0, 14.0,
            150.0, 81.0, 45.0
        )

        var payphone = Location(world, 73.0, 75.0, 21.0)
    }

    object Items {
        /** Текстура головы, используемая для предмета рюкзака */
        var backpackTexture: URL =
            URI("http://textures.minecraft.net/texture/17114065a3c95c5d225188ad7bfdaa9b8b0845df4e36c24b45347fd7470a7")
                .toURL()
    }

    /** Настройки отвечающие за взаимодействия игроков */
    object PlayerInteract {
        /** Максимальное расстояние (м) с которого можно надеть наручники/стяжки на игрока */
        var cuffDistance = 3.0

        /** Длительность (т) оглушения от удара шокером */
        var shockerDuration = 30 * 20L

        /** Длительность (с) иммунитета к ограблениям */
        var robberyCooldown = 5 * 60

        /** Эффекты применяемые к оглушённым шокером игрокам */
        var stunEffects = setOf(PotionEffect(NAUSEA, 4 * 20, 0, true, false, false))

        /** Эффекты применяемые к игрокам в наручниках */
        var cuffEffects = setOf(PotionEffect(SLOWNESS, 2 * 20, 5, true, false, false))
    }

    /** Настройки отвечающие за безопасность */
    object Security {
        /** Требуемая версия мода */
        var modVersion = "2"

        /** Список IP-адресов которые могут заходить на OP аккаунты */
        var opsIPs = emptySet<String>()
    }
}