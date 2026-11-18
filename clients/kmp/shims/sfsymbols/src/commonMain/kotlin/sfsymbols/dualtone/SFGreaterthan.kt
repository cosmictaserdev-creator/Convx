package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFGreaterthan (dualtone)
 * Viewport: 17.3926 x 16.3184
 */
public val SfSymbols.Dualtone.SFGreaterthan: ImageVector
    get() {
        if (_sFGreaterthan != null) {
            return _sFGreaterthan!!
        }
        _sFGreaterthan = sfIcon(
            name = "Dualtone.SFGreaterthan",
            viewportWidth = 17.3926f,
            viewportHeight = 16.3184f
        ) {
            addSfPath("M0.908203 16.3184C1.2207 16.3184 1.42578 16.2207 1.65039 16.1133L15.9668 9.66797C16.5723 9.39453 17.0312 8.92578 17.0312 8.25195C17.0312 7.57812 16.5723 7.10938 15.957 6.82617L1.65039 0.195312C1.43555 0.0878906 1.24023 0.00976562 0.947266 0.00976562C0.390625 0.00976562 0 0.390625 0 0.957031C0 1.44531 0.244141 1.72852 0.693359 1.93359L14.4922 8.11523L14.4922 8.24219L0.693359 14.3848C0.244141 14.5801 0 14.873 0 15.3613C0 15.9473 0.380859 16.3184 0.908203 16.3184Z", fillAlpha = 0.85f)
        }
        return _sFGreaterthan!!
    }

private var _sFGreaterthan: ImageVector? = null
