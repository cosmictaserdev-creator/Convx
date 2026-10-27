package com.convx.storage.domain

internal actual fun canonicalUriText(value: String): String =
    java.text.Normalizer.normalize(value, java.text.Normalizer.Form.NFC)
