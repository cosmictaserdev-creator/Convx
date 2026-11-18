package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFXCircle (monochrome)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Monochrome.SFXCircle: ImageVector
    get() {
        if (_sFXCircle != null) {
            return _sFXCircle!!
        }
        _sFXCircle = sfIcon(
            name = "Monochrome.SFXCircle",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.4395C19.7363 25.4395 25.4395 19.7461 25.4395 12.7246C25.4395 5.70312 19.7363 0 12.7148 0C5.69336 0 0 5.70312 0 12.7246C0 19.7461 5.69336 25.4395 12.7148 25.4395ZM12.7148 23.623C6.68945 23.623 1.81641 18.75 1.81641 12.7246C1.81641 6.69922 6.68945 1.82617 12.7148 1.82617C18.7402 1.82617 23.6133 6.69922 23.6133 12.7246C23.6133 18.75 18.7402 23.623 12.7148 23.623Z", fillAlpha = 0.85f)
            addSfPath("M8.99414 18.4668C9.3457 18.4668 9.53125 18.3398 9.80469 17.9688L12.6367 13.877L12.6855 13.877L15.5078 17.9688C15.7812 18.3398 15.9668 18.4668 16.3184 18.4668C16.7969 18.4668 17.1582 18.1348 17.1582 17.6758C17.1582 17.4609 17.0898 17.2949 16.9531 17.1094L13.75 12.627L16.9824 8.125C17.1289 7.92969 17.1875 7.75391 17.1875 7.55859C17.1875 7.12891 16.8359 6.80664 16.377 6.80664C16.0352 6.80664 15.8301 6.92383 15.5859 7.28516L12.832 11.377L12.7734 11.377L9.9707 7.27539C9.73633 6.92383 9.52148 6.80664 9.15039 6.80664C8.69141 6.80664 8.32031 7.1582 8.32031 7.59766C8.32031 7.83203 8.37891 7.98828 8.54492 8.22266L11.6406 12.5781L8.38867 17.1582C8.25195 17.3633 8.20312 17.5 8.20312 17.7246C8.20312 18.1348 8.54492 18.4668 8.99414 18.4668Z", fillAlpha = 0.85f)
        }
        return _sFXCircle!!
    }

private var _sFXCircle: ImageVector? = null
