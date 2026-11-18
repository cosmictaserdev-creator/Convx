package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFSmokeCircleFill (dualtone)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Dualtone.SFSmokeCircleFill: ImageVector
    get() {
        if (_sFSmokeCircleFill != null) {
            return _sFSmokeCircleFill!!
        }
        _sFSmokeCircleFill = sfIcon(
            name = "Dualtone.SFSmokeCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.459C19.7266 25.459 25.4395 19.7461 25.4395 12.7344C25.4395 5.73242 19.7266 0.0195312 12.7148 0.0195312C5.71289 0.0195312 0 5.73242 0 12.7344C0 19.7461 5.71289 25.459 12.7148 25.459Z", fillAlpha = 0.2125f)
            addSfPath("M9.14062 18.4082C7.55859 18.4082 6.47461 17.2852 6.47461 15.8301C6.47461 14.4531 7.60742 13.291 9.02344 13.252C9.22852 11.9043 10.3418 10.918 11.709 10.918C12.0312 10.918 12.3047 10.9668 12.6465 11.0645C13.252 10.1465 14.2578 9.58008 15.4297 9.58008C17.2168 9.58008 18.6133 11.0059 18.6914 12.7441C20.0488 12.998 21.0547 14.1602 21.0547 15.5566C21.0547 17.1777 19.834 18.4082 18.0469 18.4082ZM3.83789 11.9629C3.83789 10.5664 4.82422 9.4043 6.20117 9.14062C6.26953 7.39258 7.73438 5.97656 9.50195 5.97656C10.625 5.97656 11.6113 6.54297 12.2461 7.49023C12.5684 7.39258 12.832 7.33398 13.1836 7.33398C14.3945 7.33398 15.1562 7.97852 15.3809 8.73047C14.1602 8.74023 13.0762 9.20898 12.3145 10.0977C12.1191 10.0684 11.9043 10.0391 11.709 10.0391C10.1367 10.0391 8.81836 11.0547 8.33008 12.4805C7.17773 12.7344 6.23047 13.5645 5.83008 14.6484C4.57031 14.248 3.83789 13.2031 3.83789 11.9629Z", fillAlpha = 0.85f)
        }
        return _sFSmokeCircleFill!!
    }

private var _sFSmokeCircleFill: ImageVector? = null
