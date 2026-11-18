package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFFryingPan (monochrome)
 * Viewport: 38.8672 x 24.7559
 */
public val SfSymbols.Monochrome.SFFryingPan: ImageVector
    get() {
        if (_sFFryingPan != null) {
            return _sFFryingPan!!
        }
        _sFFryingPan = sfIcon(
            name = "Monochrome.SFFryingPan",
            viewportWidth = 38.8672f,
            viewportHeight = 24.7559f
        ) {
            addSfPath("M0 13.7598C0 20.3613 6.18164 24.7559 15.6055 24.7559C25.0391 24.7559 31.2207 20.3613 31.2207 13.7598C31.2207 11.0059 24.6484 8.81836 15.6055 8.81836C6.57227 8.81836 0 11.0059 0 13.7598ZM1.72852 13.7598C1.72852 12.1484 8.92578 10.4492 15.6055 10.4492C22.2754 10.4492 29.4922 12.1484 29.4922 13.7598C29.4922 15.2441 23.0273 16.9141 15.6055 16.9141C8.19336 16.9141 1.72852 15.2441 1.72852 13.7598ZM2.06055 16.0449C4.42383 17.5684 9.80469 18.6426 15.6055 18.6426C21.416 18.6426 26.7969 17.5684 29.1699 16.0449C28.1641 20.2734 22.8418 23.0273 15.6055 23.0273C8.37891 23.0273 3.05664 20.2734 2.06055 16.0449ZM28.7695 12.5195L37.002 7.40234C40.5762 5.18555 37.207 0.00976562 32.9688 3.0957L23.252 10.2148L25.0586 10.7324L34.1895 4.31641C36.1719 2.89062 37.5293 5.00977 35.9473 6.01562L27.2656 11.4648ZM8.22266 17.1387L21.9336 17.2168C21.9629 14.6387 19.8438 12.8906 17.3047 12.8906C15.6348 12.8906 14.1602 13.6523 13.3203 14.8535C12.8223 14.668 12.4121 14.5996 11.875 14.5996C10.0781 14.5898 8.58398 15.6934 8.22266 17.1387Z", fillAlpha = 0.85f)
        }
        return _sFFryingPan!!
    }

private var _sFFryingPan: ImageVector? = null
