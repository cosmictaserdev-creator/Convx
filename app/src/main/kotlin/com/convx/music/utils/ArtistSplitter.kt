package com.convx.music.utils

object ArtistSplitter {
    private val keepWhole = listOf(
        "AC/DC",
        "Tyler, the Creator",
        "Earth, Wind & Fire",
        "Crosby, Stills, Nash & Young",
        "Simon & Garfunkel",
        "Simon and Garfunkel",
        "Hall & Oates",
        "Hall and Oates",
        "Brooks & Dunn",
        "Mumford & Sons",
        "Sly & the Family Stone",
        "Echo & the Bunnymen",
        "Belle and Sebastian",
        "Coheed and Cambria",
        "Of Monsters and Men",
    )

    private val separator = Regex(
        """\s*(?:\u0000|;|/|,|&|\bfeat\.?|\bft\.?|\band\b)\s*""",
        RegexOption.IGNORE_CASE,
    )

    private val edgeChars = charArrayOf(' ', '(', ')', '[', ']')

    fun split(raw: String?): List<String> {
        if (raw.isNullOrBlank()) return emptyList()

        var text: String = raw
        val protectedNames = mutableMapOf<String, String>()
        keepWhole.forEachIndexed { index, name ->
            if (text.contains(name, ignoreCase = true)) {
                val placeholder = "\u0001$index\u0001"
                text = text.replace(name, placeholder, ignoreCase = true)
                protectedNames[placeholder] = name
            }
        }

        return text.split(separator)
            .map { part ->
                var restored = part
                protectedNames.forEach { (placeholder, name) ->
                    restored = restored.replace(placeholder, name)
                }
                restored.trim(*edgeChars)
            }
            .filter { it.isNotBlank() }
            .distinct()
    }
}
