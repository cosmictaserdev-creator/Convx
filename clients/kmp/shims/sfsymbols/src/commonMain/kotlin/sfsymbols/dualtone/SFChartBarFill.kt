package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFChartBarFill (dualtone)
 * Viewport: 31.6406 x 23.6621
 */
public val SfSymbols.Dualtone.SFChartBarFill: ImageVector
    get() {
        if (_sFChartBarFill != null) {
            return _sFChartBarFill!!
        }
        _sFChartBarFill = sfIcon(
            name = "Dualtone.SFChartBarFill",
            viewportWidth = 31.6406f,
            viewportHeight = 23.6621f
        ) {
            addSfPath("M25 23.5059L28.7793 23.5059C30.4297 23.5059 31.2793 22.7051 31.2793 21.1035L31.2793 2.40234C31.2793 0.800781 30.4297 0 28.7793 0L25 0C23.3594 0 22.5098 0.800781 22.5098 2.40234L22.5098 21.1035C22.5098 22.7051 23.3594 23.5059 25 23.5059Z", fillAlpha = 0.85f)
            addSfPath("M13.7402 23.5059L17.5195 23.5059C19.1797 23.5059 20.0195 22.7051 20.0195 21.1035L20.0195 6.06445C20.0195 4.47266 19.1797 3.66211 17.5195 3.66211L13.7402 3.66211C12.0996 3.66211 11.2598 4.47266 11.2598 6.06445L11.2598 21.1035C11.2598 22.7051 12.0996 23.5059 13.7402 23.5059Z", fillAlpha = 0.85f)
            addSfPath("M2.48047 23.5059L6.26953 23.5059C7.91992 23.5059 8.75977 22.7051 8.75977 21.1035L8.75977 9.70703C8.75977 8.10547 7.91992 7.30469 6.26953 7.30469L2.48047 7.30469C0.839844 7.30469 0 8.10547 0 9.70703L0 21.1035C0 22.7051 0.839844 23.5059 2.48047 23.5059Z", fillAlpha = 0.85f)
        }
        return _sFChartBarFill!!
    }

private var _sFChartBarFill: ImageVector? = null
