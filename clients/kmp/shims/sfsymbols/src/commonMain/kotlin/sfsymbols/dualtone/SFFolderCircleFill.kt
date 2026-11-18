package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFFolderCircleFill (dualtone)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Dualtone.SFFolderCircleFill: ImageVector
    get() {
        if (_sFFolderCircleFill != null) {
            return _sFFolderCircleFill!!
        }
        _sFFolderCircleFill = sfIcon(
            name = "Dualtone.SFFolderCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.459C19.7266 25.459 25.4395 19.7461 25.4395 12.7344C25.4395 5.73242 19.7266 0.0195312 12.7148 0.0195312C5.71289 0.0195312 0 5.73242 0 12.7344C0 19.7461 5.71289 25.459 12.7148 25.459Z", fillAlpha = 0.2125f)
            addSfPath("M7.36328 18.5938C6.09375 18.5938 5.43945 17.959 5.43945 16.709L5.43945 8.41797C5.43945 7.23633 6.03516 6.64062 7.09961 6.64062L9.01367 6.64062C9.62891 6.64062 9.92188 6.74805 10.3516 7.09961L10.7422 7.41211C11.0547 7.68555 11.2988 7.79297 11.7773 7.79297L18.0957 7.79297C19.3652 7.79297 20.0195 8.4375 20.0195 9.6875L20.0195 16.709C20.0195 17.9492 19.375 18.5938 18.252 18.5938ZM6.52344 10.2539L18.916 10.2539L18.916 9.9707C18.916 9.56055 18.6523 9.27734 18.125 9.27734L7.32422 9.27734C6.80664 9.27734 6.52344 9.56055 6.52344 9.9707Z", fillAlpha = 0.85f)
        }
        return _sFFolderCircleFill!!
    }

private var _sFFolderCircleFill: ImageVector? = null
