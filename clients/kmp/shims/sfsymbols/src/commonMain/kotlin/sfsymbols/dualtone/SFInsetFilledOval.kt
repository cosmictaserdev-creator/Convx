package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFInsetFilledOval (dualtone)
 * Viewport: 32.168 x 24.0137
 */
public val SfSymbols.Dualtone.SFInsetFilledOval: ImageVector
    get() {
        if (_sFInsetFilledOval != null) {
            return _sFInsetFilledOval!!
        }
        _sFInsetFilledOval = sfIcon(
            name = "Dualtone.SFInsetFilledOval",
            viewportWidth = 32.168f,
            viewportHeight = 24.0137f
        ) {
            addSfPath("M0 12.002C0 18.9746 6.5918 24.0137 15.9082 24.0137C25.2148 24.0137 31.8066 18.9746 31.8066 12.002C31.8066 5.0293 25.2148 0 15.9082 0C6.5918 0 0 5.0293 0 12.002ZM1.72852 12.002C1.72852 6.05469 7.60742 1.73828 15.9082 1.73828C24.1992 1.73828 30.0781 6.05469 30.0781 12.002C30.0781 17.959 24.1992 22.2754 15.9082 22.2754C7.60742 22.2754 1.72852 17.959 1.72852 12.002Z", fillAlpha = 0.425f)
            addSfPath("M3.38867 12.002C3.38867 16.9922 8.55469 20.625 15.9082 20.625C23.252 20.625 28.418 16.9922 28.418 12.002C28.418 7.01172 23.2422 3.38867 15.9082 3.38867C8.56445 3.38867 3.38867 7.01172 3.38867 12.002Z", fillAlpha = 0.85f)
        }
        return _sFInsetFilledOval!!
    }

private var _sFInsetFilledOval: ImageVector? = null
