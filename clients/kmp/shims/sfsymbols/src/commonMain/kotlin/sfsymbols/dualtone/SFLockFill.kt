package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFLockFill (dualtone)
 * Viewport: 17.5 x 25.2246
 */
public val SfSymbols.Dualtone.SFLockFill: ImageVector
    get() {
        if (_sFLockFill != null) {
            return _sFLockFill!!
        }
        _sFLockFill = sfIcon(
            name = "Dualtone.SFLockFill",
            viewportWidth = 17.5f,
            viewportHeight = 25.2246f
        ) {
            addSfPath("M2.73438 24.5508L14.4043 24.5508C16.1523 24.5508 17.1387 23.5352 17.1387 21.6602L17.1387 12.7734C17.1387 10.9082 16.1523 9.89258 14.4043 9.89258L2.73438 9.89258C0.976562 9.89258 0 10.9082 0 12.7734L0 21.6602C0 23.5352 0.976562 24.5508 2.73438 24.5508ZM2.23633 10.7227L3.95508 10.7227L3.95508 6.72852C3.95508 3.47656 6.01562 1.63086 8.56445 1.63086C11.1133 1.63086 13.1934 3.47656 13.1934 6.72852L13.1934 10.7227L14.9121 10.7227L14.9121 6.93359C14.9121 2.38281 11.9434 0 8.56445 0C5.19531 0 2.23633 2.38281 2.23633 6.93359Z", fillAlpha = 0.85f)
        }
        return _sFLockFill!!
    }

private var _sFLockFill: ImageVector? = null
