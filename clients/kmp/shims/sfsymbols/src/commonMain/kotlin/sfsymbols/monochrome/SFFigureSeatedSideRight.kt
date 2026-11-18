package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFFigureSeatedSideRight (monochrome)
 * Viewport: 24.1504 x 25.166
 */
public val SfSymbols.Monochrome.SFFigureSeatedSideRight: ImageVector
    get() {
        if (_sFFigureSeatedSideRight != null) {
            return _sFFigureSeatedSideRight!!
        }
        _sFFigureSeatedSideRight = sfIcon(
            name = "Monochrome.SFFigureSeatedSideRight",
            viewportWidth = 24.1504f,
            viewportHeight = 25.166f
        ) {
            addSfPath("M3.34961 6.73828C5.21484 6.73828 6.70898 5.26367 6.70898 3.39844C6.70898 1.52344 5.21484 0 3.34961 0C1.49414 0 0 1.52344 0 3.39844C0 5.19531 1.49414 6.73828 3.34961 6.73828ZM23.3398 24.4727C23.9258 23.877 24.0039 22.8809 23.2227 22.0898L17.6758 16.3477C16.5137 15.1367 15.293 14.9707 13.8281 15.5078L10.8691 16.582L7.38281 10.2344C6.32812 8.29102 4.51172 7.56836 2.76367 8.53516C0.947266 9.49219 0.615234 11.4453 1.66016 13.3594L4.28711 18.125C5.48828 20.3223 7.97852 21.2598 10.6543 20.2441L14.8828 18.6328C15.1562 18.5254 15.2832 18.5742 15.4688 18.7598L20.8008 24.2773C21.5918 25.0879 22.6953 25.1465 23.3398 24.4727Z", fillAlpha = 0.85f)
        }
        return _sFFigureSeatedSideRight!!
    }

private var _sFFigureSeatedSideRight: ImageVector? = null
