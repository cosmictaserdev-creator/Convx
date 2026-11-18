package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFForwardFrame (dualtone)
 * Viewport: 27.0801 x 18.9746
 */
public val SfSymbols.Dualtone.SFForwardFrame: ImageVector
    get() {
        if (_sFForwardFrame != null) {
            return _sFForwardFrame!!
        }
        _sFForwardFrame = sfIcon(
            name = "Dualtone.SFForwardFrame",
            viewportWidth = 27.0801f,
            viewportHeight = 18.9746f
        ) {
            addSfPath("M5.36133 18.9551C5.86914 18.9551 6.2793 18.5645 6.2793 18.0566L6.2793 0.898438C6.2793 0.390625 5.86914 0 5.36133 0C4.84375 0 4.44336 0.390625 4.44336 0.898438L4.44336 18.0566C4.44336 18.5645 4.84375 18.9551 5.36133 18.9551ZM10.3125 17.3633C10.3125 18.457 10.9473 18.9551 11.6992 18.9551C12.0215 18.9551 12.3633 18.8574 12.6855 18.6719L25.918 10.9082C26.748 10.4297 27.0801 10.0586 27.0801 9.48242C27.0801 8.90625 26.748 8.52539 25.918 8.04688L12.6855 0.283203C12.3633 0.0976562 12.0215 0 11.6992 0C10.9473 0 10.3125 0.507812 10.3125 1.60156ZM12.041 16.582L12.041 2.37305C12.041 2.20703 12.1484 2.09961 12.2852 2.09961C12.3438 2.09961 12.4023 2.11914 12.4609 2.14844L24.5703 9.27734C24.668 9.33594 24.707 9.4043 24.707 9.48242C24.707 9.55078 24.668 9.61914 24.5703 9.67773L12.4609 16.8066C12.4023 16.8457 12.3438 16.8555 12.2852 16.8555C12.1484 16.8555 12.041 16.748 12.041 16.582Z", fillAlpha = 0.85f)
        }
        return _sFForwardFrame!!
    }

private var _sFForwardFrame: ImageVector? = null
