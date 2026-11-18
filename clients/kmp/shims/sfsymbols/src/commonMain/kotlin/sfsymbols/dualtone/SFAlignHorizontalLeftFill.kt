package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFAlignHorizontalLeftFill (dualtone)
 * Viewport: 27.9688 x 28.6621
 */
public val SfSymbols.Dualtone.SFAlignHorizontalLeftFill: ImageVector
    get() {
        if (_sFAlignHorizontalLeftFill != null) {
            return _sFAlignHorizontalLeftFill!!
        }
        _sFAlignHorizontalLeftFill = sfIcon(
            name = "Dualtone.SFAlignHorizontalLeftFill",
            viewportWidth = 27.9688f,
            viewportHeight = 28.6621f
        ) {
            addSfPath("M6.58203 13.0176L16.2598 13.0176C17.9395 13.0176 18.877 12.0898 18.877 10.4395L18.877 5.83008C18.877 4.17969 17.9395 3.25195 16.2598 3.25195L6.58203 3.25195C4.90234 3.25195 3.96484 4.17969 3.96484 5.83008L3.96484 10.4395C3.96484 12.0898 4.90234 13.0176 6.58203 13.0176ZM6.58203 25.3809L24.9902 25.3809C26.6699 25.3809 27.6074 24.4531 27.6074 22.8027L27.6074 18.1934C27.6074 16.543 26.6699 15.6152 24.9902 15.6152L6.58203 15.6152C4.90234 15.6152 3.96484 16.543 3.96484 18.1934L3.96484 22.8027C3.96484 24.4531 4.90234 25.3809 6.58203 25.3809Z", fillAlpha = 0.425f)
            addSfPath("M0.800781 28.6426C1.2207 28.6426 1.5918 28.3105 1.5918 27.8906L1.5918 0.751953C1.5918 0.332031 1.2207 0 0.800781 0C0.371094 0 0 0.332031 0 0.751953L0 27.8906C0 28.3105 0.371094 28.6426 0.800781 28.6426Z", fillAlpha = 0.85f)
        }
        return _sFAlignHorizontalLeftFill!!
    }

private var _sFAlignHorizontalLeftFill: ImageVector? = null
