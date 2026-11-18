package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFTextDocument (monochrome)
 * Viewport: 21.3281 x 26.9238
 */
public val SfSymbols.Monochrome.SFTextDocument: ImageVector
    get() {
        if (_sFTextDocument != null) {
            return _sFTextDocument!!
        }
        _sFTextDocument = sfIcon(
            name = "Monochrome.SFTextDocument",
            viewportWidth = 21.3281f,
            viewportHeight = 26.9238f
        ) {
            addSfPath("M3.75977 26.9238L17.207 26.9238C19.707 26.9238 20.9668 25.6348 20.9668 23.125L20.9668 11.4355C20.9668 9.98047 20.791 9.3457 19.8828 8.42773L12.6465 1.09375C11.7871 0.205078 11.084 0.00976562 9.78516 0.00976562L3.75977 0.00976562C1.2793 0.00976562 0 1.28906 0 3.80859L0 23.125C0 25.6445 1.25977 26.9238 3.75977 26.9238ZM3.84766 25.1953C2.45117 25.1953 1.72852 24.4531 1.72852 23.0859L1.72852 3.84766C1.72852 2.50977 2.45117 1.73828 3.85742 1.73828L9.63867 1.73828L9.63867 9.21875C9.63867 10.7129 10.3906 11.4355 11.8555 11.4355L19.2383 11.4355L19.2383 23.0859C19.2383 24.4531 18.5156 25.1953 17.1094 25.1953ZM12.041 9.80469C11.4941 9.80469 11.2695 9.58984 11.2695 9.0332L11.2695 2.08008L18.8965 9.80469Z", fillAlpha = 0.85f)
            addSfPath("M15.2637 15.2441L5.49805 15.2441C5.09766 15.2441 4.80469 15.5371 4.80469 15.9082C4.80469 16.2891 5.09766 16.5918 5.49805 16.5918L15.2637 16.5918C15.6543 16.5918 15.9473 16.2891 15.9473 15.9082C15.9473 15.5371 15.6543 15.2441 15.2637 15.2441Z", fillAlpha = 0.85f)
            addSfPath("M15.2637 19.6484L5.49805 19.6484C5.09766 19.6484 4.80469 19.9512 4.80469 20.3418C4.80469 20.7129 5.09766 21.0059 5.49805 21.0059L15.2637 21.0059C15.6543 21.0059 15.9473 20.7129 15.9473 20.3418C15.9473 19.9512 15.6543 19.6484 15.2637 19.6484Z", fillAlpha = 0.85f)
        }
        return _sFTextDocument!!
    }

private var _sFTextDocument: ImageVector? = null
