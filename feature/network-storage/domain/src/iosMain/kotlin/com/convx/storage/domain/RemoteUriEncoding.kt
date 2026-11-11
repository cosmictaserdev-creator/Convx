package com.convx.storage.domain

import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSString
import platform.Foundation.precomposedStringWithCanonicalMapping

@OptIn(ExperimentalForeignApi::class)
@Suppress("CAST_NEVER_SUCCEEDS")
internal actual fun canonicalUriText(value: String): String =
    (value as NSString).precomposedStringWithCanonicalMapping
