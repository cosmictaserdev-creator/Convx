package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFAspectratioFill (monochrome)
 * Viewport: 29.9512 x 22.9785
 */
public val SfSymbols.Monochrome.SFAspectratioFill: ImageVector
    get() {
        if (_sFAspectratioFill != null) {
            return _sFAspectratioFill!!
        }
        _sFAspectratioFill = sfIcon(
            name = "Monochrome.SFAspectratioFill",
            viewportWidth = 29.9512f,
            viewportHeight = 22.9785f
        ) {
            addSfPath("M0 8.50586L0 6.95312L19.5703 6.95312C21.5723 6.95312 22.6562 8.02734 22.6562 10L22.6562 22.9785L21.1035 22.9785L21.1035 10.166C21.1035 9.12109 20.498 8.50586 19.4629 8.50586L15.5273 8.50586L15.5273 22.9785L13.9746 22.9785L13.9746 8.50586ZM3.79883 22.9688L25.7812 22.9688C28.3105 22.9688 29.5898 21.6895 29.5898 19.209L29.5898 3.7793C29.5898 1.28906 28.3105 0.00976562 25.7812 0.00976562L3.79883 0.00976562C1.2793 0.00976562 0 1.2793 0 3.7793L0 19.209C0 21.709 1.2793 22.9688 3.79883 22.9688Z", fillAlpha = 0.85f)
        }
        return _sFAspectratioFill!!
    }

private var _sFAspectratioFill: ImageVector? = null
