package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFOval (monochrome)
 * Viewport: 32.168 x 24.0137
 */
public val SfSymbols.Monochrome.SFOval: ImageVector
    get() {
        if (_sFOval != null) {
            return _sFOval!!
        }
        _sFOval = sfIcon(
            name = "Monochrome.SFOval",
            viewportWidth = 32.168f,
            viewportHeight = 24.0137f
        ) {
            addSfPath("M0 12.002C0 18.9746 6.5918 24.0137 15.9082 24.0137C25.2148 24.0137 31.8066 18.9746 31.8066 12.002C31.8066 5.0293 25.2148 0 15.9082 0C6.5918 0 0 5.0293 0 12.002ZM1.72852 12.002C1.72852 6.05469 7.60742 1.73828 15.9082 1.73828C24.1992 1.73828 30.0781 6.05469 30.0781 12.002C30.0781 17.959 24.1992 22.2754 15.9082 22.2754C7.60742 22.2754 1.72852 17.959 1.72852 12.002Z", fillAlpha = 0.85f)
        }
        return _sFOval!!
    }

private var _sFOval: ImageVector? = null
