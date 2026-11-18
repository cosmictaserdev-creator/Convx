package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFPointerArrowSquare (dualtone)
 * Viewport: 23.3203 x 22.959
 */
public val SfSymbols.Dualtone.SFPointerArrowSquare: ImageVector
    get() {
        if (_sFPointerArrowSquare != null) {
            return _sFPointerArrowSquare!!
        }
        _sFPointerArrowSquare = sfIcon(
            name = "Dualtone.SFPointerArrowSquare",
            viewportWidth = 23.3203f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M3.79883 22.959L19.1504 22.959C21.6797 22.959 22.959 21.6797 22.959 19.1992L22.959 3.76953C22.959 1.2793 21.6797 0 19.1504 0L3.79883 0C1.2793 0 0 1.26953 0 3.76953L0 19.1992C0 21.6992 1.2793 22.959 3.79883 22.959ZM3.83789 21.2305C2.4707 21.2305 1.72852 20.5078 1.72852 19.1016L1.72852 3.85742C1.72852 2.46094 2.4707 1.72852 3.83789 1.72852L19.1211 1.72852C20.459 1.72852 21.2305 2.46094 21.2305 3.85742L21.2305 19.1016C21.2305 20.5078 20.459 21.2305 19.1211 21.2305Z", fillAlpha = 0.425f)
            addSfPath("M14.0332 18.457C14.541 18.252 14.7852 17.666 14.5703 17.1484L12.5684 12.4902L15.3711 12.4316C15.8008 12.4219 16.0156 11.9922 15.7129 11.6602L8.81836 4.44336C8.52539 4.15039 8.06641 4.32617 8.05664 4.74609L8.01758 14.834C8.00781 15.2734 8.53516 15.4297 8.82812 15.1367L10.7715 13.1641L12.7051 17.9199C12.9102 18.4277 13.5059 18.6719 14.0332 18.457Z", fillAlpha = 0.85f)
        }
        return _sFPointerArrowSquare!!
    }

private var _sFPointerArrowSquare: ImageVector? = null
