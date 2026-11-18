package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFOctagonRighthalfFilled (monochrome)
 * Viewport: 25.4785 x 24.2676
 */
public val SfSymbols.Monochrome.SFOctagonRighthalfFilled: ImageVector
    get() {
        if (_sFOctagonRighthalfFilled != null) {
            return _sFOctagonRighthalfFilled!!
        }
        _sFOctagonRighthalfFilled = sfIcon(
            name = "Monochrome.SFOctagonRighthalfFilled",
            viewportWidth = 25.4785f,
            viewportHeight = 24.2676f
        ) {
            addSfPath("M16.2305 24.2578C17.9688 24.2578 18.6914 23.6816 19.5508 22.7539L24.1113 17.7637C24.9121 16.8848 25.1172 16.3086 25.1172 15.0684L25.1172 9.18945C25.1172 7.94922 24.9121 7.38281 24.1113 6.49414L19.5508 1.51367C18.6914 0.576172 17.9688 0 16.2305 0L8.88672 0C7.14844 0 6.42578 0.576172 5.55664 1.51367L1.00586 6.49414C0.205078 7.38281 0 7.94922 0 9.18945L0 15.0684C0 16.3086 0.205078 16.8848 1.00586 17.7637L5.55664 22.7539C6.42578 23.6816 7.14844 24.2578 8.88672 24.2578ZM12.5586 1.71875L12.5586 22.5391L9.41406 22.5391C8.02734 22.5391 7.65625 22.2168 6.86523 21.377L2.58789 16.709C2.08008 16.1523 1.96289 15.8105 1.96289 14.8828L1.96289 9.375C1.96289 8.44727 2.08008 8.11523 2.58789 7.55859L6.86523 2.88086C7.65625 2.04102 8.02734 1.71875 9.41406 1.71875Z", fillAlpha = 0.85f)
        }
        return _sFOctagonRighthalfFilled!!
    }

private var _sFOctagonRighthalfFilled: ImageVector? = null
