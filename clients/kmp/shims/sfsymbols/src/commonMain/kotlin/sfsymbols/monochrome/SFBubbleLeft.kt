package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFBubbleLeft (monochrome)
 * Viewport: 27.8711 x 27.2754
 */
public val SfSymbols.Monochrome.SFBubbleLeft: ImageVector
    get() {
        if (_sFBubbleLeft != null) {
            return _sFBubbleLeft!!
        }
        _sFBubbleLeft = sfIcon(
            name = "Monochrome.SFBubbleLeft",
            viewportWidth = 27.8711f,
            viewportHeight = 27.2754f
        ) {
            addSfPath("M7.42188 27.2754C7.95898 27.2754 8.33008 26.9922 8.96484 26.4258L13.3691 22.5488L22.002 22.5488C25.5371 22.5488 27.5098 20.5273 27.5098 17.0508L27.5098 7.08984C27.5098 3.60352 25.5371 1.58203 22.002 1.58203L5.50781 1.58203C1.96289 1.58203 0 3.59375 0 7.08984L0 17.0508C0 20.5469 1.96289 22.5488 5.50781 22.5488L6.24023 22.5488L6.24023 25.9082C6.24023 26.7383 6.66992 27.2754 7.42188 27.2754ZM7.85156 25.3125L7.85156 21.6406C7.85156 21.0449 7.62695 20.8203 7.04102 20.8203L5.51758 20.8203C2.99805 20.8203 1.72852 19.4727 1.72852 17.0312L1.72852 7.09961C1.72852 4.6582 2.99805 3.31055 5.51758 3.31055L21.9922 3.31055C24.4824 3.31055 25.7812 4.6582 25.7812 7.09961L25.7812 17.0312C25.7812 19.4727 24.4824 20.8203 21.9922 20.8203L13.3301 20.8203C12.7148 20.8203 12.3926 20.918 11.9629 21.3574Z", fillAlpha = 0.85f)
        }
        return _sFBubbleLeft!!
    }

private var _sFBubbleLeft: ImageVector? = null
