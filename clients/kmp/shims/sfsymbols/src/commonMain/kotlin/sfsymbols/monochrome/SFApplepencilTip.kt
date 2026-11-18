package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFApplepencilTip (monochrome)
 * Viewport: 12.3858 x 24.3848
 */
public val SfSymbols.Monochrome.SFApplepencilTip: ImageVector
    get() {
        if (_sFApplepencilTip != null) {
            return _sFApplepencilTip!!
        }
        _sFApplepencilTip = sfIcon(
            name = "Monochrome.SFApplepencilTip",
            viewportWidth = 12.3858f,
            viewportHeight = 24.3848f
        ) {
            addSfPath("M0.870619 24.375C1.34913 24.375 1.71046 24.0332 1.72999 23.4961L2.07179 17.373L3.5757 10.9668L2.47218 11.8359L9.55226 11.8359L8.44874 10.9668L9.95265 17.373L10.2944 23.4961C10.314 24.0332 10.6753 24.375 11.1538 24.375C11.6421 24.375 12.0523 24.0137 12.023 23.4277L11.6909 17.2461C11.6812 17.0703 11.6714 16.9531 11.6226 16.8164L10.0503 10.6641C9.98195 10.3809 9.75734 10.2051 9.47413 10.2051L2.55031 10.2051C2.2671 10.2051 2.04249 10.3809 1.97413 10.6641L0.401869 16.8164C0.353041 16.9531 0.343275 17.0703 0.33351 17.2461L0.00147851 23.4277C-0.0278184 24.0137 0.382338 24.375 0.870619 24.375Z", fillAlpha = 0.85f)
            addSfPath("M2.42335 8.65234L9.55226 8.65234L6.94484 0.732422C6.79835 0.273438 6.47609 0 5.99757 0C5.49953 0 5.14796 0.273438 5.00148 0.732422Z", fillAlpha = 0.85f)
        }
        return _sFApplepencilTip!!
    }

private var _sFApplepencilTip: ImageVector? = null
