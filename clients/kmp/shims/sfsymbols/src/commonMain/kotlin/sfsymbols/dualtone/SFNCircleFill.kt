package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFNCircleFill (dualtone)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Dualtone.SFNCircleFill: ImageVector
    get() {
        if (_sFNCircleFill != null) {
            return _sFNCircleFill!!
        }
        _sFNCircleFill = sfIcon(
            name = "Dualtone.SFNCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.4395C19.7266 25.4395 25.4395 19.7266 25.4395 12.7246C25.4395 5.71289 19.7266 0 12.7148 0C5.71289 0 0 5.71289 0 12.7246C0 19.7266 5.71289 25.4395 12.7148 25.4395Z", fillAlpha = 0.2125f)
            addSfPath("M8.82812 18.6133C8.30078 18.6133 7.99805 18.2715 7.99805 17.7246L7.99805 7.63672C7.99805 7.02148 8.33008 6.65039 8.89648 6.65039C9.28711 6.65039 9.55078 6.79688 9.84375 7.22656L15.6738 15.7031L15.7617 15.7031L15.7617 7.54883C15.7617 7.00195 16.0645 6.65039 16.582 6.65039C17.1191 6.65039 17.4316 6.98242 17.4316 7.54883L17.4316 17.6562C17.4316 18.2617 17.0898 18.6133 16.5137 18.6133C16.123 18.6133 15.8887 18.4766 15.5859 18.0469L9.74609 9.6582L9.66797 9.6582L9.66797 17.7246C9.66797 18.291 9.35547 18.6133 8.82812 18.6133Z", fillAlpha = 0.85f)
        }
        return _sFNCircleFill!!
    }

private var _sFNCircleFill: ImageVector? = null
