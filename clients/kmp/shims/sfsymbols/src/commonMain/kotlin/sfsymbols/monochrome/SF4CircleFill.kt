package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SF4CircleFill (monochrome)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Monochrome.SF4CircleFill: ImageVector
    get() {
        if (_sF4CircleFill != null) {
            return _sF4CircleFill!!
        }
        _sF4CircleFill = sfIcon(
            name = "Monochrome.SF4CircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M25.4395 12.7246C25.4395 19.7266 19.7266 25.4395 12.7148 25.4395C5.71289 25.4395 0 19.7266 0 12.7246C0 5.71289 5.71289 0 12.7148 0C19.7266 0 25.4395 5.71289 25.4395 12.7246ZM12.1191 7.60742C10.8203 9.55078 8.98438 12.2559 7.92969 13.9844C7.70508 14.3555 7.62695 14.6387 7.62695 14.9707C7.62695 15.6445 8.08594 16.084 8.80859 16.084L13.5352 16.084L13.5352 17.6465C13.5352 18.2227 13.8477 18.5742 14.3555 18.5742C14.8926 18.5742 15.1953 18.2324 15.1953 17.6465L15.1953 16.084L16.3086 16.084C16.748 16.084 17.0605 15.7812 17.0605 15.3516C17.0605 14.9023 16.7578 14.5996 16.3086 14.5996L15.1953 14.5996L15.1953 7.83203C15.1953 7.11914 14.6484 6.64062 13.8379 6.64062C13.0664 6.64062 12.5781 6.91406 12.1191 7.60742ZM13.5352 14.5996L9.29688 14.5996L9.29688 14.5312C10.2441 12.9102 12.1973 10.0098 13.4473 8.13477L13.5352 8.13477Z", fillAlpha = 0.85f)
        }
        return _sF4CircleFill!!
    }

private var _sF4CircleFill: ImageVector? = null
