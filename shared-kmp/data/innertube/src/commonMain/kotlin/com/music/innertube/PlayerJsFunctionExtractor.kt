package com.music.innertube

internal data class SignatureFunctionInfo(
    val name: String,
    val constantArgument: Int?,
)

internal data class ThrottlingFunctionInfo(
    val name: String,
    val arrayIndex: Int?,
)

internal object PlayerJsFunctionExtractor {
    private val signatureTimestampPattern = Regex("""signatureTimestamp[=:](\d+)""")
    private val signatureFunctionPatterns = listOf(
        Regex("""&&\s*\(\s*[a-zA-Z0-9$]+\s*=\s*([a-zA-Z0-9$]+)\s*\(\s*(\d+)\s*,\s*decodeURIComponent\s*\(\s*[a-zA-Z0-9$]+\s*\)"""),
        Regex("""\b[cs]\s*&&\s*[adf]\.set\([^,]+\s*,\s*encodeURIComponent\(([a-zA-Z0-9$]+)\("""),
        Regex("""\b[a-zA-Z0-9]+\s*&&\s*[a-zA-Z0-9]+\.set\([^,]+\s*,\s*encodeURIComponent\(([a-zA-Z0-9$]+)\("""),
        Regex("""\bm=([a-zA-Z0-9${'$'}]{2,})\(decodeURIComponent\(h\.s\)\)"""),
        Regex("""\bc\s*&&\s*d\.set\([^,]+\s*,\s*(?:encodeURIComponent\s*\()([a-zA-Z0-9$]+)\("""),
        Regex("""\bc\s*&&\s*[a-z]\.set\([^,]+\s*,\s*encodeURIComponent\(([a-zA-Z0-9$]+)\("""),
    )
    private val throttlingFunctionPatterns = listOf(
        Regex("""\.get\("n"\)\)&&\(b=([a-zA-Z0-9$]+)(?:\[(\d+)\])?\(([a-zA-Z0-9])\)"""),
        Regex("""\.get\("n"\)\)\s*&&\s*\(([a-zA-Z0-9$]+)\s*=\s*([a-zA-Z0-9$]+)(?:\[(\d+)\])?\(\1\)"""),
        Regex("""\(\s*([a-zA-Z0-9$]+)\s*=\s*String\.fromCharCode\(110\)"""),
        Regex("""([a-zA-Z0-9$]+)\s*=\s*function\([a-zA-Z0-9]\)\s*\{[^}]*?enhanced_except_"""),
    )

    fun signatureTimestamp(playerJs: String): Int? =
        signatureTimestampPattern.find(playerJs)?.groupValues?.getOrNull(1)?.toIntOrNull()

    fun signatureFunction(playerJs: String): SignatureFunctionInfo? {
        for (pattern in signatureFunctionPatterns) {
            val match = pattern.find(playerJs) ?: continue
            val name = match.groupValues.getOrNull(1)?.takeIf(String::isNotBlank) ?: continue
            return SignatureFunctionInfo(
                name = name,
                constantArgument = match.groupValues.getOrNull(2)?.toIntOrNull(),
            )
        }
        return null
    }

    fun throttlingFunction(playerJs: String): ThrottlingFunctionInfo? {
        for ((index, pattern) in throttlingFunctionPatterns.withIndex()) {
            val match = pattern.find(playerJs) ?: continue
            val nameIndex = if (index == 1) 2 else 1
            val name = match.groupValues.getOrNull(nameIndex)?.takeIf(String::isNotBlank) ?: continue
            val arrayIndex = when (index) {
                0 -> match.groupValues.getOrNull(2)?.toIntOrNull()
                1 -> match.groupValues.getOrNull(3)?.toIntOrNull()
                else -> null
            }
            return ThrottlingFunctionInfo(name, arrayIndex)
        }
        return null
    }
}
