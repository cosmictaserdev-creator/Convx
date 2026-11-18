package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFHatWidebrimFill (dualtone)
 * Viewport: 35.0977 x 22.666
 */
public val SfSymbols.Dualtone.SFHatWidebrimFill: ImageVector
    get() {
        if (_sFHatWidebrimFill != null) {
            return _sFHatWidebrimFill!!
        }
        _sFHatWidebrimFill = sfIcon(
            name = "Dualtone.SFHatWidebrimFill",
            viewportWidth = 35.0977f,
            viewportHeight = 22.666f
        ) {
            addSfPath("M17.3633 21.9922C27.6855 21.9922 34.7363 18.4961 34.7363 13.6523C34.7363 10.3027 31.1914 7.57812 25.6738 6.63086C24.8145 2.55859 21.9531 0 17.3633 0C12.7832 0 9.92188 2.55859 9.0625 6.63086C3.54492 7.57812 0 10.3027 0 13.6523C0 18.4961 7.05078 21.9922 17.3633 21.9922ZM17.3242 15.293C13.6914 15.293 10.7227 14.3555 9.08203 12.9004C8.92578 11.709 8.90625 10.0977 9.01367 8.92578C10.8691 10.5371 14.1406 11.5527 17.3242 11.5527C20.5078 11.5527 23.7891 10.5371 25.6445 8.92578C25.752 10.0977 25.7324 11.709 25.5664 12.9004C23.9355 14.3555 20.9668 15.293 17.3242 15.293Z", fillAlpha = 0.85f)
        }
        return _sFHatWidebrimFill!!
    }

private var _sFHatWidebrimFill: ImageVector? = null
