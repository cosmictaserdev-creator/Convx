package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFPlusSquare (dualtone)
 * Viewport: 23.3203 x 22.959
 */
public val SfSymbols.Dualtone.SFPlusSquare: ImageVector
    get() {
        if (_sFPlusSquare != null) {
            return _sFPlusSquare!!
        }
        _sFPlusSquare = sfIcon(
            name = "Dualtone.SFPlusSquare",
            viewportWidth = 23.3203f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M3.79883 22.959L19.1504 22.959C21.6797 22.959 22.959 21.6797 22.959 19.1992L22.959 3.76953C22.959 1.2793 21.6797 0 19.1504 0L3.79883 0C1.2793 0 0 1.26953 0 3.76953L0 19.1992C0 21.6992 1.2793 22.959 3.79883 22.959ZM3.83789 21.2305C2.4707 21.2305 1.72852 20.5078 1.72852 19.1016L1.72852 3.85742C1.72852 2.46094 2.4707 1.72852 3.83789 1.72852L19.1211 1.72852C20.459 1.72852 21.2305 2.46094 21.2305 3.85742L21.2305 19.1016C21.2305 20.5078 20.459 21.2305 19.1211 21.2305Z", fillAlpha = 0.425f)
            addSfPath("M12.3828 16.3086L12.3828 6.61133C12.3828 6.06445 12.0117 5.68359 11.4648 5.68359C10.957 5.68359 10.5859 6.06445 10.5859 6.61133L10.5859 16.3086C10.5859 16.8457 10.957 17.2266 11.4648 17.2266C12.0117 17.2266 12.3828 16.8555 12.3828 16.3086ZM6.64062 12.3535L16.3379 12.3535C16.875 12.3535 17.2559 11.9922 17.2559 11.4746C17.2559 10.9277 16.8848 10.5566 16.3379 10.5566L6.64062 10.5566C6.09375 10.5566 5.71289 10.9277 5.71289 11.4746C5.71289 11.9922 6.09375 12.3535 6.64062 12.3535Z", fillAlpha = 0.85f)
        }
        return _sFPlusSquare!!
    }

private var _sFPlusSquare: ImageVector? = null
