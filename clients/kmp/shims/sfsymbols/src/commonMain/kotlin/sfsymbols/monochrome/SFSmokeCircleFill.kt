package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFSmokeCircleFill (monochrome)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Monochrome.SFSmokeCircleFill: ImageVector
    get() {
        if (_sFSmokeCircleFill != null) {
            return _sFSmokeCircleFill!!
        }
        _sFSmokeCircleFill = sfIcon(
            name = "Monochrome.SFSmokeCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M25.4395 12.7344C25.4395 19.7461 19.7266 25.459 12.7148 25.459C5.71289 25.459 0 19.7461 0 12.7344C0 5.73242 5.71289 0.0195312 12.7148 0.0195312C19.7266 0.0195312 25.4395 5.73242 25.4395 12.7344ZM12.6465 11.0645C12.3047 10.9668 12.0312 10.918 11.709 10.918C10.3418 10.918 9.22852 11.9043 9.02344 13.252C7.60742 13.291 6.47461 14.4531 6.47461 15.8301C6.47461 17.2852 7.55859 18.4082 9.14062 18.4082L18.0469 18.4082C19.834 18.4082 21.0547 17.1777 21.0547 15.5566C21.0547 14.1602 20.0488 12.998 18.6914 12.7441C18.6133 11.0059 17.2168 9.58008 15.4297 9.58008C14.2578 9.58008 13.252 10.1465 12.6465 11.0645ZM6.20117 9.14062C4.82422 9.4043 3.83789 10.5664 3.83789 11.9629C3.83789 13.2031 4.57031 14.248 5.83008 14.6484C6.23047 13.5645 7.17773 12.7344 8.33008 12.4805C8.81836 11.0547 10.1367 10.0391 11.709 10.0391C11.9043 10.0391 12.1191 10.0684 12.3145 10.0977C13.0762 9.20898 14.1602 8.74023 15.3809 8.73047C15.1562 7.97852 14.3945 7.33398 13.1836 7.33398C12.832 7.33398 12.5684 7.39258 12.2461 7.49023C11.6113 6.54297 10.625 5.97656 9.50195 5.97656C7.73438 5.97656 6.26953 7.39258 6.20117 9.14062Z", fillAlpha = 0.85f)
        }
        return _sFSmokeCircleFill!!
    }

private var _sFSmokeCircleFill: ImageVector? = null
