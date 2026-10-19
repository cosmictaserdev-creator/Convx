/** Adapted from Flamingo by Yos-X, GPL-3.0. See NOTICE. */
package com.convx.music.ui.component.flamingo

import androidx.compose.runtime.Stable


@Stable
data class YosUIConfig(
    val edgeFade: Boolean = true,
    val formatText: Boolean = true,
    val noLrcText: String = "No lyrics",
    val blankHeight: Int = 70,
    val mainTextSize: Int = 34,
    val subTextSize: Int = mainTextSize - 18,
    val mainTextBasicColor: Long = 0xFFF2F2F2,
    val subTextBasicColor: Long = 0xFF919191,
    val normalMainTextAlpha: Float = 0.4f,
    val normalSubTextAlpha: Float = 0.3f,
    val currentMainTextAlpha: Float = 0.9f,
    val currentSubTextAlpha: Float = 0.6f
)
