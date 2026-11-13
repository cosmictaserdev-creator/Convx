package com.convx.music.playback

/**
 * Parser for AutoEq ParametricEQ.txt text.
 *
 *   Preamp: -5.2 dB
 *   Filter 1: ON LSC Fc 105 Hz Gain 8.8 dB Q 0.70
 *   Filter 2: ON PK Fc 70 Hz Gain -6.7 dB Q 0.29
 *
 * LSC/HSC are shelves, PK is peaking, LPQ/HPQ are low/high pass.
 */
object EqualizerProfileParser {

    fun parseText(content: String): EqualizerProfile {
        var preamp = 0.0
        val bands = mutableListOf<EqualizerBand>()
        val metadata = mutableMapOf<String, String>()

        for (line in content.lines()) {
            val trimmedLine = line.trim()
            if (trimmedLine.isEmpty()) continue

            when {
                trimmedLine.startsWith("Preamp:", ignoreCase = true) -> preamp = parsePreamp(trimmedLine)
                trimmedLine.startsWith("Filter", ignoreCase = true) -> parseFilterLine(trimmedLine)?.let(bands::add)
                // Preserve unrecognized key/value lines as metadata.
                else -> {
                    val parts = trimmedLine.split(":", limit = 2)
                    if (parts.size == 2) metadata[parts[0].trim()] = parts[1].trim()
                }
            }
        }

        return EqualizerProfile(preamp = preamp, bands = bands, metadata = metadata)
    }

    fun validate(eq: EqualizerProfile): List<String> {
        val errors = mutableListOf<String>()

        if (!eq.preamp.isFinite() || eq.preamp < -50.0 || eq.preamp > 50.0) {
            errors.add("Preamp value ${eq.preamp} dB is out of range (-50 to +50 dB)")
        }
        if (eq.bands.isEmpty()) {
            errors.add("EQ profile must have at least one band")
        }
        if (eq.bands.size > EqualizerProfile.MAX_BANDS) {
            errors.add("EQ profile has ${eq.bands.size} bands, maximum is ${EqualizerProfile.MAX_BANDS}")
        }
        eq.bands.forEachIndexed { index, band ->
            if (!band.frequency.isFinite() || band.frequency <= 0.0 || band.frequency > 100000.0) {
                errors.add("Band ${index + 1}: Frequency ${band.frequency} Hz is out of range (1 to 100000 Hz)")
            }
            if (!band.gain.isFinite() || band.gain < -30.0 || band.gain > 30.0) {
                errors.add("Band ${index + 1}: Gain ${band.gain} dB is out of range (-30 to +30 dB)")
            }
            if (!band.q.isFinite() || band.q <= 0.0 || band.q > 20.0) {
                errors.add("Band ${index + 1}: Q factor ${band.q} is out of range (0.01 to 20)")
            }
        }

        return errors
    }

    private fun parsePreamp(line: String): Double =
        PREAMP.find(line)?.groupValues?.get(1)?.toDoubleOrNull() ?: 0.0

    private fun parseFilterLine(line: String): EqualizerBand? {
        if (!line.contains("ON", ignoreCase = true)) return null
        val filterType = parseFilterType(line) ?: return null
        val frequency = parseValue(line, "Fc", "Hz") ?: return null
        val gain = parseValue(line, "Gain", "dB") ?: return null
        val q = parseValue(line, "Q", null) ?: return null
        return EqualizerBand(filterType = filterType, frequency = frequency, gain = gain, q = q)
    }

    private fun parseFilterType(line: String): EqualizerFilterType? = when {
        line.contains("LSC", ignoreCase = true) -> EqualizerFilterType.LSC
        line.contains("HSC", ignoreCase = true) -> EqualizerFilterType.HSC
        line.contains("PK", ignoreCase = true) -> EqualizerFilterType.PK
        line.contains("LPQ", ignoreCase = true) -> EqualizerFilterType.LPQ
        line.contains("HPQ", ignoreCase = true) -> EqualizerFilterType.HPQ
        else -> null
    }

    private fun parseValue(line: String, keyword: String, unit: String?): Double? {
        val unitPattern = if (unit != null) "\\s*$unit" else ""
        return Regex("""$keyword\s+([-+]?\d+\.?\d*)$unitPattern""", RegexOption.IGNORE_CASE)
            .find(line)?.groupValues?.get(1)?.toDoubleOrNull()
    }

    private val PREAMP = Regex("""Preamp:\s*([-+]?\d+\.?\d*)\s*dB""", RegexOption.IGNORE_CASE)
}
