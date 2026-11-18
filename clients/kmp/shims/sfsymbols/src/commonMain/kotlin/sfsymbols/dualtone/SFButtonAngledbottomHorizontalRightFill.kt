package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFButtonAngledbottomHorizontalRightFill (dualtone)
 * Viewport: 32.5488 x 22.2976
 */
public val SfSymbols.Dualtone.SFButtonAngledbottomHorizontalRightFill: ImageVector
    get() {
        if (_sFButtonAngledbottomHorizontalRightFill != null) {
            return _sFButtonAngledbottomHorizontalRightFill!!
        }
        _sFButtonAngledbottomHorizontalRightFill = sfIcon(
            name = "Dualtone.SFButtonAngledbottomHorizontalRightFill",
            viewportWidth = 32.5488f,
            viewportHeight = 22.2976f
        ) {
            addSfPath("M0 12.5892C0 15.2162 0.400391 16.3783 2.36328 17.3842L9.45312 21.0267C12.0215 22.3451 13.8086 22.6771 16.6699 21.8373L27.5684 18.6537C30.8496 17.6869 32.1875 15.7826 32.1875 12.5892L32.1875 4.75721C32.1875 2.54042 30.7617 1.08534 28.5547 1.08534L3.66211 1.07557C1.43555 1.07557 0 2.52089 0 4.71815Z", fillAlpha = 0.85f)
        }
        return _sFButtonAngledbottomHorizontalRightFill!!
    }

private var _sFButtonAngledbottomHorizontalRightFill: ImageVector? = null
