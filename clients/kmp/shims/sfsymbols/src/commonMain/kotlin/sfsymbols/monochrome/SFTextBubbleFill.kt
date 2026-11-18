package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFTextBubbleFill (monochrome)
 * Viewport: 27.8711 x 27.3926
 */
public val SfSymbols.Monochrome.SFTextBubbleFill: ImageVector
    get() {
        if (_sFTextBubbleFill != null) {
            return _sFTextBubbleFill!!
        }
        _sFTextBubbleFill = sfIcon(
            name = "Monochrome.SFTextBubbleFill",
            viewportWidth = 27.8711f,
            viewportHeight = 27.3926f
        ) {
            addSfPath("M27.5098 7.14844L27.5098 17.1094C27.5098 20.5859 25.5371 22.5977 22.002 22.5977L13.0273 22.5977L8.4668 26.709C7.96875 27.168 7.64648 27.3926 7.2168 27.3926C6.5918 27.3926 6.23047 26.9434 6.23047 26.2598L6.23047 22.5977L5.50781 22.5977C1.96289 22.5977 0 20.6055 0 17.1094L0 7.14844C0 3.65234 1.96289 1.64062 5.50781 1.64062L22.002 1.64062C25.5371 1.64062 27.5098 3.66211 27.5098 7.14844ZM6.97266 15.5273C6.61133 15.5273 6.31836 15.8203 6.31836 16.1914C6.31836 16.5625 6.61133 16.8555 6.97266 16.8555L15.6445 16.8555C16.0059 16.8555 16.2988 16.5625 16.2988 16.1914C16.2988 15.8203 16.0059 15.5273 15.6445 15.5273ZM6.97266 11.3184C6.61133 11.3184 6.31836 11.6211 6.31836 11.9824C6.31836 12.3535 6.61133 12.6465 6.97266 12.6465L20.2344 12.6465C20.6055 12.6465 20.8984 12.3535 20.8984 11.9824C20.8984 11.6211 20.6055 11.3184 20.2344 11.3184ZM6.97266 7.12891C6.61133 7.12891 6.31836 7.42188 6.31836 7.7832C6.31836 8.1543 6.61133 8.44727 6.97266 8.44727L20.2344 8.44727C20.6055 8.44727 20.8984 8.1543 20.8984 7.7832C20.8984 7.42188 20.6055 7.12891 20.2344 7.12891Z", fillAlpha = 0.85f)
        }
        return _sFTextBubbleFill!!
    }

private var _sFTextBubbleFill: ImageVector? = null
