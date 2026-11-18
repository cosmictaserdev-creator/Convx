package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFFolderCircleFill (monochrome)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Monochrome.SFFolderCircleFill: ImageVector
    get() {
        if (_sFFolderCircleFill != null) {
            return _sFFolderCircleFill!!
        }
        _sFFolderCircleFill = sfIcon(
            name = "Monochrome.SFFolderCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M25.4395 12.7344C25.4395 19.7461 19.7266 25.459 12.7148 25.459C5.71289 25.459 0 19.7461 0 12.7344C0 5.73242 5.71289 0.0195312 12.7148 0.0195312C19.7266 0.0195312 25.4395 5.73242 25.4395 12.7344ZM7.09961 6.64062C6.03516 6.64062 5.43945 7.23633 5.43945 8.41797L5.43945 16.709C5.43945 17.959 6.09375 18.5938 7.36328 18.5938L18.252 18.5938C19.375 18.5938 20.0195 17.9492 20.0195 16.709L20.0195 9.6875C20.0195 8.4375 19.3652 7.79297 18.0957 7.79297L11.7773 7.79297C11.2988 7.79297 11.0547 7.68555 10.7422 7.41211L10.3516 7.09961C9.92188 6.74805 9.62891 6.64062 9.01367 6.64062ZM18.916 9.9707L18.916 10.2539L6.52344 10.2539L6.52344 9.9707C6.52344 9.56055 6.80664 9.27734 7.32422 9.27734L18.125 9.27734C18.6523 9.27734 18.916 9.56055 18.916 9.9707Z", fillAlpha = 0.85f)
        }
        return _sFFolderCircleFill!!
    }

private var _sFFolderCircleFill: ImageVector? = null
