package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFExternaldriveFill (dualtone)
 * Viewport: 29.1699 x 20.8496
 */
public val SfSymbols.Dualtone.SFExternaldriveFill: ImageVector
    get() {
        if (_sFExternaldriveFill != null) {
            return _sFExternaldriveFill!!
        }
        _sFExternaldriveFill = sfIcon(
            name = "Dualtone.SFExternaldriveFill",
            viewportWidth = 29.1699f,
            viewportHeight = 20.8496f
        ) {
            addSfPath("M4.92188 20.8496L23.8867 20.8496C26.7578 20.8496 28.8086 18.8086 28.8086 15.957C28.8086 13.1152 26.7578 11.0645 23.8867 11.0645L4.92188 11.0645C2.05078 11.0645 0 13.1152 0 15.957C0 18.8086 2.05078 20.8496 4.92188 20.8496ZM4.92188 9.50195L23.8867 9.50195C25.0488 9.50195 26.1035 9.77539 27.0215 10.2539L23.8379 2.80273C23.0762 0.986328 21.5332 0.0195312 19.4727 0.0195312L9.33594 0.0195312C7.27539 0.0195312 5.72266 0.986328 4.9707 2.80273L1.77734 10.2539C2.69531 9.77539 3.75977 9.50195 4.92188 9.50195ZM22.1289 15.9863C22.1289 15.1465 22.8613 14.4141 23.7305 14.4141C24.5703 14.4141 25.293 15.1465 25.293 15.9863C25.293 16.8555 24.5703 17.5586 23.7305 17.5586C22.8613 17.5586 22.1289 16.875 22.1289 15.9863Z", fillAlpha = 0.85f)
        }
        return _sFExternaldriveFill!!
    }

private var _sFExternaldriveFill: ImageVector? = null
