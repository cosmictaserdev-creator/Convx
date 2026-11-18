package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFInfoBubbleFill (monochrome)
 * Viewport: 27.8711 x 27.3926
 */
public val SfSymbols.Monochrome.SFInfoBubbleFill: ImageVector
    get() {
        if (_sFInfoBubbleFill != null) {
            return _sFInfoBubbleFill!!
        }
        _sFInfoBubbleFill = sfIcon(
            name = "Monochrome.SFInfoBubbleFill",
            viewportWidth = 27.8711f,
            viewportHeight = 27.3926f
        ) {
            addSfPath("M27.5098 7.14844L27.5098 17.1094C27.5098 20.5859 25.5371 22.5977 22.002 22.5977L13.0273 22.5977L8.4668 26.709C7.96875 27.168 7.64648 27.3926 7.2168 27.3926C6.5918 27.3926 6.23047 26.9434 6.23047 26.2598L6.23047 22.5977L5.50781 22.5977C1.96289 22.5977 0 20.6055 0 17.1094L0 7.14844C0 3.65234 1.96289 1.64062 5.50781 1.64062L22.002 1.64062C25.5371 1.64062 27.5098 3.66211 27.5098 7.14844ZM11.8164 9.91211C11.3867 9.91211 11.0645 10.2441 11.0645 10.6543C11.0645 11.084 11.3867 11.3965 11.8164 11.3965L13.2617 11.3965L13.2617 17.2949L11.6406 17.2949C11.2109 17.2949 10.8789 17.627 10.8789 18.0273C10.8789 18.457 11.2109 18.7695 11.6406 18.7695L16.5234 18.7695C16.9531 18.7695 17.2852 18.457 17.2852 18.0273C17.2852 17.627 16.9531 17.2949 16.5234 17.2949L14.9023 17.2949L14.9023 10.8398C14.9023 10.293 14.6191 9.91211 14.0918 9.91211ZM12.2656 6.40625C12.2656 7.1875 12.8906 7.8125 13.6816 7.8125C14.4629 7.8125 15.0879 7.1875 15.0879 6.40625C15.0879 5.61523 14.4629 4.98047 13.6816 4.98047C12.8906 4.98047 12.2656 5.61523 12.2656 6.40625Z", fillAlpha = 0.85f)
        }
        return _sFInfoBubbleFill!!
    }

private var _sFInfoBubbleFill: ImageVector? = null
