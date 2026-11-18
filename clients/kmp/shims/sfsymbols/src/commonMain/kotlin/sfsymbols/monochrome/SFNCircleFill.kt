package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFNCircleFill (monochrome)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Monochrome.SFNCircleFill: ImageVector
    get() {
        if (_sFNCircleFill != null) {
            return _sFNCircleFill!!
        }
        _sFNCircleFill = sfIcon(
            name = "Monochrome.SFNCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M25.4395 12.7246C25.4395 19.7266 19.7266 25.4395 12.7148 25.4395C5.71289 25.4395 0 19.7266 0 12.7246C0 5.71289 5.71289 0 12.7148 0C19.7266 0 25.4395 5.71289 25.4395 12.7246ZM15.7617 7.54883L15.7617 15.7031L15.6738 15.7031L9.84375 7.22656C9.55078 6.79688 9.28711 6.65039 8.89648 6.65039C8.33008 6.65039 7.99805 7.02148 7.99805 7.63672L7.99805 17.7246C7.99805 18.2715 8.30078 18.6133 8.82812 18.6133C9.35547 18.6133 9.66797 18.291 9.66797 17.7246L9.66797 9.6582L9.74609 9.6582L15.5859 18.0469C15.8887 18.4766 16.123 18.6133 16.5137 18.6133C17.0898 18.6133 17.4316 18.2617 17.4316 17.6562L17.4316 7.54883C17.4316 6.98242 17.1191 6.65039 16.582 6.65039C16.0645 6.65039 15.7617 7.00195 15.7617 7.54883Z", fillAlpha = 0.85f)
        }
        return _sFNCircleFill!!
    }

private var _sFNCircleFill: ImageVector? = null
