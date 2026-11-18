package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFPlus (dualtone)
 * Viewport: 20.918 x 20.5762
 */
public val SfSymbols.Dualtone.SFPlus: ImageVector
    get() {
        if (_sFPlus != null) {
            return _sFPlus!!
        }
        _sFPlus = sfIcon(
            name = "Dualtone.SFPlus",
            viewportWidth = 20.918f,
            viewportHeight = 20.5762f
        ) {
            addSfPath("M11.2305 19.5996L11.2305 0.957031C11.2305 0.439453 10.8008 0 10.2734 0C9.75586 0 9.32617 0.439453 9.32617 0.957031L9.32617 19.5996C9.32617 20.1172 9.75586 20.5566 10.2734 20.5566C10.8008 20.5566 11.2305 20.1172 11.2305 19.5996ZM0.957031 11.2305L19.5996 11.2305C20.1172 11.2305 20.5566 10.8008 20.5566 10.2832C20.5566 9.75586 20.1172 9.32617 19.5996 9.32617L0.957031 9.32617C0.439453 9.32617 0 9.75586 0 10.2832C0 10.8008 0.439453 11.2305 0.957031 11.2305Z", fillAlpha = 0.85f)
        }
        return _sFPlus!!
    }

private var _sFPlus: ImageVector? = null
