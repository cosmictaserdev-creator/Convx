package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFPlusBubbleFill (dualtone)
 * Viewport: 27.8711 x 27.3926
 */
public val SfSymbols.Dualtone.SFPlusBubbleFill: ImageVector
    get() {
        if (_sFPlusBubbleFill != null) {
            return _sFPlusBubbleFill!!
        }
        _sFPlusBubbleFill = sfIcon(
            name = "Dualtone.SFPlusBubbleFill",
            viewportWidth = 27.8711f,
            viewportHeight = 27.3926f
        ) {
            addSfPath("M7.2168 27.3926C7.64648 27.3926 7.96875 27.168 8.4668 26.709L13.0273 22.5977L22.002 22.5977C25.5371 22.5977 27.5098 20.5859 27.5098 17.1094L27.5098 7.14844C27.5098 3.66211 25.5371 1.64062 22.002 1.64062L5.50781 1.64062C1.96289 1.64062 0 3.65234 0 7.14844L0 17.1094C0 20.6055 1.96289 22.5977 5.50781 22.5977L6.23047 22.5977L6.23047 26.2598C6.23047 26.9434 6.5918 27.3926 7.2168 27.3926Z", fillAlpha = 0.2125f)
            addSfPath("M14.6875 17.0801L14.6875 7.13867C14.6875 6.57227 14.3066 6.18164 13.7402 6.18164C13.2031 6.18164 12.832 6.58203 12.832 7.13867L12.832 17.0801C12.832 17.6172 13.2031 18.0176 13.7402 18.0176C14.3066 18.0176 14.6875 17.627 14.6875 17.0801ZM8.79883 13.0273L18.7402 13.0273C19.2773 13.0273 19.6777 12.6562 19.6777 12.1191C19.6777 11.5527 19.2871 11.1719 18.7402 11.1719L8.79883 11.1719C8.23242 11.1719 7.8418 11.5527 7.8418 12.1191C7.8418 12.6562 8.24219 13.0273 8.79883 13.0273Z", fillAlpha = 0.85f)
        }
        return _sFPlusBubbleFill!!
    }

private var _sFPlusBubbleFill: ImageVector? = null
