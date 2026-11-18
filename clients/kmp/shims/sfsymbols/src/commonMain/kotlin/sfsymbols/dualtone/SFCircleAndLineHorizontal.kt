package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFCircleAndLineHorizontal (dualtone)
 * Viewport: 35.0488 x 25.459
 */
public val SfSymbols.Dualtone.SFCircleAndLineHorizontal: ImageVector
    get() {
        if (_sFCircleAndLineHorizontal != null) {
            return _sFCircleAndLineHorizontal!!
        }
        _sFCircleAndLineHorizontal = sfIcon(
            name = "Dualtone.SFCircleAndLineHorizontal",
            viewportWidth = 35.0488f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M4.62891 12.7246L4.67281 13.5937L0.839844 13.5938C0.380859 13.5938 0 13.1934 0 12.7344C0 12.2656 0.380859 11.875 0.839844 11.875L4.67181 11.875ZM28.252 12.7246L28.2082 13.5937L6.49879 13.5937C6.46661 13.3088 6.45508 13.0181 6.45508 12.7246C6.45508 12.4377 6.4661 12.1535 6.4978 11.875L28.2091 11.875ZM34.6875 12.7344C34.6875 13.1934 34.3066 13.5938 33.8477 13.5938L30.0245 13.5937C30.0584 13.3091 30.0684 13.018 30.0684 12.7246C30.0684 12.4378 30.0589 12.1532 30.0255 11.875L33.8477 11.875C34.3066 11.875 34.6875 12.2656 34.6875 12.7344Z", fillAlpha = 0.425f)
            addSfPath("M17.3535 25.4395C24.375 25.4395 30.0684 19.7461 30.0684 12.7246C30.0684 5.70312 24.375 0 17.3535 0C10.3223 0 4.62891 5.70312 4.62891 12.7246C4.62891 19.7461 10.3223 25.4395 17.3535 25.4395ZM17.3535 23.623C11.3184 23.623 6.45508 18.75 6.45508 12.7246C6.45508 6.69922 11.3184 1.82617 17.3535 1.82617C23.3789 1.82617 28.252 6.69922 28.252 12.7246C28.252 18.75 23.3789 23.623 17.3535 23.623Z", fillAlpha = 0.85f)
        }
        return _sFCircleAndLineHorizontal!!
    }

private var _sFCircleAndLineHorizontal: ImageVector? = null
