package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFPano (monochrome)
 * Viewport: 31.8457 x 21.0254
 */
public val SfSymbols.Monochrome.SFPano: ImageVector
    get() {
        if (_sFPano != null) {
            return _sFPano!!
        }
        _sFPano = sfIcon(
            name = "Monochrome.SFPano",
            viewportWidth = 31.8457f,
            viewportHeight = 21.0254f
        ) {
            addSfPath("M1.9043 21.0254C4.19922 21.0254 7.90039 18.3301 15.7422 18.3301C23.5449 18.3301 27.2949 21.0156 29.5703 21.0156C30.8008 21.0156 31.4844 20.2734 31.4844 18.9746L31.4844 2.07031C31.4844 0.771484 30.8008 0.0292969 29.5703 0.0292969C27.2949 0.0292969 23.5449 2.73438 15.7422 2.73438C7.92969 2.73438 4.18945 0.0292969 1.9043 0.0292969C0.683594 0.0292969 0 0.771484 0 2.07031L0 18.9844C0 20.2832 0.683594 21.0254 1.9043 21.0254ZM2.12891 19.2383C1.875 19.2383 1.72852 19.1016 1.72852 18.8184L1.72852 2.23633C1.72852 1.94336 1.875 1.81641 2.12891 1.81641C3.50586 1.81641 7.8418 4.375 15.7422 4.375C23.6328 4.375 28.1836 1.81641 29.3555 1.81641C29.6094 1.81641 29.7461 1.94336 29.7461 2.22656L29.7461 18.8184C29.7461 19.1016 29.6094 19.2383 29.3555 19.2383C27.9785 19.2383 23.6328 16.6895 15.7422 16.6895C7.80273 16.6895 3.30078 19.2383 2.12891 19.2383Z", fillAlpha = 0.85f)
        }
        return _sFPano!!
    }

private var _sFPano: ImageVector? = null
