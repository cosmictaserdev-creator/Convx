package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFBackwardFrame (dualtone)
 * Viewport: 27.8027 x 18.9746
 */
public val SfSymbols.Dualtone.SFBackwardFrame: ImageVector
    get() {
        if (_sFBackwardFrame != null) {
            return _sFBackwardFrame!!
        }
        _sFBackwardFrame = sfIcon(
            name = "Dualtone.SFBackwardFrame",
            viewportWidth = 27.8027f,
            viewportHeight = 18.9746f
        ) {
            addSfPath("M21.7188 18.9551C22.2363 18.9551 22.6367 18.5645 22.6367 18.0566L22.6367 0.898438C22.6367 0.390625 22.2363 0 21.7188 0C21.2109 0 20.8105 0.390625 20.8105 0.898438L20.8105 18.0566C20.8105 18.5645 21.2109 18.9551 21.7188 18.9551ZM16.7773 17.3633L16.7773 1.60156C16.7773 0.507812 16.1328 0 15.3906 0C15.0586 0 14.7266 0.0976562 14.3945 0.283203L1.16211 8.04688C0.332031 8.52539 0 8.90625 0 9.48242C0 10.0586 0.332031 10.4297 1.16211 10.9082L14.3945 18.6719C14.7266 18.8574 15.0586 18.9551 15.3906 18.9551C16.1328 18.9551 16.7773 18.457 16.7773 17.3633ZM15.0391 16.582C15.0391 16.748 14.9414 16.8555 14.8047 16.8555C14.7363 16.8555 14.6875 16.8457 14.6289 16.8066L2.51953 9.67773C2.41211 9.61914 2.37305 9.55078 2.37305 9.48242C2.37305 9.4043 2.41211 9.33594 2.51953 9.27734L14.6289 2.14844C14.6875 2.11914 14.7363 2.09961 14.8047 2.09961C14.9414 2.09961 15.0391 2.20703 15.0391 2.37305Z", fillAlpha = 0.85f)
        }
        return _sFBackwardFrame!!
    }

private var _sFBackwardFrame: ImageVector? = null
