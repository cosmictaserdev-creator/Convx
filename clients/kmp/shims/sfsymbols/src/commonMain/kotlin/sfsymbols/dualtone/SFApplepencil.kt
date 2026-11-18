package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFApplepencil (dualtone)
 * Viewport: 20.407 x 19.9897
 */
public val SfSymbols.Dualtone.SFApplepencil: ImageVector
    get() {
        if (_sFApplepencil != null) {
            return _sFApplepencil!!
        }
        _sFApplepencil = sfIcon(
            name = "Dualtone.SFApplepencil",
            viewportWidth = 20.407f,
            viewportHeight = 19.9897f
        ) {
            addSfPath("M0.0472506 19.4821C-0.128531 19.8044 0.223032 20.0974 0.496469 19.9509L2.32264 18.9646L1.06288 17.7048ZM1.59999 16.7478L3.2992 18.447L3.99256 18.0661L19.6273 2.50949C20.184 1.95285 20.184 1.05441 19.6273 0.507534C19.0707-0.0491068 18.1625-0.0491068 17.6254 0.497768L1.99061 16.0642Z", fillAlpha = 0.85f)
        }
        return _sFApplepencil!!
    }

private var _sFApplepencil: ImageVector? = null
