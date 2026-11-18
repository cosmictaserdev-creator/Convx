package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFKCircle (monochrome)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Monochrome.SFKCircle: ImageVector
    get() {
        if (_sFKCircle != null) {
            return _sFKCircle!!
        }
        _sFKCircle = sfIcon(
            name = "Monochrome.SFKCircle",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.4395C19.7363 25.4395 25.4395 19.7461 25.4395 12.7246C25.4395 5.70312 19.7363 0 12.7148 0C5.69336 0 0 5.70312 0 12.7246C0 19.7461 5.69336 25.4395 12.7148 25.4395ZM12.7148 23.623C6.68945 23.623 1.81641 18.75 1.81641 12.7246C1.81641 6.69922 6.68945 1.82617 12.7148 1.82617C18.7402 1.82617 23.6133 6.69922 23.6133 12.7246C23.6133 18.75 18.7402 23.623 12.7148 23.623Z", fillAlpha = 0.85f)
            addSfPath("M9.41406 18.4668C9.9707 18.4668 10.2832 18.1055 10.2832 17.5098L10.2832 14.6094L11.8652 12.9883L15.7129 18.0273C15.9473 18.3301 16.1719 18.457 16.4844 18.457C16.9727 18.457 17.3145 18.125 17.3145 17.6465C17.3145 17.4219 17.2168 17.1875 17.0703 16.9727L13.125 11.8652L16.748 8.1543C16.9434 7.95898 17.0215 7.77344 17.0215 7.53906C17.0215 7.12891 16.6699 6.80664 16.2305 6.80664C15.9473 6.80664 15.7617 6.89453 15.5176 7.14844L10.3516 12.3926L10.2832 12.3926L10.2832 7.76367C10.2832 7.1582 9.9707 6.80664 9.41406 6.80664C8.88672 6.80664 8.56445 7.1582 8.56445 7.76367L8.56445 17.5098C8.56445 18.1055 8.88672 18.4668 9.41406 18.4668Z", fillAlpha = 0.85f)
        }
        return _sFKCircle!!
    }

private var _sFKCircle: ImageVector? = null
