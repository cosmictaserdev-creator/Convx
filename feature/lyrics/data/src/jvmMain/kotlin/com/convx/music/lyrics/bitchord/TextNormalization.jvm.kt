package com.convx.music.lyrics.bitchord

import java.text.Normalizer

internal actual fun String.decomposeCanonical(): String = Normalizer.normalize(this, Normalizer.Form.NFD)
