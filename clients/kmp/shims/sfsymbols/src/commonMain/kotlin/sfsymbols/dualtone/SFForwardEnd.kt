package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFForwardEnd (dualtone)
 * Viewport: 18.8965 x 18.9746
 */
public val SfSymbols.Dualtone.SFForwardEnd: ImageVector
    get() {
        if (_sFForwardEnd != null) {
            return _sFForwardEnd!!
        }
        _sFForwardEnd = sfIcon(
            name = "Dualtone.SFForwardEnd",
            viewportWidth = 18.8965f,
            viewportHeight = 18.9746f
        ) {
            addSfPath("M0 17.3633C0 18.457 0.644531 18.9551 1.38672 18.9551C1.71875 18.9551 2.05078 18.8574 2.37305 18.6719L15.6055 10.9082C16.4355 10.4297 16.7676 10.0586 16.7676 9.48242C16.7676 8.90625 16.4355 8.52539 15.6055 8.04688L2.37305 0.283203C2.05078 0.0976562 1.71875 0 1.38672 0C0.644531 0 0 0.507812 0 1.60156ZM1.72852 16.582L1.72852 2.37305C1.72852 2.20703 1.83594 2.09961 1.97266 2.09961C2.03125 2.09961 2.08984 2.11914 2.14844 2.14844L14.2578 9.27734C14.3555 9.33594 14.3945 9.4043 14.3945 9.48242C14.3945 9.55078 14.3555 9.61914 14.2578 9.67773L2.14844 16.8066C2.08984 16.8457 2.03125 16.8555 1.97266 16.8555C1.83594 16.8555 1.72852 16.748 1.72852 16.582ZM17.627 18.9551C18.1348 18.9551 18.5352 18.5645 18.5352 18.0566L18.5352 0.898438C18.5352 0.390625 18.1348 0 17.627 0C17.1094 0 16.709 0.390625 16.709 0.898438L16.709 18.0566C16.709 18.5645 17.1094 18.9551 17.627 18.9551Z", fillAlpha = 0.85f)
        }
        return _sFForwardEnd!!
    }

private var _sFForwardEnd: ImageVector? = null
