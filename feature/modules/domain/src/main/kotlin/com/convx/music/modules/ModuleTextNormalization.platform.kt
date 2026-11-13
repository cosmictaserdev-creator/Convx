package com.convx.music.modules

import java.text.Normalizer

internal fun moduleUnicodeCompatibilityDecomposition(value: String): String =
    Normalizer.normalize(value, Normalizer.Form.NFKD)
