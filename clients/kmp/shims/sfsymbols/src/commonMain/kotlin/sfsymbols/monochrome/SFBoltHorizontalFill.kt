package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFBoltHorizontalFill (monochrome)
 * Viewport: 34.5178 x 16.3184
 */
public val SfSymbols.Monochrome.SFBoltHorizontalFill: ImageVector
    get() {
        if (_sFBoltHorizontalFill != null) {
            return _sFBoltHorizontalFill!!
        }
        _sFBoltHorizontalFill = sfIcon(
            name = "Monochrome.SFBoltHorizontalFill",
            viewportWidth = 34.5178f,
            viewportHeight = 16.3184f
        ) {
            addSfPath("M0.378992 13.5254C-0.66593 14.8633 0.65243 15.9668 1.8829 15.2539L10.9942 10.0195L22.0782 15.9668C22.5079 16.1914 22.879 16.3184 23.2306 16.3184C23.7384 16.3184 24.1974 16.084 24.627 15.5371L33.836 3.88672C34.7247 2.75391 33.6505 1.37695 32.3028 2.12891L23.2208 7.39258L12.1075 1.43555C11.6876 1.20117 11.3067 1.09375 10.9552 1.09375C10.4571 1.09375 10.0177 1.32812 9.58798 1.875Z", fillAlpha = 0.85f)
        }
        return _sFBoltHorizontalFill!!
    }

private var _sFBoltHorizontalFill: ImageVector? = null
