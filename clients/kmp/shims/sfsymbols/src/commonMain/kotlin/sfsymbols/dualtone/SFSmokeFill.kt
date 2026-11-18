package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFSmokeFill (dualtone)
 * Viewport: 33.1055 x 25.0293
 */
public val SfSymbols.Dualtone.SFSmokeFill: ImageVector
    get() {
        if (_sFSmokeFill != null) {
            return _sFSmokeFill!!
        }
        _sFSmokeFill = sfIcon(
            name = "Dualtone.SFSmokeFill",
            viewportWidth = 33.1055f,
            viewportHeight = 25.0293f
        ) {
            addSfPath("M16.0156 4.19922C16.5918 4.00391 17.0801 3.91602 17.6562 3.91602C19.4358 3.91602 20.9761 4.86742 21.7965 6.32494C21.7489 6.31885 21.6997 6.31836 21.6504 6.31836C19.4434 6.31836 17.4805 7.24609 16.123 8.7793C15.7715 8.7207 15.4395 8.68164 15.0879 8.68164C12.3047 8.68164 9.9707 10.4492 9.11133 12.9785C7.03921 13.4398 5.37334 14.9387 4.66945 16.8659C2.54302 16.203 1.19141 14.3577 1.19141 12.1191C1.19141 9.64844 2.95898 7.59766 5.3418 7.14844C5.48828 4.07227 7.95898 1.55273 11.0938 1.55273C13.1641 1.55273 14.9316 2.55859 16.0156 4.19922Z", fillAlpha = 0.85f)
            addSfPath("M10.5566 23.4668L26.2793 23.4668C29.4043 23.4668 31.5527 21.2695 31.5527 18.4375C31.5527 15.9766 29.7852 13.9258 27.4121 13.4766C27.2559 10.4004 24.7852 7.88086 21.6504 7.88086C19.5801 7.88086 17.8125 8.88672 16.7285 10.5176C16.1523 10.332 15.6641 10.2441 15.0879 10.2441C12.6758 10.2441 10.7031 11.9922 10.3613 14.375C7.8418 14.4434 5.85938 16.4844 5.85938 18.916C5.85938 21.4844 7.75391 23.4668 10.5566 23.4668Z", fillAlpha = 0.85f)
        }
        return _sFSmokeFill!!
    }

private var _sFSmokeFill: ImageVector? = null
