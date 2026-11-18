package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFDoorSlidingRightHandOpen (monochrome)
 * Viewport: 30.5176 x 26.4551
 */
public val SfSymbols.Monochrome.SFDoorSlidingRightHandOpen: ImageVector
    get() {
        if (_sFDoorSlidingRightHandOpen != null) {
            return _sFDoorSlidingRightHandOpen!!
        }
        _sFDoorSlidingRightHandOpen = sfIcon(
            name = "Monochrome.SFDoorSlidingRightHandOpen",
            viewportWidth = 30.5176f,
            viewportHeight = 26.4551f
        ) {
            addSfPath("M29.2871 26.4355C29.7656 26.4355 30.1562 26.0449 30.1562 25.5664L30.1562 2.28516C30.1562 0.917969 29.2188 0 27.8125 0L2.34375 0C0.9375 0 0 0.917969 0 2.28516L0 25.5664C0 26.0449 0.380859 26.4355 0.869141 26.4355C1.34766 26.4355 1.73828 26.0449 1.73828 25.5664L1.73828 2.40234C1.73828 2.00195 1.99219 1.73828 2.37305 1.73828L27.7832 1.73828C28.1543 1.73828 28.418 2.00195 28.418 2.40234L28.418 25.5664C28.418 26.0449 28.7988 26.4355 29.2871 26.4355Z", fillAlpha = 0.85f)
            addSfPath("M14.5215 26.0156C14.7266 26.0156 14.8926 25.8496 14.8926 25.6445L14.8926 3.4668C14.8926 3.26172 14.7266 3.0957 14.5215 3.0957L3.4668 3.0957C3.26172 3.0957 3.0957 3.26172 3.0957 3.4668L3.0957 25.6445C3.0957 25.8496 3.26172 26.0156 3.4668 26.0156ZM12.0703 16.4453C11.7285 16.4453 11.4844 16.2012 11.4844 15.8594L11.4844 13.6523C11.4844 13.3105 11.7285 13.0664 12.0703 13.0664C12.4121 13.0664 12.6465 13.3105 12.6465 13.6523L12.6465 15.8594C12.6465 16.2012 12.4121 16.4453 12.0703 16.4453Z", fillAlpha = 0.85f)
        }
        return _sFDoorSlidingRightHandOpen!!
    }

private var _sFDoorSlidingRightHandOpen: ImageVector? = null
