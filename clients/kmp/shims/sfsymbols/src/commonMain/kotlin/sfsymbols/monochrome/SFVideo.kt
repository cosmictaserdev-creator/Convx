package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFVideo (monochrome)
 * Viewport: 32.5 x 20.6348
 */
public val SfSymbols.Monochrome.SFVideo: ImageVector
    get() {
        if (_sFVideo != null) {
            return _sFVideo!!
        }
        _sFVideo = sfIcon(
            name = "Monochrome.SFVideo",
            viewportWidth = 32.5f,
            viewportHeight = 20.6348f
        ) {
            addSfPath("M6.24023 20.6152L19.6289 20.6152C22.6172 20.6152 24.3652 18.8965 24.3652 15.918L24.3652 4.69727C24.3652 1.71875 22.6172 0 19.6289 0L6.24023 0C3.36914 0 1.50391 1.71875 1.50391 4.69727L1.50391 15.918C1.50391 18.8965 3.26172 20.6152 6.24023 20.6152ZM6.55273 18.9941C4.41406 18.9941 3.23242 17.8906 3.23242 15.6543L3.23242 4.9707C3.23242 2.72461 4.41406 1.62109 6.55273 1.62109L19.3164 1.62109C21.4551 1.62109 22.6367 2.72461 22.6367 4.9707L22.6367 15.6543C22.6367 17.8906 21.4551 18.9941 19.3164 18.9941ZM24.1016 6.95312L24.1016 8.98438L30.1172 4.07227C30.2734 3.94531 30.4004 3.85742 30.5371 3.85742C30.7324 3.85742 30.8301 4.01367 30.8301 4.25781L30.8301 16.3574C30.8301 16.6113 30.7324 16.7578 30.5371 16.7578C30.4004 16.7578 30.2734 16.6602 30.1172 16.543L24.1016 11.6309L24.1016 13.6719L29.2773 17.998C29.7754 18.3984 30.293 18.6719 30.8105 18.6719C31.8262 18.6719 32.5 17.9297 32.5 16.8164L32.5 3.80859C32.5 2.69531 31.8262 1.94336 30.8105 1.94336C30.293 1.94336 29.7754 2.2168 29.2773 2.62695Z", fillAlpha = 0.85f)
        }
        return _sFVideo!!
    }

private var _sFVideo: ImageVector? = null
