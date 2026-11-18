package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFFlameFill (dualtone)
 * Viewport: 21.1133 x 29.3262
 */
public val SfSymbols.Dualtone.SFFlameFill: ImageVector
    get() {
        if (_sFFlameFill != null) {
            return _sFFlameFill!!
        }
        _sFFlameFill = sfIcon(
            name = "Dualtone.SFFlameFill",
            viewportWidth = 21.1133f,
            viewportHeight = 29.3262f
        ) {
            addSfPath("M9.80469 27.0215C16.3672 27.0215 20.752 22.5781 20.752 15.8594C20.752 4.69727 11.25 0 4.53125 0C3.44727 0 2.7832 0.380859 2.7832 1.11328C2.7832 1.39648 2.91016 1.68945 3.16406 1.98242C4.66797 3.78906 6.18164 5.95703 6.20117 8.37891C6.20117 9.00391 6.10352 9.57031 5.66406 10.2539L6.25 10.1367C5.66406 8.32031 4.08203 7.06055 2.83203 7.06055C2.37305 7.06055 2.07031 7.39258 2.07031 7.88086C2.07031 8.21289 2.16797 8.98438 2.16797 9.54102C2.16797 12.5098 0 14.0723 0 18.5645C0 23.6523 3.88672 27.0215 9.80469 27.0215ZM10.0586 23.4766C7.76367 23.4766 6.25 22.0996 6.25 20.0293C6.25 17.8711 7.77344 17.0898 7.97852 15.7031C7.99805 15.5859 8.06641 15.5566 8.16406 15.625C8.73047 16.123 9.11133 16.748 9.42383 17.4609C10.0781 16.582 10.3906 14.7266 10.1562 12.6855C10.1367 12.5781 10.2148 12.5098 10.3223 12.5586C13.0078 13.8379 14.4141 16.4941 14.4141 18.8965C14.4141 21.3379 12.9883 23.4766 10.0586 23.4766Z", fillAlpha = 0.85f)
        }
        return _sFFlameFill!!
    }

private var _sFFlameFill: ImageVector? = null
