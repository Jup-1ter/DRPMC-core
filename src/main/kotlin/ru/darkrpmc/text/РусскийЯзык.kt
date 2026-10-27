@file:Suppress("NonAsciiCharacters", "PropertyName", "ClassName", "LocalVariableName", "FunctionName", "EnumEntryName")

package ru.darkrpmc.text

enum class Падеж(
    val окончаниеЧислительного1: String,
    val окончаниеЧислительного2: String,
    val окончаниеЧислительного3: String
) {
    Именительный("ый", "ой", "ий"),
    Родительный("ого", "ого", "его"),
    Дательный("ому", "ому", "ему"),
    Винительный("ый", "ой", "ий"),
    Творительный("ым", "ым", "им"),
    Предложный("ом", "ом", "ем");
}

fun Int.окончПорядк(падеж: Падеж = Падеж.Именительный): String {

    val lastTwoDigits = this % 100
    val lastDigit = this % 10

    return when {
        lastTwoDigits in 11..19 -> падеж.окончаниеЧислительного1
        lastDigit == 0 || lastDigit == 2 || lastDigit in 6..8 -> падеж.окончаниеЧислительного2
        lastDigit == 3 -> падеж.окончаниеЧислительного3
        else -> падеж.окончаниеЧислительного1
    }
}