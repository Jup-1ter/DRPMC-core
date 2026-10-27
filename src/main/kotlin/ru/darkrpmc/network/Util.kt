package ru.darkrpmc.network

import ru.darkrpmc.professions.*

fun doNetworkPrep() {
    registerChannels()
    formProfessionsBytes()
}

private fun formProfessionsBytes() {
    professionsBytes = buildString {
        for (prof in ProfessionInfo.entries) {
            append(
                """{
                "id":"$prof",
                "displayName":"${prof.displayName}",
                "category":"${prof.category}",
                "description":"${prof.description}",
                "slots":${prof.slots}},""".trimIndent()
            )
        }
        deleteCharAt(lastIndex).append("]")
    }.toByteArray()
}