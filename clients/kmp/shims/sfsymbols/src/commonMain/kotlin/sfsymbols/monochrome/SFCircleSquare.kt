package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFCircleSquare (monochrome)
 * Viewport: 23.3203 x 22.959
 */
public val SfSymbols.Monochrome.SFCircleSquare: ImageVector
    get() {
        if (_sFCircleSquare != null) {
            return _sFCircleSquare!!
        }
        _sFCircleSquare = sfIcon(
            name = "Monochrome.SFCircleSquare",
            viewportWidth = 23.3203f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M3.79883 22.959L19.1504 22.959C21.6797 22.959 22.959 21.6797 22.959 19.1992L22.959 3.76953C22.959 1.2793 21.6797 0 19.1504 0L3.79883 0C1.2793 0 0 1.26953 0 3.76953L0 19.1992C0 21.6992 1.2793 22.959 3.79883 22.959ZM3.83789 21.2305C2.4707 21.2305 1.72852 20.5078 1.72852 19.1016L1.72852 3.85742C1.72852 2.46094 2.4707 1.72852 3.83789 1.72852L19.1211 1.72852C20.459 1.72852 21.2305 2.46094 21.2305 3.85742L21.2305 19.1016C21.2305 20.5078 20.459 21.2305 19.1211 21.2305Z", fillAlpha = 0.85f)
            addSfPath("M11.4844 18.2031C15.2051 18.2031 18.2129 15.1953 18.2129 11.4746C18.2129 7.74414 15.2051 4.74609 11.4844 4.74609C7.76367 4.74609 4.75586 7.74414 4.75586 11.4746C4.75586 15.1953 7.76367 18.2031 11.4844 18.2031ZM11.4844 16.5234C8.69141 16.5234 6.43555 14.2578 6.43555 11.4746C6.43555 8.68164 8.69141 6.41602 11.4844 6.41602C14.2773 6.41602 16.5332 8.68164 16.5332 11.4746C16.5332 14.2578 14.2773 16.5234 11.4844 16.5234Z", fillAlpha = 0.85f)
        }
        return _sFCircleSquare!!
    }

private var _sFCircleSquare: ImageVector? = null
