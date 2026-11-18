package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFIphoneGen1 (monochrome)
 * Viewport: 16.0254 x 26.2402
 */
public val SfSymbols.Monochrome.SFIphoneGen1: ImageVector
    get() {
        if (_sFIphoneGen1 != null) {
            return _sFIphoneGen1!!
        }
        _sFIphoneGen1 = sfIcon(
            name = "Monochrome.SFIphoneGen1",
            viewportWidth = 16.0254f,
            viewportHeight = 26.2402f
        ) {
            addSfPath("M0 23.0078C0 24.9805 1.28906 26.2305 3.33008 26.2305L12.3242 26.2305C14.3652 26.2305 15.6641 24.9805 15.6641 23.0078L15.6641 3.22266C15.6641 1.25 14.3652 0 12.3242 0L3.33008 0C1.28906 0 0 1.25 0 3.22266ZM1.72852 22.1289L1.72852 4.10156L13.9258 4.10156L13.9258 22.1289ZM7.8418 25.0293C7.26562 25.0293 6.79688 24.5508 6.79688 23.9746C6.79688 23.3984 7.26562 22.9395 7.8418 22.9395C8.41797 22.9395 8.88672 23.3984 8.88672 23.9746C8.88672 24.5508 8.41797 25.0293 7.8418 25.0293ZM5.5957 2.11914C5.5957 1.85547 5.77148 1.67969 6.03516 1.67969L9.63867 1.67969C9.89258 1.67969 10.0781 1.85547 10.0781 2.11914C10.0781 2.38281 9.89258 2.55859 9.63867 2.55859L6.03516 2.55859C5.77148 2.55859 5.5957 2.38281 5.5957 2.11914Z", fillAlpha = 0.85f)
        }
        return _sFIphoneGen1!!
    }

private var _sFIphoneGen1: ImageVector? = null
