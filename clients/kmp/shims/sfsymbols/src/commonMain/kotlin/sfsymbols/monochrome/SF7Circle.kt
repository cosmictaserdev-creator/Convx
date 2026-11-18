package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SF7Circle (monochrome)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Monochrome.SF7Circle: ImageVector
    get() {
        if (_sF7Circle != null) {
            return _sF7Circle!!
        }
        _sF7Circle = sfIcon(
            name = "Monochrome.SF7Circle",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.4395C19.7363 25.4395 25.4395 19.7461 25.4395 12.7246C25.4395 5.70312 19.7363 0 12.7148 0C5.69336 0 0 5.70312 0 12.7246C0 19.7461 5.69336 25.4395 12.7148 25.4395ZM12.7148 23.623C6.68945 23.623 1.81641 18.75 1.81641 12.7246C1.81641 6.69922 6.68945 1.82617 12.7148 1.82617C18.7402 1.82617 23.6133 6.69922 23.6133 12.7246C23.6133 18.75 18.7402 23.623 12.7148 23.623Z", fillAlpha = 0.85f)
            addSfPath("M11.0156 18.623C11.3867 18.623 11.6406 18.4668 11.8652 18.0371L16.6309 8.96484C16.8457 8.56445 16.8945 8.33984 16.8945 8.0957C16.8945 7.49023 16.416 7.04102 15.6934 7.04102L9.52148 7.04102C9.13086 7.04102 8.86719 7.32422 8.86719 7.72461C8.86719 8.11523 9.14062 8.39844 9.52148 8.39844L15.0293 8.39844L15.0293 8.49609L10.3027 17.2852C10.1953 17.4805 10.1465 17.6367 10.1465 17.8516C10.1465 18.3105 10.5371 18.623 11.0156 18.623Z", fillAlpha = 0.85f)
        }
        return _sF7Circle!!
    }

private var _sF7Circle: ImageVector? = null
