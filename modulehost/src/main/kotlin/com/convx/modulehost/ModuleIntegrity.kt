package com.convx.modulehost

import java.security.MessageDigest

/**
 * SHA-256 integrity verification for module packages.
 *
 * A digest guarantees byte integrity only; it does not authenticate the publisher.
 * Trusted-source/signature policy remains a host concern outside this class.
 */
object ModuleIntegrity {
    private val hexPattern = Regex("[0-9A-Fa-f]{64}")

    fun sha256Hex(bytes: ByteArray): String =
        MessageDigest.getInstance("SHA-256").digest(bytes).joinToString("") { "%02x".format(it) }

    fun verify(bytes: ByteArray, expectedSha256: String) {
        if (!hexPattern.matches(expectedSha256)) {
            throw ModuleValidationException("sha256 must be exactly 64 hexadecimal characters")
        }
        val actual = sha256Hex(bytes)
        if (actual != expectedSha256.lowercase()) {
            throw ModuleValidationException("sha256 mismatch: expected $expectedSha256, got $actual")
        }
    }
}