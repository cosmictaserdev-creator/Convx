package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFMountFill (dualtone)
 * Viewport: 24.1895 x 29.4434
 */
public val SfSymbols.Dualtone.SFMountFill: ImageVector
    get() {
        if (_sFMountFill != null) {
            return _sFMountFill!!
        }
        _sFMountFill = sfIcon(
            name = "Dualtone.SFMountFill",
            viewportWidth = 24.1895f,
            viewportHeight = 29.4434f
        ) {
            addSfPath("M0.0683594 9.56055C0.0683594 10.2441 0.546875 10.8789 1.49414 10.8789L22.373 10.8789C23.3203 10.8789 23.7988 10.2441 23.7988 9.56055C23.7988 9.11133 23.5938 8.64258 23.1445 8.29102L13.3887 0.556641C12.9102 0.195312 12.4512 0 11.9336 0C11.416 0 10.9473 0.195312 10.4785 0.556641L0.722656 8.29102C0.273438 8.64258 0.0683594 9.11133 0.0683594 9.56055ZM0.0195312 14.7168C0.0195312 15.2637 0.410156 15.6445 0.986328 15.6445L22.8711 15.6445C23.4375 15.6445 23.8281 15.2637 23.8281 14.7168C23.8281 14.1602 23.4375 13.7793 22.8711 13.7793L0.986328 13.7793C0.410156 13.7793 0.0195312 14.1602 0.0195312 14.7168ZM0 19.873C0 20.3125 0.205078 20.791 0.654297 21.1328L10.4102 28.8672C10.8789 29.2383 11.3477 29.4238 11.8652 29.4238C12.3828 29.4238 12.8418 29.2383 13.3203 28.8672L23.0762 21.1328C23.5254 20.791 23.7305 20.3125 23.7305 19.873C23.7305 19.1895 23.252 18.5547 22.3047 18.5547L1.42578 18.5547C0.478516 18.5547 0 19.1895 0 19.873Z", fillAlpha = 0.85f)
        }
        return _sFMountFill!!
    }

private var _sFMountFill: ImageVector? = null
