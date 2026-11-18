package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFLocationSlashCircle (monochrome)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Monochrome.SFLocationSlashCircle: ImageVector
    get() {
        if (_sFLocationSlashCircle != null) {
            return _sFLocationSlashCircle!!
        }
        _sFLocationSlashCircle = sfIcon(
            name = "Monochrome.SFLocationSlashCircle",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M25.4395 12.7246C25.4395 19.7461 19.7363 25.4395 12.7148 25.4395C5.69336 25.4395 0 19.7461 0 12.7246C0 5.70312 5.69336 0 12.7148 0C19.7363 0 25.4395 5.70312 25.4395 12.7246ZM1.81641 12.7246C1.81641 18.75 6.68945 23.623 12.7148 23.623C18.7402 23.623 23.6133 18.75 23.6133 12.7246C23.6133 6.69922 18.7402 1.82617 12.7148 1.82617C6.68945 1.82617 1.81641 6.69922 1.81641 12.7246Z", fillAlpha = 0.85f)
            addSfPath("M14.5975 16.5116L13.0273 19.8535C12.6953 20.5762 11.6309 20.4785 11.6309 19.4824L11.6309 14.0137C11.6309 13.7988 11.4941 13.6719 11.2891 13.6719L5.75195 13.6621C4.79492 13.6621 4.6582 12.6172 5.40039 12.2656L8.76069 10.6748ZM18.7305 7.71484L15.8222 13.905L11.3634 9.44255L17.5293 6.52344C18.4082 6.11328 19.1504 6.83594 18.7305 7.71484Z", fillAlpha = 0.85f)
            addSfPath("M18.2812 18.9941C18.4766 19.1992 18.8086 19.1992 19.0137 18.9941C19.2188 18.7891 19.2188 18.4766 19.0137 18.2617L7.1875 6.43555C6.98242 6.23047 6.65039 6.23047 6.44531 6.43555C6.24023 6.64062 6.25 6.97266 6.44531 7.16797Z", fillAlpha = 0.85f)
        }
        return _sFLocationSlashCircle!!
    }

private var _sFLocationSlashCircle: ImageVector? = null
