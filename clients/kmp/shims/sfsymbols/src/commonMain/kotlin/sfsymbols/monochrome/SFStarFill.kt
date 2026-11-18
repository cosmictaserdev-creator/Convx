package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFStarFill (monochrome)
 * Viewport: 28.0136 x 28.1738
 */
public val SfSymbols.Monochrome.SFStarFill: ImageVector
    get() {
        if (_sFStarFill != null) {
            return _sFStarFill!!
        }
        _sFStarFill = sfIcon(
            name = "Monochrome.SFStarFill",
            viewportWidth = 28.0136f,
            viewportHeight = 28.1738f
        ) {
            addSfPath("M5.30072 26.2109C5.75971 26.5527 6.33588 26.4453 7.02924 25.9375L13.8261 20.957L20.6132 25.9375C21.3163 26.4453 21.8828 26.5527 22.3515 26.2109C22.8007 25.8691 22.9081 25.3027 22.6249 24.4824L19.9589 16.4941L26.8046 11.5723C27.5078 11.0742 27.7714 10.5566 27.5956 10.0098C27.4199 9.47266 26.9023 9.20898 26.0331 9.20898L17.6249 9.20898L15.0663 1.23047C14.7929 0.410156 14.3925 0 13.8261 0C13.2499 0 12.8496 0.410156 12.5859 1.23047L10.0175 9.20898L1.60932 9.20898C0.740177 9.20898 0.232364 9.47266 0.056583 10.0098C-0.128964 10.5566 0.144474 11.0742 0.837833 11.5723L7.68354 16.4941L5.01752 24.4824C4.74408 25.3027 4.8515 25.8691 5.30072 26.2109Z", fillAlpha = 0.85f)
        }
        return _sFStarFill!!
    }

private var _sFStarFill: ImageVector? = null
