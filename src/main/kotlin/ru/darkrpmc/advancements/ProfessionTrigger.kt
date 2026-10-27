package ru.darkrpmc.advancements

import com.mojang.serialization.Codec
import net.minecraft.advancements.critereon.SimpleCriterionTrigger
import net.minecraft.advancements.critereon.SimpleCriterionTrigger.SimpleInstance

class ProfessionTrigger : SimpleCriterionTrigger<SimpleInstance>() {
    override fun codec(): Codec<SimpleInstance> {
        TODO("Not yet implemented")
    }
}