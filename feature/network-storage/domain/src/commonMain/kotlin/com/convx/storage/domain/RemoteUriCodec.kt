package com.convx.storage.domain

// java.net.URI.toASCIIString normalizes Unicode before escaping. Preserve existing
// Android queue IDs and encrypted source addresses when another client reads them.
internal expect fun canonicalUriText(value: String): String

internal fun encodeRemoteUriPath(path: String): String = buildString {
    val hex = "0123456789ABCDEF"
    for (byte in canonicalUriText(path).encodeToByteArray()) {
        val value = byte.toInt() and 255
        val char = value.toChar()
        if (char in 'a'..'z' || char in 'A'..'Z' || char in '0'..'9' || char in "-._~!$&'()*+,;=:@/") append(char)
        else {
            append('%')
            append(hex[value ushr 4])
            append(hex[value and 15])
        }
    }
}
