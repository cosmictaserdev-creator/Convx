package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFArrowDownLeft (dualtone)
 * Viewport: 18.8574 x 18.5254
 */
public val SfSymbols.Dualtone.SFArrowDownLeft: ImageVector
    get() {
        if (_sFArrowDownLeft != null) {
            return _sFArrowDownLeft!!
        }
        _sFArrowDownLeft = sfIcon(
            name = "Dualtone.SFArrowDownLeft",
            viewportWidth = 18.8574f,
            viewportHeight = 18.5254f
        ) {
            addSfPath("M18.2031 0.371094C17.8027-0.0195312 17.2656-0.0488281 16.8555 0.371094L3.60352 13.6133L1.15234 16.2207C0.78125 16.5918 0.791016 17.0703 1.13281 17.4219C1.48438 17.7637 1.97266 17.7734 2.33398 17.4121L4.95117 14.9609L18.2031 1.69922C18.6035 1.29883 18.584 0.751953 18.2031 0.371094ZM1.93359 10.1953L1.93359 4.63867C1.93359 4.13086 1.50391 3.67188 0.966797 3.67188C0.449219 3.67188 0 4.10156 0 4.67773L0.0195312 17.5C0.0195312 18.1055 0.419922 18.5254 1.04492 18.5254L13.8672 18.5254C14.4629 18.5254 14.8633 18.0762 14.8633 17.5586C14.8633 17.0508 14.4141 16.6211 13.9062 16.6211L8.83789 16.6211L1.69922 16.8262Z", fillAlpha = 0.85f)
        }
        return _sFArrowDownLeft!!
    }

private var _sFArrowDownLeft: ImageVector? = null
