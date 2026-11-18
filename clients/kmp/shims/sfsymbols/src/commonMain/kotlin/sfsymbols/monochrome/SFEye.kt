package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFEye (monochrome)
 * Viewport: 34.4141 x 21.4746
 */
public val SfSymbols.Monochrome.SFEye: ImageVector
    get() {
        if (_sFEye != null) {
            return _sFEye!!
        }
        _sFEye = sfIcon(
            name = "Monochrome.SFEye",
            viewportWidth = 34.4141f,
            viewportHeight = 21.4746f
        ) {
            addSfPath("M17.0312 21.4746C27.0605 21.4746 34.0527 13.291 34.0527 10.7422C34.0527 8.19336 27.0215 0.0195312 17.0312 0.0195312C7.13867 0.0195312 0 8.19336 0 10.7422C0 13.291 7.10938 21.4746 17.0312 21.4746ZM17.0312 19.7461C8.56445 19.7461 1.9043 12.5684 1.9043 10.7422C1.9043 9.14062 8.56445 1.73828 17.0312 1.73828C25.4883 1.73828 32.1484 9.14062 32.1484 10.7422C32.1484 12.5684 25.4883 19.7461 17.0312 19.7461ZM17.0312 17.793C20.9375 17.793 24.1016 14.6191 24.1016 10.7227C24.1016 6.81641 20.9375 3.65234 17.0312 3.65234C13.1348 3.65234 9.96094 6.81641 9.96094 10.7227C9.96094 14.6191 13.1348 17.793 17.0312 17.793ZM17.0312 13.0176C15.7617 13.0176 14.7461 11.9922 14.7461 10.7324C14.7461 9.46289 15.7617 8.4375 17.0312 8.4375C18.3008 8.4375 19.3262 9.46289 19.3262 10.7324C19.3262 11.9922 18.3008 13.0176 17.0312 13.0176Z", fillAlpha = 0.85f)
        }
        return _sFEye!!
    }

private var _sFEye: ImageVector? = null
