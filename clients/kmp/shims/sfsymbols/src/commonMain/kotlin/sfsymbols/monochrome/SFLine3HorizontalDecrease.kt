package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFLine3HorizontalDecrease (monochrome)
 * Viewport: 25.6738 x 14.5605
 */
public val SfSymbols.Monochrome.SFLine3HorizontalDecrease: ImageVector
    get() {
        if (_sFLine3HorizontalDecrease != null) {
            return _sFLine3HorizontalDecrease!!
        }
        _sFLine3HorizontalDecrease = sfIcon(
            name = "Monochrome.SFLine3HorizontalDecrease",
            viewportWidth = 25.6738f,
            viewportHeight = 14.5605f
        ) {
            addSfPath("M6.35742 14.541L19.0039 14.541C19.4824 14.541 19.8633 14.1602 19.8633 13.6816C19.8633 13.1934 19.4824 12.8125 19.0039 12.8125L6.35742 12.8125C5.87891 12.8125 5.49805 13.1934 5.49805 13.6816C5.49805 14.1602 5.87891 14.541 6.35742 14.541Z", fillAlpha = 0.85f)
            addSfPath("M3.63281 8.13477L21.7285 8.13477C22.207 8.13477 22.5879 7.75391 22.5879 7.27539C22.5879 6.78711 22.207 6.40625 21.7285 6.40625L3.63281 6.40625C3.1543 6.40625 2.77344 6.78711 2.77344 7.27539C2.77344 7.75391 3.1543 8.13477 3.63281 8.13477Z", fillAlpha = 0.85f)
            addSfPath("M0.869141 1.72852L24.4434 1.72852C24.9316 1.72852 25.3125 1.34766 25.3125 0.859375C25.3125 0.380859 24.9316 0 24.4434 0L0.869141 0C0.380859 0 0 0.380859 0 0.859375C0 1.34766 0.380859 1.72852 0.869141 1.72852Z", fillAlpha = 0.85f)
        }
        return _sFLine3HorizontalDecrease!!
    }

private var _sFLine3HorizontalDecrease: ImageVector? = null
