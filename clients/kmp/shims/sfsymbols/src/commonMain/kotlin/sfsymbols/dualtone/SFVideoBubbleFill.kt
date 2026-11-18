package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFVideoBubbleFill (dualtone)
 * Viewport: 27.8711 x 27.3926
 */
public val SfSymbols.Dualtone.SFVideoBubbleFill: ImageVector
    get() {
        if (_sFVideoBubbleFill != null) {
            return _sFVideoBubbleFill!!
        }
        _sFVideoBubbleFill = sfIcon(
            name = "Dualtone.SFVideoBubbleFill",
            viewportWidth = 27.8711f,
            viewportHeight = 27.3926f
        ) {
            addSfPath("M7.2168 27.3926C7.64648 27.3926 7.96875 27.168 8.4668 26.709L13.0273 22.5977L22.002 22.5977C25.5371 22.5977 27.5098 20.5859 27.5098 17.1094L27.5098 7.14844C27.5098 3.66211 25.5371 1.64062 22.002 1.64062L5.50781 1.64062C1.96289 1.64062 0 3.65234 0 7.14844L0 17.1094C0 20.6055 1.96289 22.5977 5.50781 22.5977L6.23047 22.5977L6.23047 26.2598C6.23047 26.9434 6.5918 27.3926 7.2168 27.3926Z", fillAlpha = 0.2125f)
            addSfPath("M8.45703 17.2754C7.17773 17.2754 6.41602 16.5332 6.41602 15.2539L6.41602 8.98438C6.41602 7.69531 7.2168 6.96289 8.45703 6.96289L15.332 6.96289C16.6309 6.96289 17.3242 7.69531 17.3242 8.98438L17.3242 15.2539C17.3242 16.5332 16.5723 17.2754 15.2832 17.2754ZM18.0371 13.8867L18.0371 10.3418L20.498 8.27148C20.7031 8.08594 20.9668 7.97852 21.1914 7.97852C21.6602 7.97852 21.9629 8.30078 21.9629 8.81836L21.9629 15.4102C21.9629 15.9375 21.6602 16.2695 21.1914 16.2695C20.9668 16.2695 20.7227 16.1523 20.498 15.9668Z", fillAlpha = 0.85f)
        }
        return _sFVideoBubbleFill!!
    }

private var _sFVideoBubbleFill: ImageVector? = null
