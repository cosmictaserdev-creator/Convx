package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFParkingsignCircleFill (monochrome)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Monochrome.SFParkingsignCircleFill: ImageVector
    get() {
        if (_sFParkingsignCircleFill != null) {
            return _sFParkingsignCircleFill!!
        }
        _sFParkingsignCircleFill = sfIcon(
            name = "Monochrome.SFParkingsignCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M25.4395 12.7246C25.4395 19.7266 19.7266 25.4395 12.7148 25.4395C5.71289 25.4395 0 19.7266 0 12.7246C0 5.71289 5.71289 0 12.7148 0C19.7266 0 25.4395 5.71289 25.4395 12.7246ZM9.93164 6.78711C9.36523 6.78711 9.0625 7.19727 9.0625 7.79297L9.0625 17.6172C9.0625 18.2031 9.375 18.6133 9.94141 18.6133C10.5273 18.6133 10.8496 18.2227 10.8496 17.6172L10.8496 14.502L13.5254 14.502C15.9473 14.502 17.5781 12.9492 17.5781 10.6445C17.5781 8.35938 15.9277 6.78711 13.5352 6.78711ZM15.8203 10.6445C15.8203 12.1484 14.834 13.1152 13.3105 13.1152L10.8496 13.1152L10.8496 8.17383L13.3105 8.17383C14.8242 8.17383 15.8203 9.15039 15.8203 10.6445Z", fillAlpha = 0.85f)
        }
        return _sFParkingsignCircleFill!!
    }

private var _sFParkingsignCircleFill: ImageVector? = null
