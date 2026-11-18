package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFMusicNote (dualtone)
 * Viewport: 15.3711 x 25.5762
 */
public val SfSymbols.Dualtone.SFMusicNote: ImageVector
    get() {
        if (_sFMusicNote != null) {
            return _sFMusicNote!!
        }
        _sFMusicNote = sfIcon(
            name = "Dualtone.SFMusicNote",
            viewportWidth = 15.3711f,
            viewportHeight = 25.5762f
        ) {
            addSfPath("M15.0098 6.2207L15.0098 1.5625C15.0098 0.9375 14.5117 0.527344 13.8965 0.654297L7.58789 2.03125C6.8457 2.1875 6.44531 2.59766 6.44531 3.25195L6.47461 17.1387C6.5332 17.793 6.21094 18.2324 5.63477 18.3496L3.63281 18.7695C1.16211 19.2773 0 20.5176 0 22.3926C0 24.2676 1.43555 25.5762 3.48633 25.5762C5.3125 25.5762 8.00781 24.248 8.00781 20.6445L8.00781 9.23828C8.00781 8.53516 8.1543 8.36914 8.79883 8.22266L14.375 7.00195C14.7656 6.91406 15.0098 6.62109 15.0098 6.2207Z", fillAlpha = 0.85f)
        }
        return _sFMusicNote!!
    }

private var _sFMusicNote: ImageVector? = null
