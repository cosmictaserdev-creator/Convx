package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFLessthan (monochrome)
 * Viewport: 17.3926 x 16.3184
 */
public val SfSymbols.Monochrome.SFLessthan: ImageVector
    get() {
        if (_sFLessthan != null) {
            return _sFLessthan!!
        }
        _sFLessthan = sfIcon(
            name = "Monochrome.SFLessthan",
            viewportWidth = 17.3926f,
            viewportHeight = 16.3184f
        ) {
            addSfPath("M16.123 16.3184C16.6504 16.3184 17.0312 15.9473 17.0312 15.3613C17.0312 14.873 16.7871 14.5801 16.3379 14.3848L2.53906 8.24219L2.53906 8.11523L16.3379 1.93359C16.7871 1.72852 17.0312 1.44531 17.0312 0.957031C17.0312 0.390625 16.6406 0.00976562 16.084 0.00976562C15.791 0.00976562 15.5957 0.0878906 15.3809 0.195312L1.07422 6.82617C0.458984 7.10938 0 7.57812 0 8.25195C0 8.92578 0.458984 9.39453 1.06445 9.66797L15.3809 16.1133C15.6055 16.2207 15.8105 16.3184 16.123 16.3184Z", fillAlpha = 0.85f)
        }
        return _sFLessthan!!
    }

private var _sFLessthan: ImageVector? = null
