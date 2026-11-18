package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFDocumentCircle (monochrome)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Monochrome.SFDocumentCircle: ImageVector
    get() {
        if (_sFDocumentCircle != null) {
            return _sFDocumentCircle!!
        }
        _sFDocumentCircle = sfIcon(
            name = "Monochrome.SFDocumentCircle",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.4395C19.7363 25.4395 25.4395 19.7461 25.4395 12.7246C25.4395 5.70312 19.7363 0 12.7148 0C5.69336 0 0 5.70312 0 12.7246C0 19.7461 5.69336 25.4395 12.7148 25.4395ZM12.7148 23.623C6.68945 23.623 1.81641 18.75 1.81641 12.7246C1.81641 6.69922 6.68945 1.82617 12.7148 1.82617C18.7402 1.82617 23.6133 6.69922 23.6133 12.7246C23.6133 18.75 18.7402 23.623 12.7148 23.623Z", fillAlpha = 0.85f)
            addSfPath("M9.69727 19.4531L16.2988 19.4531C17.6465 19.4531 18.3203 18.7598 18.3203 17.4023L18.3203 11.8359L13.3496 11.8359C12.5293 11.8359 12.1387 11.4453 12.1387 10.625L12.1387 5.5957L9.69727 5.5957C8.35938 5.5957 7.67578 6.28906 7.67578 7.64648L7.67578 17.4023C7.67578 18.7695 8.34961 19.4531 9.69727 19.4531ZM13.4766 10.8984L18.2324 10.8984C18.2031 10.6543 18.0176 10.4102 17.7344 10.127L13.8477 6.18164C13.5742 5.89844 13.3203 5.72266 13.0859 5.67383L13.0859 10.498C13.0859 10.7617 13.2129 10.8984 13.4766 10.8984Z", fillAlpha = 0.85f)
        }
        return _sFDocumentCircle!!
    }

private var _sFDocumentCircle: ImageVector? = null
