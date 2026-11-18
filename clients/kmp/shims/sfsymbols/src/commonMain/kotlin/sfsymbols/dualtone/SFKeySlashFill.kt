package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFKeySlashFill (dualtone)
 * Viewport: 28.4619 x 30.9125
 */
public val SfSymbols.Dualtone.SFKeySlashFill: ImageVector
    get() {
        if (_sFKeySlashFill != null) {
            return _sFKeySlashFill!!
        }
        _sFKeySlashFill = sfIcon(
            name = "Dualtone.SFKeySlashFill",
            viewportWidth = 28.4619f,
            viewportHeight = 30.9125f
        ) {
            addSfPath("M16.9459 22.4854L15.9302 23.4983L18.4302 25.969C18.6646 26.2033 18.6743 26.5354 18.4302 26.7893L14.4751 30.7444C14.2212 30.9983 13.8403 30.9397 13.6353 30.7346L11.5552 28.6545C11.3306 28.4201 11.2427 28.1858 11.2427 27.8928L11.2427 16.7847ZM21.9849 8.04904C21.9849 11.2341 20.1129 13.9218 16.7193 15.5069L6.57506 5.37143C7.65929 2.30429 10.5782 0.12912 14.0454 0.12912C18.4497 0.12912 21.9849 3.66428 21.9849 8.04904ZM12.0044 5.31467C12.0044 6.45724 12.9126 7.37521 14.0454 7.37521C15.188 7.37521 16.106 6.45724 16.106 5.31467C16.106 4.18185 15.188 3.27365 14.0454 3.27365C12.9028 3.27365 12.0044 4.18185 12.0044 5.31467Z", fillAlpha = 0.425f)
            addSfPath("M1.77002 5.11935L24.2993 27.6291C24.6216 27.9612 25.1587 27.9612 25.4712 27.6291C25.7935 27.2971 25.8032 26.7795 25.4712 26.4572L2.95166 3.94748C2.63916 3.62521 2.10205 3.61545 1.77002 3.94748C1.44775 4.25998 1.44775 4.80685 1.77002 5.11935Z", fillAlpha = 0.85f)
        }
        return _sFKeySlashFill!!
    }

private var _sFKeySlashFill: ImageVector? = null
