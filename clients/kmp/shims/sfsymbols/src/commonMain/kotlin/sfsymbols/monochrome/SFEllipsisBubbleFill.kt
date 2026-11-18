package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFEllipsisBubbleFill (monochrome)
 * Viewport: 27.8711 x 27.3926
 */
public val SfSymbols.Monochrome.SFEllipsisBubbleFill: ImageVector
    get() {
        if (_sFEllipsisBubbleFill != null) {
            return _sFEllipsisBubbleFill!!
        }
        _sFEllipsisBubbleFill = sfIcon(
            name = "Monochrome.SFEllipsisBubbleFill",
            viewportWidth = 27.8711f,
            viewportHeight = 27.3926f
        ) {
            addSfPath("M27.5098 7.14844L27.5098 17.1094C27.5098 20.5859 25.5371 22.5977 22.002 22.5977L13.0273 22.5977L8.4668 26.709C7.96875 27.168 7.64648 27.3926 7.2168 27.3926C6.5918 27.3926 6.23047 26.9434 6.23047 26.2598L6.23047 22.5977L5.50781 22.5977C1.96289 22.5977 0 20.6055 0 17.1094L0 7.14844C0 3.65234 1.96289 1.64062 5.50781 1.64062L22.002 1.64062C25.5371 1.64062 27.5098 3.66211 27.5098 7.14844ZM18.1445 12.1094C18.1445 13.0859 18.9258 13.8672 19.9023 13.8672C20.8691 13.8672 21.6504 13.0859 21.6504 12.1094C21.6504 11.1426 20.8691 10.3613 19.9023 10.3613C18.9258 10.3613 18.1445 11.1426 18.1445 12.1094ZM12.002 12.1094C12.002 13.0859 12.7832 13.8672 13.7598 13.8672C14.7266 13.8672 15.5078 13.0859 15.5078 12.1094C15.5078 11.1426 14.7266 10.3613 13.7598 10.3613C12.7832 10.3613 12.002 11.1426 12.002 12.1094ZM5.85938 12.1094C5.85938 13.0859 6.65039 13.8672 7.62695 13.8672C8.59375 13.8672 9.375 13.0859 9.375 12.1094C9.375 11.1426 8.59375 10.3613 7.62695 10.3613C6.65039 10.3613 5.85938 11.1426 5.85938 12.1094Z", fillAlpha = 0.85f)
        }
        return _sFEllipsisBubbleFill!!
    }

private var _sFEllipsisBubbleFill: ImageVector? = null
