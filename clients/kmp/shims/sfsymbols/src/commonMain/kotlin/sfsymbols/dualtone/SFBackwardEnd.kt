package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFBackwardEnd (dualtone)
 * Viewport: 18.8965 x 18.9746
 */
public val SfSymbols.Dualtone.SFBackwardEnd: ImageVector
    get() {
        if (_sFBackwardEnd != null) {
            return _sFBackwardEnd!!
        }
        _sFBackwardEnd = sfIcon(
            name = "Dualtone.SFBackwardEnd",
            viewportWidth = 18.8965f,
            viewportHeight = 18.9746f
        ) {
            addSfPath("M18.5352 17.3633L18.5352 1.60156C18.5352 0.507812 17.9004 0 17.1484 0C16.8262 0 16.4844 0.0976562 16.1621 0.283203L2.92969 8.04688C2.09961 8.52539 1.76758 8.90625 1.76758 9.48242C1.76758 10.0586 2.09961 10.4297 2.92969 10.9082L16.1621 18.6719C16.4844 18.8574 16.8262 18.9551 17.1484 18.9551C17.9004 18.9551 18.5352 18.457 18.5352 17.3633ZM16.8066 16.582C16.8066 16.748 16.6992 16.8555 16.5625 16.8555C16.5039 16.8555 16.4453 16.8457 16.3867 16.8066L4.27734 9.67773C4.17969 9.61914 4.14062 9.55078 4.14062 9.48242C4.14062 9.4043 4.17969 9.33594 4.27734 9.27734L16.3867 2.14844C16.4453 2.11914 16.5039 2.09961 16.5625 2.09961C16.6992 2.09961 16.8066 2.20703 16.8066 2.37305ZM0.917969 18.9551C1.42578 18.9551 1.82617 18.5645 1.82617 18.0566L1.82617 0.898438C1.82617 0.390625 1.42578 0 0.917969 0C0.400391 0 0 0.390625 0 0.898438L0 18.0566C0 18.5645 0.400391 18.9551 0.917969 18.9551Z", fillAlpha = 0.85f)
        }
        return _sFBackwardEnd!!
    }

private var _sFBackwardEnd: ImageVector? = null
