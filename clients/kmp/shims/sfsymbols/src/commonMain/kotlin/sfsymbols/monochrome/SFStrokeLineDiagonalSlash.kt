package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFStrokeLineDiagonalSlash (monochrome)
 * Viewport: 22.9614 x 22.6151
 */
public val SfSymbols.Monochrome.SFStrokeLineDiagonalSlash: ImageVector
    get() {
        if (_sFStrokeLineDiagonalSlash != null) {
            return _sFStrokeLineDiagonalSlash!!
        }
        _sFStrokeLineDiagonalSlash = sfIcon(
            name = "Monochrome.SFStrokeLineDiagonalSlash",
            viewportWidth = 22.9614f,
            viewportHeight = 22.6151f
        ) {
            addSfPath("M11.0727 13.6735L3.55103 21.1952C2.96509 21.7812 1.9983 21.7714 1.41236 21.1855C0.845954 20.5995 0.845954 19.6523 1.42213 19.0761L8.94717 11.5469ZM21.1878 1.44913C21.7542 2.0253 21.7542 2.9921 21.178 3.56827L13.6557 11.0905L11.5295 8.96315L19.0491 1.43936C19.635 0.863191 20.6116 0.863191 21.1878 1.44913Z", fillAlpha = 0.85f)
            addSfPath("M19.928 21.6444C20.3967 22.1132 21.1682 22.1132 21.6272 21.6542C22.1057 21.1757 22.1057 20.414 21.6272 19.9355L2.68189 0.980378C2.21314 0.511628 1.46119 0.501863 0.972907 0.970613C0.513922 1.4296 0.484626 2.19132 0.972907 2.6796Z", fillAlpha = 0.85f)
        }
        return _sFStrokeLineDiagonalSlash!!
    }

private var _sFStrokeLineDiagonalSlash: ImageVector? = null
