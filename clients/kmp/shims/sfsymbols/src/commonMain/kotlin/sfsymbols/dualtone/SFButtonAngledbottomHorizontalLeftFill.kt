package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFButtonAngledbottomHorizontalLeftFill (dualtone)
 * Viewport: 32.5488 x 22.2976
 */
public val SfSymbols.Dualtone.SFButtonAngledbottomHorizontalLeftFill: ImageVector
    get() {
        if (_sFButtonAngledbottomHorizontalLeftFill != null) {
            return _sFButtonAngledbottomHorizontalLeftFill!!
        }
        _sFButtonAngledbottomHorizontalLeftFill = sfIcon(
            name = "Dualtone.SFButtonAngledbottomHorizontalLeftFill",
            viewportWidth = 32.5488f,
            viewportHeight = 22.2976f
        ) {
            addSfPath("M32.1875 12.5892L32.1875 4.71815C32.1875 2.52089 30.752 1.07557 28.5352 1.07557L3.64258 1.08534C1.42578 1.08534 0 2.54042 0 4.75721L0 12.5892C0 15.7826 1.34766 17.6869 4.62891 18.6537L15.5176 21.8373C18.3887 22.6771 20.1758 22.3451 22.7344 21.0267L29.834 17.3842C31.7871 16.3783 32.1875 15.2162 32.1875 12.5892Z", fillAlpha = 0.85f)
        }
        return _sFButtonAngledbottomHorizontalLeftFill!!
    }

private var _sFButtonAngledbottomHorizontalLeftFill: ImageVector? = null
