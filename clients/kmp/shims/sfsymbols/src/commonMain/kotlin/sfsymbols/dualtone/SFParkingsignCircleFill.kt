package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFParkingsignCircleFill (dualtone)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Dualtone.SFParkingsignCircleFill: ImageVector
    get() {
        if (_sFParkingsignCircleFill != null) {
            return _sFParkingsignCircleFill!!
        }
        _sFParkingsignCircleFill = sfIcon(
            name = "Dualtone.SFParkingsignCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.4395C19.7266 25.4395 25.4395 19.7266 25.4395 12.7246C25.4395 5.71289 19.7266 0 12.7148 0C5.71289 0 0 5.71289 0 12.7246C0 19.7266 5.71289 25.4395 12.7148 25.4395Z", fillAlpha = 0.2125f)
            addSfPath("M9.94141 18.6133C9.375 18.6133 9.0625 18.2031 9.0625 17.6172L9.0625 7.79297C9.0625 7.19727 9.36523 6.78711 9.93164 6.78711L13.5352 6.78711C15.9277 6.78711 17.5781 8.35938 17.5781 10.6445C17.5781 12.9492 15.9473 14.502 13.5254 14.502L10.8496 14.502L10.8496 17.6172C10.8496 18.2227 10.5273 18.6133 9.94141 18.6133ZM10.8496 13.1152L13.3105 13.1152C14.834 13.1152 15.8203 12.1484 15.8203 10.6445C15.8203 9.15039 14.8242 8.17383 13.3105 8.17383L10.8496 8.17383Z", fillAlpha = 0.85f)
        }
        return _sFParkingsignCircleFill!!
    }

private var _sFParkingsignCircleFill: ImageVector? = null
