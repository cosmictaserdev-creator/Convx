package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFFryingPanFill (dualtone)
 * Viewport: 38.6719 x 24.7559
 */
public val SfSymbols.Dualtone.SFFryingPanFill: ImageVector
    get() {
        if (_sFFryingPanFill != null) {
            return _sFFryingPanFill!!
        }
        _sFFryingPanFill = sfIcon(
            name = "Dualtone.SFFryingPanFill",
            viewportWidth = 38.6719f,
            viewportHeight = 24.7559f
        ) {
            addSfPath("M0 13.7598C0 20.3613 6.18164 24.7559 15.6055 24.7559C25.0391 24.7559 31.2207 20.3613 31.2207 13.7598C31.2207 11.0059 24.6484 8.81836 15.6055 8.81836C6.57227 8.81836 0 11.0059 0 13.7598ZM1.72852 13.7598C1.72852 12.1484 8.92578 10.4492 15.6055 10.4492C22.2754 10.4492 29.4922 12.1484 29.4922 13.7598C29.4922 15.2441 23.0273 17.0117 15.6055 17.0117C8.19336 17.0117 1.72852 15.2441 1.72852 13.7598ZM28.75 12.3047L36.9043 7.22656C40.2344 5.15625 37.0996 0.322266 33.0859 3.25195L23.6621 10.1758ZM8.22266 17.1387L21.9336 17.2168C21.9629 14.6387 19.8438 12.8906 17.3047 12.8906C15.6348 12.8906 14.1602 13.6523 13.3203 14.8535C12.8223 14.668 12.4121 14.5996 11.875 14.5996C10.0781 14.5898 8.58398 15.6934 8.22266 17.1387Z", fillAlpha = 0.85f)
        }
        return _sFFryingPanFill!!
    }

private var _sFFryingPanFill: ImageVector? = null
