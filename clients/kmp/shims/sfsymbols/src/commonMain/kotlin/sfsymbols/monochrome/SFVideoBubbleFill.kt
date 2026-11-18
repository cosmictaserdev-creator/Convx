package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFVideoBubbleFill (monochrome)
 * Viewport: 27.8711 x 27.3926
 */
public val SfSymbols.Monochrome.SFVideoBubbleFill: ImageVector
    get() {
        if (_sFVideoBubbleFill != null) {
            return _sFVideoBubbleFill!!
        }
        _sFVideoBubbleFill = sfIcon(
            name = "Monochrome.SFVideoBubbleFill",
            viewportWidth = 27.8711f,
            viewportHeight = 27.3926f
        ) {
            addSfPath("M27.5098 7.14844L27.5098 17.1094C27.5098 20.5859 25.5371 22.5977 22.002 22.5977L13.0273 22.5977L8.4668 26.709C7.96875 27.168 7.64648 27.3926 7.2168 27.3926C6.5918 27.3926 6.23047 26.9434 6.23047 26.2598L6.23047 22.5977L5.50781 22.5977C1.96289 22.5977 0 20.6055 0 17.1094L0 7.14844C0 3.65234 1.96289 1.64062 5.50781 1.64062L22.002 1.64062C25.5371 1.64062 27.5098 3.66211 27.5098 7.14844ZM8.45703 6.96289C7.2168 6.96289 6.41602 7.69531 6.41602 8.98438L6.41602 15.2539C6.41602 16.5332 7.17773 17.2754 8.45703 17.2754L15.2832 17.2754C16.5723 17.2754 17.3242 16.5332 17.3242 15.2539L17.3242 8.98438C17.3242 7.69531 16.6309 6.96289 15.332 6.96289ZM20.498 8.27148L18.0371 10.3418L18.0371 13.8867L20.498 15.9668C20.7227 16.1523 20.9668 16.2695 21.1914 16.2695C21.6602 16.2695 21.9629 15.9375 21.9629 15.4102L21.9629 8.81836C21.9629 8.30078 21.6602 7.97852 21.1914 7.97852C20.9668 7.97852 20.7031 8.08594 20.498 8.27148Z", fillAlpha = 0.85f)
        }
        return _sFVideoBubbleFill!!
    }

private var _sFVideoBubbleFill: ImageVector? = null
