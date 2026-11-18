package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFPanoFill (dualtone)
 * Viewport: 31.8457 x 21.0254
 */
public val SfSymbols.Dualtone.SFPanoFill: ImageVector
    get() {
        if (_sFPanoFill != null) {
            return _sFPanoFill!!
        }
        _sFPanoFill = sfIcon(
            name = "Dualtone.SFPanoFill",
            viewportWidth = 31.8457f,
            viewportHeight = 21.0254f
        ) {
            addSfPath("M1.9043 21.0254C4.19922 21.0254 7.90039 18.3301 15.7422 18.3301C23.5449 18.3301 27.2949 21.0156 29.5703 21.0156C30.8008 21.0156 31.4844 20.2734 31.4844 18.9746L31.4844 2.07031C31.4844 0.771484 30.8008 0.0292969 29.5703 0.0292969C27.2949 0.0292969 23.5449 2.73438 15.7422 2.73438C7.92969 2.73438 4.18945 0.0292969 1.9043 0.0292969C0.683594 0.0292969 0 0.771484 0 2.07031L0 18.9844C0 20.2832 0.683594 21.0254 1.9043 21.0254Z", fillAlpha = 0.85f)
        }
        return _sFPanoFill!!
    }

private var _sFPanoFill: ImageVector? = null
