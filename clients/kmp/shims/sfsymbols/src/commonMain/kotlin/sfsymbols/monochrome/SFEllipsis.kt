package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFEllipsis (monochrome)
 * Viewport: 23.1641 x 4.58008
 */
public val SfSymbols.Monochrome.SFEllipsis: ImageVector
    get() {
        if (_sFEllipsis != null) {
            return _sFEllipsis!!
        }
        _sFEllipsis = sfIcon(
            name = "Monochrome.SFEllipsis",
            viewportWidth = 23.1641f,
            viewportHeight = 4.58008f
        ) {
            addSfPath("M20.5176 4.56055C21.7773 4.56055 22.8027 3.54492 22.8027 2.28516C22.8027 1.01562 21.7773 0 20.5176 0C19.2578 0 18.2324 1.01562 18.2324 2.28516C18.2324 3.54492 19.2578 4.56055 20.5176 4.56055Z", fillAlpha = 0.85f)
            addSfPath("M11.3965 4.56055C12.666 4.56055 13.6816 3.54492 13.6816 2.28516C13.6816 1.01562 12.666 0 11.3965 0C10.1367 0 9.12109 1.01562 9.12109 2.28516C9.12109 3.54492 10.1367 4.56055 11.3965 4.56055Z", fillAlpha = 0.85f)
            addSfPath("M2.28516 4.56055C3.54492 4.56055 4.57031 3.54492 4.57031 2.28516C4.57031 1.01562 3.54492 0 2.28516 0C1.01562 0 0 1.01562 0 2.28516C0 3.54492 1.01562 4.56055 2.28516 4.56055Z", fillAlpha = 0.85f)
        }
        return _sFEllipsis!!
    }

private var _sFEllipsis: ImageVector? = null
