package com.convx.music.lyrics.bitchord

import platform.Foundation.NSString
import platform.Foundation.decomposedStringWithCanonicalMapping

@Suppress("CAST_NEVER_SUCCEEDS")
internal actual fun String.decomposeCanonical(): String = (this as NSString).decomposedStringWithCanonicalMapping
