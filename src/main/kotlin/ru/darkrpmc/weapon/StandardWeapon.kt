package ru.darkrpmc.weapon

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor.*
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.attribute.Attribute
import org.bukkit.attribute.AttributeModifier
import org.bukkit.inventory.*
import ru.darkrpmc.data.persistent.FirearmDataType
import ru.darkrpmc.data.persistent.FirearmStateDataType
import ru.darkrpmc.plugin
import ru.darkrpmc.text.colored

// @formatter:off
enum class StandardWeapon(
    val displayName: Component,
    private val weaponType: WeaponType,
    private val model: NamespacedKey,
    val startDamage: Float,
    val damageLossStart: Float,
    val damageLossEnd: Float,
    val endDamage: Float,
    val bullets: Short,
    val headshotMultiplier: Float,
    val fireRate: Short,
    val cooling: Float,
    val spreadLensDistance: Float,
    val spreadLensRadius: Float,
    val smallSpreadLensRadius: Float,
    val scope: Boolean,
    val magazine: Short,
    val recharge: Float,
    val singleRecharging: Boolean = false,
    val ammoType: AmmoType,
    val deploy: Float,
    val rayLength: Float = 400f,
    val function: String? = null
) {
    // TODO хэдшот, изготовка, разброс, уменьш. разброс, кулинг, разброс, дробь
    // Шокер
    TASER("Шокер".colored(DARK_AQUA), WeaponType.TASER,
        NamespacedKey(plugin, "weapon/taser"),
        0f, 0f, 1f, 0f, 1, 1f, 600, 1f, 1f, 0f, 0f, false, 1, 2.5f, false, AmmoType.TASER, 1.5f, 10f, "stun"),
    // Пистолеты
    DESERT_EAGLE("Desert Eagle".colored(GRAY), WeaponType.PISTOL,
        NamespacedKey(plugin, "weapon/firearms/pistols/desert_eagle"),
        7f, 10f, 70f, 1f, 1, 1f, 120, 2f, 5f, 3f, 3f, false, 8, 4f, false, AmmoType.PISTOL, 1f),
    G17("Glock 17".colored(GRAY), WeaponType.PISTOL,
        NamespacedKey(plugin, "weapon/firearms/pistols/g17"),
        6f, 10f, 70f, 1f, 1, 1f, 120, 2f, 5f, 3f, 3f, false, 17, 3f, false, AmmoType.PISTOL, 1f),
    G17_POLICE("(P) ".colored(BLUE).append("Glock".colored(GRAY)), WeaponType.PISTOL,
        NamespacedKey(plugin, "weapon/firearms/pistols/g17_police"),
        6f, 10f, 70f, 1f, 1, 1f, 120, 2f, 5f, 3f, 3f, false, 17, 3f, false, AmmoType.PISTOL, 1f),
    LEMAT("Lemat".colored(GRAY), WeaponType.SUBMACHINE,
        NamespacedKey(plugin, "weapon/firearms/pistols/lemat"),
        8f, 10f, 70f, 1f, 1, 1f, 132, 2f, 5f, 3f, 3f, false, 8, 4f, false, AmmoType.PISTOL, 1f),
    LEMAT_POLICE("(P) ".colored(BLUE).append("Lemat".colored(GRAY)), WeaponType.PISTOL,
        NamespacedKey(plugin, "weapon/firearms/pistols/lemat_police"),
        8f, 10f, 70f, 1f, 1, 1f, 132, 2f, 5f, 3f, 3f, false, 8, 4f, false, AmmoType.PISTOL, 1f),
    BERETTA("Beretta M9".colored(GRAY), WeaponType.SUBMACHINE,
        NamespacedKey(plugin, "weapon/firearms/pistols/beretta"),
        5f, 10f, 70f, 1f, 1, 1f, 120, 2f, 5f, 3f, 3f, false, 12, 2.1f, false, AmmoType.PISTOL, 1f),
    BERETTA_POLICE("(P) ".colored(BLUE).append("Beretta M9".colored(GRAY)), WeaponType.PISTOL,
        NamespacedKey(plugin, "weapon/firearms/pistols/beretta_police"),
        5f, 10f, 70f, 1f, 1, 1f, 120, 2f, 5f, 3f, 3f, false, 12, 2.1f, false, AmmoType.PISTOL, 1f),
    MAGNUM("Magnum".colored(GRAY), WeaponType.PISTOL,
        NamespacedKey(plugin, "weapon/firearms/pistols/magnum"),
        9f, 10f, 70f, 1f, 1, 1f, 120, 2f, 5f, 3f, 3f, false, 6, 4f, false, AmmoType.PISTOL, 1f),
    MAGNUM_POLICE("(P) ".colored(BLUE).append("Magnum".colored(GRAY)), WeaponType.PISTOL,
        NamespacedKey(plugin, "weapon/firearms/pistols/magnum_police"),
        9f, 10f, 70f, 1f, 1, 1f, 120, 2f, 5f, 3f, 3f, false, 6, 4f, false, AmmoType.PISTOL, 1f),
    // Пистолеты-пулемёты
    BIZON("Bizon".colored(GRAY), WeaponType.SUBMACHINE,
        NamespacedKey(plugin, "weapon/firearms/submachine_guns/bizon"),
        2f, 10f, 70f, 1f, 1, 1f, 450, 2f, 5f, 3f, 3f, false, 64, 6f, false, AmmoType.SUBMACHINE, 1f),
    MAC10("MAC-10".colored(GRAY), WeaponType.SUBMACHINE,
        NamespacedKey(plugin, "weapon/firearms/submachine_guns/mac10"),
        2f, 10f, 70f, 1f, 1, 1f, 300, 2f, 5f, 3f, 3f, false, 32, 4f, false, AmmoType.SUBMACHINE, 1f),
    MPX("SIG MPX".colored(GRAY), WeaponType.SUBMACHINE,
        NamespacedKey(plugin, "weapon/firearms/submachine_guns/mpx"),
        1f, 10f, 70f, 1f, 1, 1f, 750, 2f, 5f, 3f, 3f, false, 30, 4f, false, AmmoType.SUBMACHINE, 1f),
    MPX_POLICE("(P) ".colored(BLUE).append("SIG MPX".colored(GRAY)), WeaponType.SUBMACHINE,
        NamespacedKey(plugin, "weapon/firearms/submachine_guns/mpx_police"),
        1f, 10f, 70f, 1f, 1, 1f, 850, 2f, 5f, 3f, 3f, false, 30, 4f, false, AmmoType.SUBMACHINE, 1f),
    P90("P90".colored(GRAY), WeaponType.SUBMACHINE,
        NamespacedKey(plugin, "weapon/firearms/submachine_guns/p90"),
        1f, 10f, 70f, 1f, 1, 1f, 750, 2f, 5f, 3f, 3f, false, 50, 4f, false, AmmoType.SUBMACHINE, 1f),
    P90_POLICE("(P) ".colored(BLUE).append("P90".colored(GRAY)), WeaponType.SUBMACHINE,
        NamespacedKey(plugin, "weapon/firearms/submachine_guns/p90_police"),
        1f, 10f, 70f, 1f, 1, 1f, 750, 2f, 5f, 3f, 3f, false, 50, 4f, false, AmmoType.SUBMACHINE, 1f),
    SCORPION("Šcorpion".colored(GRAY), WeaponType.SUBMACHINE,
        NamespacedKey(plugin, "weapon/firearms/submachine_guns/scorpion"),
        2f, 10f, 70f, 1f, 1, 1f, 400, 2f, 5f, 3f, 3f, false, 20, 4f, false, AmmoType.SUBMACHINE, 1f),
    // TODO TMP("T".colored(GRAY)), NamespacedKey(plugin, "weapon/firearms/submachine_guns/"),
    THOMPSON("Thompson".colored(GRAY), WeaponType.SUBMACHINE,
        NamespacedKey(plugin, "weapon/firearms/submachine_guns/thompson"),
        1f, 10f, 70f, 1f, 1, 1f, 600, 2f, 5f, 3f, 3f, false, 50, 4f, false, AmmoType.SUBMACHINE, 1f),
    UMP45("UMP-45".colored(GRAY), WeaponType.SUBMACHINE,
        NamespacedKey(plugin, "weapon/firearms/submachine_guns/ump45"),
        2f, 10f, 70f, 1f, 1, 1f, 400, 2f, 5f, 3f, 3f, false, 25, 4f, false, AmmoType.SUBMACHINE, 1f),
    UZI("Uzi".colored(GRAY), WeaponType.SUBMACHINE,
        NamespacedKey(plugin, "weapon/firearms/submachine_guns/uzi"),
        1f, 10f, 70f, 1f, 1, 1f, 600, 2f, 5f, 3f, 3f, false, 32, 4f, false, AmmoType.SUBMACHINE, 1f),
    VECTOR("Vector".colored(GRAY), WeaponType.SUBMACHINE,
        NamespacedKey(plugin, "weapon/firearms/submachine_guns/vector"),
        1.5f, 10f, 70f, 1f, 1, 1f, 850, 2f, 5f, 3f, 3f, false, 32, 4f, false, AmmoType.SUBMACHINE, 1f),
    // Дробовики
    AA12("AA-12".colored(GRAY), WeaponType.SHOTGUN,
        NamespacedKey(plugin, "weapon/firearms/shotguns/aa12"),
        1f, 10f, 70f, 1f, 6, 1f, 336, 2f, 5f, 3f, 3f, false, 20, 4f, false, AmmoType.SHOTGUN, 1f),
    BROWNING("Browning".colored(GRAY), WeaponType.SHOTGUN,
        NamespacedKey(plugin, "weapon/firearms/shotguns/browning"),
        3f, 10f, 70f, 1f, 6, 1f, 70, 2f, 5f, 3f, 3f, false, 4, 1f, true, AmmoType.SHOTGUN, 1f),
    DP12("DP-12".colored(GRAY), WeaponType.SHOTGUN,
        NamespacedKey(plugin, "weapon/firearms/shotguns/dp12"),
        3f, 10f, 70f, 1f, 6, 1f, 150, 2f, 5f, 3f, 3f, false, 14, 1f, true, AmmoType.SHOTGUN, 1f),
    M4S90("Benelli M4S90".colored(GRAY), WeaponType.SHOTGUN,
        NamespacedKey(plugin, "weapon/firearms/shotguns/m4s90"),
        3f, 10f, 70f, 1f, 6, 1f, 170, 2f, 5f, 3f, 3f, false, 6, 1f, true, AmmoType.SHOTGUN, 1f),
    M4S90_POLICE("(P) ".colored(BLUE).append("Benelli M4S90".colored(GRAY)), WeaponType.SHOTGUN,
        NamespacedKey(plugin, "weapon/firearms/shotguns/m4s90_police"),
        3f, 10f, 70f, 1f, 6, 1f, 170, 2f, 5f, 3f, 3f, false, 6, 1f, true, AmmoType.SHOTGUN, 1f),
    DOUBLE_BARREL("Double Barrel".colored(GRAY), WeaponType.SHOTGUN,
        NamespacedKey(plugin, "weapon/firearms/shotguns/double_barrel"),
        4f, 10f, 70f, 1f, 6, 1f, 1300, 2f, 5f, 3f, 3f, false, 2, 6f, true, AmmoType.SHOTGUN, 1f),
    OLYMPIA("Olympia".colored(GRAY), WeaponType.SHOTGUN,
        NamespacedKey(plugin, "weapon/firearms/shotguns/olympia"),
        3f, 10f, 70f, 1f, 6, 1f, 212, 2f, 5f, 3f, 3f, false, 2, 4f, true, AmmoType.SHOTGUN, 1f),
    STRIKER("Striker".colored(GRAY), WeaponType.SHOTGUN,
        NamespacedKey(plugin, "weapon/firearms/shotguns/striker"),
        2f, 10f, 70f, 1f, 6, 1f, 250, 2f, 5f, 3f, 3f, false, 12, 1f, true, AmmoType.SHOTGUN, 1f),
    // Автоматы
    AK74("АК-74".colored(GRAY), WeaponType.ASSAULT,
        NamespacedKey(plugin, "weapon/firearms/assault/ak74"),
        2f, 10f, 70f, 1f, 1, 1f, 650, 2f, 5f, 3f, 3f, false, 30, 5f, false, AmmoType.RIFLE, 1f),
    AK74_POLICE("(P) ".colored(BLUE).append("АК-74".colored(GRAY)), WeaponType.ASSAULT,
        NamespacedKey(plugin, "weapon/firearms/assault/ak74_police"),
        10f, 70f, 1f, 2f, 1, 1f, 650, 2f, 5f, 3f, 3f, false, 30, 5f, false, AmmoType.RIFLE, 1f),
    AUG("AUG".colored(GRAY), WeaponType.ASSAULT,
        NamespacedKey(plugin, "weapon/firearms/assault/aug"),
        2f, 10f, 70f, 1f, 1, 1f, 600, 2f, 5f, 3f, 3f, false, 40, 5f, false, AmmoType.RIFLE, 1f),
    AUG_POLICE("(P) ".colored(BLUE).append("АUG".colored(GRAY)), WeaponType.ASSAULT,
        NamespacedKey(plugin, "weapon/firearms/assault/aug_police"),
        2f, 10f, 70f, 1f, 1, 1f, 600, 2f, 5f, 3f, 3f, false, 40, 5f, false, AmmoType.RIFLE, 1f),
    GALIL("Galil ACE".colored(GRAY), WeaponType.ASSAULT,
        NamespacedKey(plugin, "weapon/firearms/assault/galil"),
        3f, 10f, 70f, 1f, 1, 1f, 312, 2f, 5f, 3f, 3f, false, 30, 5f, false, AmmoType.RIFLE, 1f),
    GALIL_POLICE("(P) ".colored(BLUE).append("Galil ACE".colored(GRAY)), WeaponType.ASSAULT,
        NamespacedKey(plugin, "weapon/firearms/assault/galil_police"),
        3f, 10f, 70f, 1f, 1, 1f, 312, 2f, 5f, 3f, 3f, false, 30, 5f, false, AmmoType.RIFLE, 1f),
    EM2("Enfield EM-2".colored(GRAY), WeaponType.ASSAULT,
        NamespacedKey(plugin, "weapon/firearms/assault/em2"),
        2f, 10f, 70f, 1f, 1, 1f, 450, 2f, 5f, 3f, 3f, false, 20, 5f, false, AmmoType.RIFLE, 1f),
    F2000("FN F2000".colored(GRAY), WeaponType.ASSAULT,
        NamespacedKey(plugin, "weapon/firearms/assault/f2000"),
        1f, 10f, 70f, 1f, 1, 1f, 850, 2f, 5f, 3f, 3f, false, 30, 5f, false, AmmoType.RIFLE, 1f),
    FAMAS("FAMAS".colored(GRAY), WeaponType.ASSAULT,
        NamespacedKey(plugin, "weapon/firearms/assault/famas"),
        1f, 10f, 70f, 1f, 1, 1f, 850, 2f, 5f, 3f, 3f, false, 30, 5f, false, AmmoType.RIFLE, 1f),
    FAMAS_POLICE("(P) ".colored(BLUE).append("FAMAS".colored(GRAY)), WeaponType.ASSAULT,
        NamespacedKey(plugin, "weapon/firearms/assault/famas_police"),
        1f, 10f, 70f, 1f, 1, 1f, 850, 2f, 5f, 3f, 3f, false, 30, 5f, false, AmmoType.RIFLE, 1f),
    FFAR("FFAR".colored(GRAY), WeaponType.ASSAULT,
        NamespacedKey(plugin, "weapon/firearms/assault/ffar"),
        2f, 10f, 70f, 1f, 1, 1f, 600, 2f, 5f, 3f, 3f, false, 30, 5f, false, AmmoType.RIFLE, 1f),
    FFAR_POLICE("(P) ".colored(BLUE).append("FFAR".colored(GRAY)), WeaponType.ASSAULT,
        NamespacedKey(plugin, "weapon/firearms/assault/ffar_police"),
        2f, 10f, 70f, 1f, 1, 1f, 600, 2f, 5f, 3f, 3f, false, 30, 5f, false, AmmoType.RIFLE, 1f),
    G36("HK G36".colored(GRAY), WeaponType.ASSAULT,
        NamespacedKey(plugin, "weapon/firearms/assault/g36"),
        2f, 10f, 70f, 1f, 1, 1f, 750, 2f, 5f, 3f, 3f, false, 30, 5f, false, AmmoType.RIFLE, 1f),
    M4("M4".colored(GRAY), WeaponType.ASSAULT,
        NamespacedKey(plugin, "weapon/firearms/assault/m4"),
        2f, 10f, 70f, 1f, 1, 1f, 700, 2f, 5f, 3f, 3f, false, 30, 5f, false, AmmoType.RIFLE, 1f),
    M4_POLICE("(P) ".colored(BLUE).append("M4".colored(GRAY)), WeaponType.ASSAULT,
        NamespacedKey(plugin, "weapon/firearms/assault/m4_police"),
        2f, 10f, 70f, 1f, 1, 1f, 700, 2f, 5f, 3f, 3f, false, 30, 5f, false, AmmoType.RIFLE, 1f),
    SCAR("FN SCAR".colored(GRAY), WeaponType.ASSAULT,
        NamespacedKey(plugin, "weapon/firearms/assault/scar"),
        3f, 10f, 70f, 1f, 1, 1f, 625, 2f, 5f, 3f, 3f, false, 30, 5f, false, AmmoType.RIFLE, 1f),
    AKS74U("AKS-74U".colored(GRAY), WeaponType.ASSAULT,
        NamespacedKey(plugin, "weapon/firearms/assault/aks74u"),
        3f, 10f, 70f, 1f, 1, 1f, 250, 2f, 5f, 3f, 3f, false, 30, 4f, false, AmmoType.RIFLE, 1f),
    // Пулемёты
    M60("M60".colored(GRAY), WeaponType.MACHINE,
        NamespacedKey(plugin, "weapon/firearms/machine_guns/m60"),
        1f, 10f, 70f, 1f, 1, 1f, 550, 2f, 5f, 3f, 3f, false, 100, 7f, false, AmmoType.RIFLE, 1f),
    M60_POLICE("(P) ".colored(BLUE).append("M60".colored(GRAY)), WeaponType.MACHINE,
        NamespacedKey(plugin, "weapon/firearms/machine_guns/m60_police"),
        1f, 10f, 70f, 1f, 1, 1f, 550, 2f, 5f, 3f, 3f, false, 100, 7f, false, AmmoType.RIFLE, 1f),
    // TODO MG82("MG82".colored(GRAY)), NamespacedKey(plugin, "weapon/firearms/machine_guns/"),
    MG338("MG 338".colored(GRAY), WeaponType.MACHINE,
        NamespacedKey(plugin, "weapon/firearms/machine_guns/mg338"),
        2f, 10f, 70f, 1f, 1, 1f, 600, 2f, 5f, 3f, 3f, false, 100, 7f, false, AmmoType.RIFLE, 1f),
    PKM("PKM".colored(GRAY), WeaponType.MACHINE,
        NamespacedKey(plugin, "weapon/firearms/machine_guns/pkm"),
        4f, 10f, 70f, 1f, 1, 1f, 250, 2f, 5f, 3f, 3f, false, 200, 7f, false, AmmoType.RIFLE, 1f),
    RPK("RPK".colored(GRAY), WeaponType.MACHINE,
        NamespacedKey(plugin, "weapon/firearms/machine_guns/rpk"),
        1f, 10f, 70f, 1f, 1, 1f, 600, 2f, 5f, 3f, 3f, false, 75, 7f, false, AmmoType.RIFLE, 1f),
    BAR("BAR".colored(GRAY), WeaponType.MACHINE,
        NamespacedKey(plugin, "weapon/firearms/machine_guns/bar"),
        1f, 10f, 70f, 1f, 1, 1f, 650, 2f, 5f, 3f, 3f, false, 50, 7f, false, AmmoType.RIFLE, 1f),
    // Снайперские винтовки
    SVD("SVD".colored(GRAY), WeaponType.SNIPER,
        NamespacedKey(plugin, "weapon/firearms/sniper/svd"),
        10f, 10f, 70f, 1f, 1, 1f, 120, 2f, 5f, 3f, 3f, true, 10, 5f, false, AmmoType.SNIPER, 1f),
    ENFIELD("Enfield".colored(GRAY), WeaponType.SNIPER,
        NamespacedKey(plugin, "weapon/firearms/sniper/enfield"),
        20f, 10f, 70f, 1f, 1, 1f, 20, 3.5f, 5f, 3f, 3f, true, 10, 5f, false, AmmoType.SNIPER, 1f),
    M24("M24".colored(GRAY), WeaponType.SNIPER,
        NamespacedKey(plugin, "weapon/firearms/sniper/m24"),
        10f, 10f, 70f, 1f, 1, 1f, 120, 2f, 5f, 3f, 3f, true, 5, 5f, false, AmmoType.SNIPER, 1f),
    VSS("VSS Vintorez".colored(GRAY), WeaponType.SNIPER,
        NamespacedKey(plugin, "weapon/firearms/sniper/vss"),
        10f, 10f, 70f, 1f, 1, 1f, 100, 2f, 5f, 3f, 3f, true, 20, 5f, false, AmmoType.SNIPER, 1f),
    AWP("AWP".colored(GRAY), WeaponType.SNIPER,
        NamespacedKey(plugin, "weapon/firearms/sniper/awp"),
        20f, 10f, 70f, 1f, 1, 1f, 60, 2f, 5f, 3f, 3f, true, 10, 5f, false, AmmoType.SNIPER, 1f),
    AWP_POLICE("(P) ".colored(BLUE).append("AWP".colored(GRAY)), WeaponType.SNIPER,
        NamespacedKey(plugin, "weapon/firearms/sniper/awp_police"),
        20f, 10f, 70f, 1f, 1, 1f, 60, 2f, 5f, 3f, 3f, true, 10, 5f, false, AmmoType.SNIPER, 1f);

    private val firearm = StandardFirearm(this)

    val item get() = ItemStack.of(Material.CROSSBOW).also {
        it.editMeta { meta ->
            meta.itemName(displayName)
            meta.lore(listOf(
                "Тип: ".colored(DARK_GRAY).append(weaponType.displayName),
                "Боеприпасы: ".colored(DARK_GRAY).append(ammoType.displayName),
            ))
            FirearmDataType.write(meta.persistentDataContainer, this@StandardWeapon.firearm)
            FirearmStateDataType.write(meta.persistentDataContainer, FirearmState())
            meta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP, ItemFlag.HIDE_ATTRIBUTES)
            meta.addAttributeModifier(Attribute.ATTACK_SPEED, AttributeModifier(
                attackSpeedDecreaseKey, -2.5, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.MAINHAND
            ))
            meta.itemModel = model
        }
    }

    private enum class WeaponType(val displayName: Component) {
        PISTOL("Пистолет".colored(DARK_AQUA)),
        SUBMACHINE("Пистолет-пулемёт".colored(DARK_AQUA)),
        SHOTGUN("Дробовик".colored(DARK_AQUA)),
        ASSAULT("Винтовка".colored(DARK_AQUA)),
        MACHINE("Пулемёт".colored(DARK_AQUA)),
        SNIPER("Снайпер".colored(DARK_AQUA)),
        TASER("Электрошокер".colored(DARK_AQUA))
    }
}
private val attackSpeedDecreaseKey = NamespacedKey(plugin, "attack-speed-decrease")
// @formatter:on