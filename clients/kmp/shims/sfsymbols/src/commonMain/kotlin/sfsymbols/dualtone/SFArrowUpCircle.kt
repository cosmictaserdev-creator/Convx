package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFArrowUpCircle (dualtone)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Dualtone.SFArrowUpCircle: ImageVector
    get() {
        if (_sFArrowUpCircle != null) {
            return _sFArrowUpCircle!!
        }
        _sFArrowUpCircle = sfIcon(
            name = "Dualtone.SFArrowUpCircle",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.4395C19.7363 25.4395 25.4395 19.7461 25.4395 12.7246C25.4395 5.70312 19.7363 0 12.7148 0C5.69336 0 0 5.70312 0 12.7246C0 19.7461 5.69336 25.4395 12.7148 25.4395ZM12.7148 23.623C6.68945 23.623 1.81641 18.75 1.81641 12.7246C1.81641 6.69922 6.68945 1.82617 12.7148 1.82617C18.7402 1.82617 23.6133 6.69922 23.6133 12.7246C23.6133 18.75 18.7402 23.623 12.7148 23.623Z", fillAlpha = 0.425f)
            addSfPath("M12.7148 19.0234C13.1836 19.0234 13.5742 18.6426 13.5742 18.1934L13.5742 10.6543L13.4961 8.18359C13.4863 7.70508 13.1055 7.43164 12.7148 7.43164C12.3242 7.43164 11.9531 7.70508 11.9336 8.18359L11.8652 10.6543L11.8652 18.1934C11.8652 18.6426 12.2559 19.0234 12.7148 19.0234ZM12.7148 6.41602C12.5 6.41602 12.3047 6.48438 12.0996 6.69922L7.91016 10.7715C7.74414 10.9277 7.65625 11.1035 7.65625 11.3281C7.65625 11.7676 7.97852 12.0898 8.42773 12.0898C8.64258 12.0898 8.87695 12.002 9.02344 11.8359L11.1035 9.64844L12.7148 7.95898L12.7148 7.95898L14.3262 9.64844L16.3965 11.8359C16.5527 12.002 16.7773 12.0898 16.9922 12.0898C17.4316 12.0898 17.7734 11.7676 17.7734 11.3281C17.7734 11.1035 17.6758 10.9277 17.5195 10.7715L13.3398 6.69922C13.125 6.48438 12.9395 6.41602 12.7148 6.41602Z", fillAlpha = 0.85f)
        }
        return _sFArrowUpCircle!!
    }

private var _sFArrowUpCircle: ImageVector? = null
