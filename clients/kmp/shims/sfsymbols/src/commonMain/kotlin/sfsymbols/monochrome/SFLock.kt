package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFLock (monochrome)
 * Viewport: 17.5 x 25.2051
 */
public val SfSymbols.Monochrome.SFLock: ImageVector
    get() {
        if (_sFLock != null) {
            return _sFLock!!
        }
        _sFLock = sfIcon(
            name = "Monochrome.SFLock",
            viewportWidth = 17.5f,
            viewportHeight = 25.2051f
        ) {
            addSfPath("M2.73438 24.541L14.4043 24.541C16.1523 24.541 17.1387 23.5352 17.1387 21.6602L17.1387 12.7734C17.1387 10.8984 16.1523 9.89258 14.4043 9.89258L2.73438 9.89258C0.976562 9.89258 0 10.8984 0 12.7734L0 21.6602C0 23.5352 0.976562 24.541 2.73438 24.541ZM2.77344 22.9102C2.13867 22.9102 1.73828 22.4805 1.73828 21.7676L1.73828 12.6562C1.73828 11.9434 2.13867 11.5234 2.77344 11.5234L14.375 11.5234C15.0098 11.5234 15.3906 11.9434 15.3906 12.6562L15.3906 21.7676C15.3906 22.4805 15.0098 22.9102 14.375 22.9102ZM2.23633 10.7129L3.95508 10.7129L3.95508 6.72852C3.95508 3.47656 6.01562 1.63086 8.56445 1.63086C11.1133 1.63086 13.1934 3.47656 13.1934 6.72852L13.1934 10.7129L14.9121 10.7129L14.9121 6.92383C14.9121 2.38281 11.9434 0 8.56445 0C5.19531 0 2.23633 2.38281 2.23633 6.92383Z", fillAlpha = 0.85f)
        }
        return _sFLock!!
    }

private var _sFLock: ImageVector? = null
