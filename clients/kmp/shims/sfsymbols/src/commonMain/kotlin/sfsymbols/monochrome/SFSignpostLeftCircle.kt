package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFSignpostLeftCircle (monochrome)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Monochrome.SFSignpostLeftCircle: ImageVector
    get() {
        if (_sFSignpostLeftCircle != null) {
            return _sFSignpostLeftCircle!!
        }
        _sFSignpostLeftCircle = sfIcon(
            name = "Monochrome.SFSignpostLeftCircle",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.4395C19.7363 25.4395 25.4395 19.7461 25.4395 12.7246C25.4395 5.70312 19.7363 0 12.7148 0C5.69336 0 0 5.70312 0 12.7246C0 19.7461 5.69336 25.4395 12.7148 25.4395ZM12.7148 23.623C6.68945 23.623 1.81641 18.75 1.81641 12.7246C1.81641 6.69922 6.68945 1.82617 12.7148 1.82617C18.7402 1.82617 23.6133 6.69922 23.6133 12.7246C23.6133 18.75 18.7402 23.623 12.7148 23.623Z", fillAlpha = 0.85f)
            addSfPath("M17.373 13.9355C18.1934 13.9355 18.6621 13.4668 18.6621 12.6562L18.6621 9.14062C18.6621 8.33984 18.1934 7.89062 17.373 7.89062L13.3398 7.89062L13.3398 6.38672C13.3398 6.04492 13.0664 5.76172 12.7051 5.76172C12.3828 5.76172 12.0996 6.04492 12.0996 6.38672L12.0996 7.89062L8.91602 7.89062C8.37891 7.89062 8.10547 8.04688 7.79297 8.33984L6.08398 9.95117C5.40039 10.5859 5.42969 11.2402 6.07422 11.8555L7.79297 13.4668C8.10547 13.7598 8.37891 13.9258 8.91602 13.9258L12.0996 13.9258L12.0996 18.4375L10.2539 18.4375C9.91211 18.4375 9.63867 18.7109 9.63867 19.0527C9.63867 19.3945 9.91211 19.668 10.2539 19.668L15.1758 19.668C15.5078 19.668 15.791 19.3945 15.791 19.0527C15.791 18.7109 15.5078 18.4375 15.1758 18.4375L13.3398 18.4375L13.3398 13.9258Z", fillAlpha = 0.85f)
        }
        return _sFSignpostLeftCircle!!
    }

private var _sFSignpostLeftCircle: ImageVector? = null
