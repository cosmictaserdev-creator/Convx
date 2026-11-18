package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFSum (monochrome)
 * Viewport: 14.873 x 23.2129
 */
public val SfSymbols.Monochrome.SFSum: ImageVector
    get() {
        if (_sFSum != null) {
            return _sFSum!!
        }
        _sFSum = sfIcon(
            name = "Monochrome.SFSum",
            viewportWidth = 14.873f,
            viewportHeight = 23.2129f
        ) {
            addSfPath("M1.43555 23.2129L13.5254 23.2129C14.082 23.2129 14.4922 22.8613 14.4922 22.3145C14.4922 21.7676 14.082 21.4062 13.5254 21.4062L2.24609 21.4062L2.24609 21.3184L9.39453 12.4023C9.67773 12.0605 9.82422 11.7285 9.82422 11.3867C9.82422 11.0352 9.66797 10.6738 9.38477 10.3516L2.26562 1.89453L2.26562 1.80664L13.5449 1.80664C14.1016 1.80664 14.5117 1.44531 14.5117 0.898438C14.5117 0.351562 14.1016 0 13.5449 0L1.46484 0C0.546875 0 0.0292969 0.644531 0.0292969 1.35742C0.0292969 1.71875 0.166016 2.09961 0.439453 2.42188L7.94922 11.3184L7.94922 11.4258L0.410156 20.791C0.136719 21.1133 0 21.4941 0 21.8652C0 22.5684 0.517578 23.2129 1.43555 23.2129Z", fillAlpha = 0.85f)
        }
        return _sFSum!!
    }

private var _sFSum: ImageVector? = null
