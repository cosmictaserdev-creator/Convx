package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFEyeHalfClosed (dualtone)
 * Viewport: 34.4141 x 21.4746
 */
public val SfSymbols.Dualtone.SFEyeHalfClosed: ImageVector
    get() {
        if (_sFEyeHalfClosed != null) {
            return _sFEyeHalfClosed!!
        }
        _sFEyeHalfClosed = sfIcon(
            name = "Dualtone.SFEyeHalfClosed",
            viewportWidth = 34.4141f,
            viewportHeight = 21.4746f
        ) {
            addSfPath("M17.0312 21.4746C27.0605 21.4746 34.0527 13.291 34.0527 10.7422C34.0527 8.19336 27.0215 0.0195312 17.0312 0.0195312C7.13867 0.0195312 0 8.19336 0 10.7422C0 13.291 7.10938 21.4746 17.0312 21.4746ZM17.0312 19.7461C8.56445 19.7461 1.9043 12.5684 1.9043 10.7422C1.9043 9.14062 8.56445 1.73828 17.0312 1.73828C25.4883 1.73828 32.1484 9.14062 32.1484 10.7422C32.1484 12.5684 25.4883 19.7461 17.0312 19.7461ZM17.0312 17.793C20.9375 17.793 24.1016 14.6191 24.1016 10.7227C24.1016 10.1855 24.043 9.67773 23.9355 9.16992L18.7402 9.19922C19.1016 9.59961 19.3262 10.1367 19.3262 10.7324C19.3262 11.9922 18.3008 13.0176 17.0312 13.0176C15.7617 13.0176 14.7461 11.9922 14.7461 10.7324C14.7461 10.1172 14.9902 9.57031 15.3809 9.16016L10.127 9.20898C10.0195 9.69727 9.96094 10.1953 9.96094 10.7227C9.96094 14.6191 13.1348 17.793 17.0312 17.793ZM1.02539 11.2988C6.45508 10.5566 11.9531 10.0684 17.0215 10.0684C22.0801 10.0684 27.5781 10.5566 33.0078 11.2988L33.0078 9.42383C27.5781 8.67188 22.8613 8.05664 17.0215 8.05664C11.1719 8.05664 6.45508 8.67188 1.02539 9.42383Z", fillAlpha = 0.85f)
        }
        return _sFEyeHalfClosed!!
    }

private var _sFEyeHalfClosed: ImageVector? = null
