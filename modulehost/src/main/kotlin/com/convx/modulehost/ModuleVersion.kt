package com.convx.modulehost

import java.math.BigInteger

/**
 * Parsed SemVer with precedence ordering per the SemVer 2.0.0 spec.
 *
 * Public so host integrations can compare an installed version with a
 * feed release without duplicating precedence logic.
 */
class ModuleVersion private constructor(
    private val major: BigInteger,
    private val minor: BigInteger,
    private val patch: BigInteger,
    private val preRelease: List<String>?,
) : Comparable<ModuleVersion> {
    override fun compareTo(other: ModuleVersion): Int {
        for ((left, right) in listOf(major to other.major, minor to other.minor, patch to other.patch)) {
            val comparison = left.compareTo(right)
            if (comparison != 0) return comparison
        }

        val leftPreRelease = preRelease
        val rightPreRelease = other.preRelease
        if (leftPreRelease == null || rightPreRelease == null) {
            return when {
                leftPreRelease == rightPreRelease -> 0
                leftPreRelease == null -> 1
                else -> -1
            }
        }

        for ((left, right) in leftPreRelease.zip(rightPreRelease)) {
            if (left == right) continue
            val leftNumeric = left.all(Char::isDigit)
            val rightNumeric = right.all(Char::isDigit)
            return when {
                leftNumeric && rightNumeric -> BigInteger(left).compareTo(BigInteger(right))
                leftNumeric != rightNumeric -> if (leftNumeric) -1 else 1
                else -> left.compareTo(right)
            }
        }
        return leftPreRelease.size.compareTo(rightPreRelease.size)
    }

    companion object {
        private val pattern = Regex(
            """^(0|[1-9][0-9]*)\.(0|[1-9][0-9]*)\.(0|[1-9][0-9]*)(?:-((?:0|[1-9][0-9]*|[0-9A-Za-z-]*[A-Za-z-][0-9A-Za-z-]*)(?:\.(?:0|[1-9][0-9]*|[0-9A-Za-z-]*[A-Za-z-][0-9A-Za-z-]*))*))?(?:\+[0-9A-Za-z-]+(?:\.[0-9A-Za-z-]+)*)?$""",
        )

        fun parse(value: String, label: String): ModuleVersion {
            val match = pattern.matchEntire(value)
                ?: throw ModuleValidationException("$label must be valid SemVer")
            val (major, minor, patch, preRelease) = match.destructured
            return ModuleVersion(
                major = BigInteger(major),
                minor = BigInteger(minor),
                patch = BigInteger(patch),
                preRelease = preRelease.takeIf(String::isNotEmpty)?.split('.'),
            )
        }
    }
}
