package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFCableConnectorHorizontal (monochrome)
 * Viewport: 30.6543 x 8.75977
 */
public val SfSymbols.Monochrome.SFCableConnectorHorizontal: ImageVector
    get() {
        if (_sFCableConnectorHorizontal != null) {
            return _sFCableConnectorHorizontal!!
        }
        _sFCableConnectorHorizontal = sfIcon(
            name = "Monochrome.SFCableConnectorHorizontal",
            viewportWidth = 30.6543f,
            viewportHeight = 8.75977f
        ) {
            addSfPath("M0 5.30273L12.7246 5.30273L12.7246 3.44727L0 3.44727ZM12.1777 8.75L22.8906 8.75C23.9258 8.75 24.3555 8.31055 24.3555 7.27539L24.3555 1.46484C24.3555 0.429688 23.9258 0 22.8906 0L12.1777 0C11.1426 0 10.7031 0.429688 10.7031 1.46484L10.7031 7.27539C10.7031 8.31055 11.1426 8.75 12.1777 8.75Z", fillAlpha = 0.85f)
            addSfPath("M25.7129 6.95312L28.8281 6.95312C29.8633 6.95312 30.293 6.50391 30.293 5.46875L30.293 3.28125C30.293 2.24609 29.8633 1.79688 28.8281 1.79688L25.7129 1.79688Z", fillAlpha = 0.85f)
        }
        return _sFCableConnectorHorizontal!!
    }

private var _sFCableConnectorHorizontal: ImageVector? = null
