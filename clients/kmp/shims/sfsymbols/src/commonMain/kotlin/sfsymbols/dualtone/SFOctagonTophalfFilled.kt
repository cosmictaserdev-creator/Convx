package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFOctagonTophalfFilled (dualtone)
 * Viewport: 25.4785 x 24.2676
 */
public val SfSymbols.Dualtone.SFOctagonTophalfFilled: ImageVector
    get() {
        if (_sFOctagonTophalfFilled != null) {
            return _sFOctagonTophalfFilled!!
        }
        _sFOctagonTophalfFilled = sfIcon(
            name = "Dualtone.SFOctagonTophalfFilled",
            viewportWidth = 25.4785f,
            viewportHeight = 24.2676f
        ) {
            addSfPath("M8.88672 24.2578L16.2305 24.2578C17.9688 24.2578 18.6914 23.6816 19.5508 22.7539L24.1113 17.7637C24.9121 16.8848 25.1172 16.3086 25.1172 15.0684L25.1172 9.18945C25.1172 7.94922 24.9121 7.38281 24.1113 6.49414L19.5508 1.51367C18.6914 0.576172 17.9688 0 16.2305 0L8.88672 0C7.14844 0 6.42578 0.576172 5.55664 1.51367L1.00586 6.49414C0.205078 7.38281 0 7.94922 0 9.18945L0 15.0684C0 16.3086 0.205078 16.8848 1.00586 17.7637L5.55664 22.7539C6.42578 23.6816 7.14844 24.2578 8.88672 24.2578ZM23.1445 12.1289L23.1445 14.8828C23.1445 15.8105 23.0371 16.1523 22.5293 16.709L18.2422 21.377C17.4609 22.2168 17.0898 22.5391 15.6934 22.5391L9.41406 22.5391C8.02734 22.5391 7.65625 22.2168 6.86523 21.377L2.58789 16.709C2.08008 16.1523 1.96289 15.8105 1.96289 14.8828L1.96289 12.1289Z", fillAlpha = 0.85f)
        }
        return _sFOctagonTophalfFilled!!
    }

private var _sFOctagonTophalfFilled: ImageVector? = null
