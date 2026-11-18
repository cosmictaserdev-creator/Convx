package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFFaceSmilingInverse (dualtone)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Dualtone.SFFaceSmilingInverse: ImageVector
    get() {
        if (_sFFaceSmilingInverse != null) {
            return _sFFaceSmilingInverse!!
        }
        _sFFaceSmilingInverse = sfIcon(
            name = "Dualtone.SFFaceSmilingInverse",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.4395C19.7266 25.4395 25.4395 19.7266 25.4395 12.7246C25.4395 5.71289 19.7266 0 12.7148 0C5.71289 0 0 5.71289 0 12.7246C0 19.7266 5.71289 25.4395 12.7148 25.4395Z", fillAlpha = 0.2125f)
            addSfPath("M12.7148 19.1016C10.0684 19.1016 8.28125 17.3145 8.28125 16.4941C8.28125 16.2207 8.55469 16.1035 8.7793 16.2207C9.6875 16.7676 10.8496 17.373 12.7148 17.373C14.5801 17.373 15.752 16.7773 16.6406 16.2207C16.875 16.1035 17.1484 16.2207 17.1484 16.4941C17.1484 17.3145 15.3516 19.1016 12.7148 19.1016ZM8.87695 11.5234C8.0957 11.5234 7.41211 10.8008 7.41211 9.81445C7.41211 8.81836 8.08594 8.08594 8.87695 8.08594C9.66797 8.08594 10.3516 8.81836 10.3516 9.81445C10.3516 10.8008 9.6582 11.5234 8.87695 11.5234ZM16.5527 11.5234C15.7812 11.5234 15.0879 10.8008 15.0879 9.81445C15.0879 8.81836 15.7715 8.08594 16.5527 8.08594C17.3438 8.08594 18.0273 8.81836 18.0273 9.81445C18.0273 10.8008 17.3438 11.5234 16.5527 11.5234Z", fillAlpha = 0.85f)
        }
        return _sFFaceSmilingInverse!!
    }

private var _sFFaceSmilingInverse: ImageVector? = null
