@file:Suppress("CAST_NEVER_SUCCEEDS")
@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)

package com.convx.music.modules

import platform.Foundation.*

internal actual fun moduleUnicodeCompatibilityDecomposition(value: String): String =
    (value as NSString).decomposedStringWithCompatibilityMapping
