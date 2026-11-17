package com.convx.storage.domain

internal fun canonicalUriText(value: String): String =
    java.text.Normalizer.normalize(value, java.text.Normalizer.Form.NFC)
