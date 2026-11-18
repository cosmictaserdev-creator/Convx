package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFGuidepointVertical (monochrome)
 * Viewport: 8.20312 x 26.9043
 */
public val SfSymbols.Monochrome.SFGuidepointVertical: ImageVector
    get() {
        if (_sFGuidepointVertical != null) {
            return _sFGuidepointVertical!!
        }
        _sFGuidepointVertical = sfIcon(
            name = "Monochrome.SFGuidepointVertical",
            viewportWidth = 8.20312f,
            viewportHeight = 26.9043f
        ) {
            addSfPath("M3.91602 7.91016C6.08398 7.91016 7.8418 6.15234 7.8418 3.98438C7.8418 1.82617 6.08398 0.0683594 3.91602 0.0683594C1.75781 0.0683594 0 1.82617 0 3.98438C0 6.15234 1.75781 7.91016 3.91602 7.91016ZM3.92578 20.2441C4.39453 20.2441 4.78516 19.8535 4.78516 19.3848L4.78516 7.59766C4.78516 7.11914 4.39453 6.72852 3.92578 6.72852C3.44727 6.72852 3.05664 7.11914 3.05664 7.59766L3.05664 19.3848C3.05664 19.8535 3.44727 20.2441 3.92578 20.2441ZM3.92578 26.9043C6.08398 26.9043 7.8418 25.1562 7.8418 22.998C7.8418 20.8203 6.08398 19.0625 3.92578 19.0625C1.75781 19.0625 0 20.8203 0 22.998C0 25.1562 1.75781 26.9043 3.92578 26.9043Z", fillAlpha = 0.85f)
        }
        return _sFGuidepointVertical!!
    }

private var _sFGuidepointVertical: ImageVector? = null
