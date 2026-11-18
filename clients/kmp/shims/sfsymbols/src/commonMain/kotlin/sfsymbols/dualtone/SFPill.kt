package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFPill (dualtone)
 * Viewport: 24.9607 x 24.6314
 */
public val SfSymbols.Dualtone.SFPill: ImageVector
    get() {
        if (_sFPill != null) {
            return _sFPill!!
        }
        _sFPill = sfIcon(
            name = "Dualtone.SFPill",
            viewportWidth = 24.9607f,
            viewportHeight = 24.6314f
        ) {
            addSfPath("M1.90419 22.7014C4.52138 25.3089 8.25185 25.2796 11.1718 22.3596L22.3632 11.1682C25.2733 8.24832 25.3026 4.52761 22.6854 1.90066C20.078-0.706763 16.3476-0.6677 13.4374 2.24246L2.24599 13.4241C-0.673933 16.344-0.703229 20.0745 1.90419 22.7014ZM3.04677 21.5589C1.08388 19.6155 1.31825 16.8225 3.47646 14.6546L14.6483 3.47292C16.787 1.32449 19.58 1.09988 21.5526 3.04324C23.5253 4.9866 23.3104 7.7698 21.1229 9.9573L9.95107 21.1292C7.82216 23.2678 5.01943 23.5022 3.04677 21.5589ZM7.96865 9.10769L15.5175 16.6565L16.6698 15.5139L9.11122 7.96511Z", fillAlpha = 0.85f)
        }
        return _sFPill!!
    }

private var _sFPill: ImageVector? = null
