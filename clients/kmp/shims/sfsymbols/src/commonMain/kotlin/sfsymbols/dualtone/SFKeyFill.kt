package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFKeyFill (dualtone)
 * Viewport: 16.2402 x 30.9125
 */
public val SfSymbols.Dualtone.SFKeyFill: ImageVector
    get() {
        if (_sFKeyFill != null) {
            return _sFKeyFill!!
        }
        _sFKeyFill = sfIcon(
            name = "Dualtone.SFKeyFill",
            viewportWidth = 16.2402f,
            viewportHeight = 30.9125f
        ) {
            addSfPath("M7.93945 0.12912C3.52539 0.12912 0 3.65451 0 8.04904C0 11.3889 2.00195 14.3283 5.12695 15.4904L5.12695 27.8928C5.12695 28.1858 5.22461 28.4201 5.44922 28.6545L7.51953 30.7346C7.72461 30.9397 8.11523 30.9983 8.35938 30.7444L12.3145 26.7893C12.5586 26.5354 12.5488 26.2033 12.3145 25.969L9.82422 23.4983L13.2422 20.0901C13.457 19.8654 13.457 19.5237 13.2031 19.2795L9.80469 15.8518C13.7012 14.3283 15.8789 11.4768 15.8789 8.04904C15.8789 3.66428 12.334 0.12912 7.93945 0.12912ZM7.93945 7.37521C6.79688 7.37521 5.88867 6.45724 5.88867 5.31467C5.88867 4.18185 6.78711 3.27365 7.93945 3.27365C9.07227 3.27365 10 4.18185 10 5.31467C10 6.45724 9.07227 7.37521 7.93945 7.37521Z", fillAlpha = 0.85f)
        }
        return _sFKeyFill!!
    }

private var _sFKeyFill: ImageVector? = null
