package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFShippingboxCircle (dualtone)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Dualtone.SFShippingboxCircle: ImageVector
    get() {
        if (_sFShippingboxCircle != null) {
            return _sFShippingboxCircle!!
        }
        _sFShippingboxCircle = sfIcon(
            name = "Dualtone.SFShippingboxCircle",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.4395C19.7363 25.4395 25.4395 19.7461 25.4395 12.7246C25.4395 5.70312 19.7363 0 12.7148 0C5.69336 0 0 5.70312 0 12.7246C0 19.7461 5.69336 25.4395 12.7148 25.4395ZM12.7148 23.623C6.68945 23.623 1.81641 18.75 1.81641 12.7246C1.81641 6.69922 6.68945 1.82617 12.7148 1.82617C18.7402 1.82617 23.6133 6.69922 23.6133 12.7246C23.6133 18.75 18.7402 23.623 12.7148 23.623Z", fillAlpha = 0.425f)
            addSfPath("M13.125 19.8926C13.1836 19.8828 13.2227 19.8535 13.2617 19.834L18.5547 16.8555C19.1309 16.5234 19.4336 16.1816 19.4336 15.3418L19.4336 10.0586C19.4336 9.87305 19.4238 9.72656 19.3848 9.59961L13.125 13.125ZM12.3047 19.8926L12.3047 13.125L6.05469 9.59961C6.00586 9.72656 6.00586 9.87305 6.00586 10.0586L6.00586 15.3418C6.00586 16.1816 6.29883 16.5234 6.875 16.8555L12.168 19.834C12.207 19.8535 12.2461 19.8828 12.3047 19.8926ZM12.7148 12.4121L15.5957 10.791L9.26758 7.2168L6.76758 8.63281C6.62109 8.70117 6.51367 8.7793 6.42578 8.86719ZM16.4453 10.3125L19.0039 8.86719C18.9258 8.7793 18.8086 8.70117 18.6621 8.63281L13.7988 5.86914C13.4277 5.6543 13.0664 5.55664 12.7148 5.55664C12.373 5.55664 12.002 5.6543 11.6406 5.86914L10.0977 6.73828Z", fillAlpha = 0.85f)
        }
        return _sFShippingboxCircle!!
    }

private var _sFShippingboxCircle: ImageVector? = null
