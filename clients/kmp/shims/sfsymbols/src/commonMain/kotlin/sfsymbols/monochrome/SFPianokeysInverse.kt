package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFPianokeysInverse (monochrome)
 * Viewport: 29.9512 x 22.9785
 */
public val SfSymbols.Monochrome.SFPianokeysInverse: ImageVector
    get() {
        if (_sFPianokeysInverse != null) {
            return _sFPianokeysInverse!!
        }
        _sFPianokeysInverse = sfIcon(
            name = "Monochrome.SFPianokeysInverse",
            viewportWidth = 29.9512f,
            viewportHeight = 22.9785f
        ) {
            addSfPath("M5.43945 12.2754L5.43945 0.0292969L9.61914 0.0292969L9.61914 12.2754C9.61914 13.1152 9.0918 13.4473 8.53516 13.4473L8.05664 13.4473L8.05664 22.9785L7.00195 22.9785L7.00195 13.4473L6.52344 13.4473C5.9668 13.4473 5.43945 13.1152 5.43945 12.2754ZM12.7051 12.2754L12.7051 0.0292969L16.8848 0.0292969L16.8848 12.2754C16.8848 13.1152 16.3574 13.4473 15.791 13.4473L15.3223 13.4473L15.3223 22.9785L14.2676 22.9785L14.2676 13.4473L13.7891 13.4473C13.2324 13.4473 12.7051 13.1152 12.7051 12.2754ZM19.9609 12.2754L19.9609 0.0292969L24.1504 0.0292969L24.1504 12.2754C24.1504 13.1152 23.623 13.4473 23.0566 13.4473L22.5879 13.4473L22.5879 22.9785L21.5234 22.9785L21.5234 13.4473L21.0547 13.4473C20.498 13.4473 19.9609 13.1152 19.9609 12.2754ZM3.79883 22.9688L25.7812 22.9688C28.3105 22.9688 29.5898 21.6895 29.5898 19.209L29.5898 3.7793C29.5898 1.28906 28.3105 0.00976562 25.7812 0.00976562L3.79883 0.00976562C1.2793 0.00976562 0 1.2793 0 3.7793L0 19.209C0 21.709 1.2793 22.9688 3.79883 22.9688Z", fillAlpha = 0.85f)
        }
        return _sFPianokeysInverse!!
    }

private var _sFPianokeysInverse: ImageVector? = null
