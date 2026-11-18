package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFMosaic (monochrome)
 * Viewport: 23.3203 x 22.959
 */
public val SfSymbols.Monochrome.SFMosaic: ImageVector
    get() {
        if (_sFMosaic != null) {
            return _sFMosaic!!
        }
        _sFMosaic = sfIcon(
            name = "Monochrome.SFMosaic",
            viewportWidth = 23.3203f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M14.1504 22.4512L15.8887 22.4512L15.8887 0.537109L14.1504 0.537109ZM0.517578 15.8984L22.4414 15.8984L22.4414 14.1699L0.517578 14.1699ZM7.07031 22.4512L8.79883 22.4512L8.79883 14.1699L7.07031 14.1699ZM14.1504 8.81836L22.4414 8.81836L22.4414 7.08984L14.1504 7.08984ZM3.79883 22.959L19.1504 22.959C21.6797 22.959 22.959 21.6797 22.959 19.1992L22.959 3.76953C22.959 1.2793 21.6797 0 19.1504 0L3.79883 0C1.2793 0 0 1.26953 0 3.76953L0 19.1992C0 21.6992 1.2793 22.959 3.79883 22.959ZM3.83789 21.2305C2.4707 21.2305 1.72852 20.5078 1.72852 19.1016L1.72852 3.85742C1.72852 2.46094 2.4707 1.72852 3.83789 1.72852L19.1211 1.72852C20.459 1.72852 21.2305 2.46094 21.2305 3.85742L21.2305 19.1016C21.2305 20.5078 20.459 21.2305 19.1211 21.2305Z", fillAlpha = 0.85f)
        }
        return _sFMosaic!!
    }

private var _sFMosaic: ImageVector? = null
