package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFUnderline (dualtone)
 * Viewport: 14.8047 x 26.1816
 */
public val SfSymbols.Dualtone.SFUnderline: ImageVector
    get() {
        if (_sFUnderline != null) {
            return _sFUnderline!!
        }
        _sFUnderline = sfIcon(
            name = "Dualtone.SFUnderline",
            viewportWidth = 14.8047f,
            viewportHeight = 26.1816f
        ) {
            addSfPath("M0.664062 26.1816L13.7793 26.1816C14.1602 26.1816 14.4434 25.8984 14.4434 25.5273C14.4434 25.1562 14.1602 24.873 13.7793 24.873L0.664062 24.873C0.292969 24.873 0 25.1562 0 25.5273C0 25.8984 0.292969 26.1816 0.664062 26.1816Z", fillAlpha = 0.85f)
            addSfPath("M7.2168 22.4414C11.6797 22.4414 14.4434 19.7559 14.4434 15.791L14.4434 4.72656C14.4434 4.12109 14.0527 3.74023 13.457 3.74023C12.8516 3.74023 12.4805 4.12109 12.4805 4.72656L12.4805 15.6348C12.4805 18.6328 10.5176 20.625 7.2168 20.625C3.93555 20.625 1.96289 18.6328 1.96289 15.6348L1.96289 4.72656C1.96289 4.12109 1.57227 3.74023 0.976562 3.74023C0.371094 3.74023 0 4.12109 0 4.72656L0 15.791C0 19.7559 2.76367 22.4414 7.2168 22.4414Z", fillAlpha = 0.425f)
        }
        return _sFUnderline!!
    }

private var _sFUnderline: ImageVector? = null
