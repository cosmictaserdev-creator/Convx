package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFBatteryblock (dualtone)
 * Viewport: 29.9512 x 21.3574
 */
public val SfSymbols.Dualtone.SFBatteryblock: ImageVector
    get() {
        if (_sFBatteryblock != null) {
            return _sFBatteryblock!!
        }
        _sFBatteryblock = sfIcon(
            name = "Dualtone.SFBatteryblock",
            viewportWidth = 29.9512f,
            viewportHeight = 21.3574f
        ) {
            addSfPath("M3.79883 21.3574L25.7812 21.3574C28.3105 21.3574 29.5898 20.0879 29.5898 17.5977L29.5898 6.99219C29.5898 4.50195 28.3105 3.23242 25.7812 3.23242L24.1602 3.23242L24.1602 1.46484C24.1602 0.556641 23.6426 0.00976562 22.7051 0.00976562L20.1074 0.00976562C19.1797 0.00976562 18.6523 0.556641 18.6523 1.46484L18.6523 3.23242L10.9277 3.23242L10.9277 1.46484C10.9277 0.556641 10.4102 0.00976562 9.47266 0.00976562L6.875 0.00976562C5.94727 0.00976562 5.41992 0.556641 5.41992 1.46484L5.41992 3.23242L3.79883 3.23242C1.2793 3.23242 0 4.49219 0 6.99219L0 17.5977C0 20.0977 1.2793 21.3574 3.79883 21.3574ZM1.72852 17.5098L1.72852 7.08008C1.72852 5.68359 2.4707 4.96094 3.83789 4.96094L25.752 4.96094C27.0898 4.96094 27.8516 5.68359 27.8516 7.08008L27.8516 17.5098C27.8516 18.9062 27.0898 19.6289 25.752 19.6289L3.83789 19.6289C2.4707 19.6289 1.72852 18.9062 1.72852 17.5098Z", fillAlpha = 0.85f)
        }
        return _sFBatteryblock!!
    }

private var _sFBatteryblock: ImageVector? = null
