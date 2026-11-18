package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFMoonHazeFill (dualtone)
 * Viewport: 33.4961 x 29.1113
 */
public val SfSymbols.Dualtone.SFMoonHazeFill: ImageVector
    get() {
        if (_sFMoonHazeFill != null) {
            return _sFMoonHazeFill!!
        }
        _sFMoonHazeFill = sfIcon(
            name = "Dualtone.SFMoonHazeFill",
            viewportWidth = 33.4961f,
            viewportHeight = 29.1113f
        ) {
            addSfPath("M13.877 2.33398C13.5059 3.24219 13.2617 4.39453 13.2617 5.51758C13.2617 11.1523 16.4648 14.3652 22.1094 14.3652C23.2715 14.3652 24.4434 14.1016 25.0977 13.8574C25.6543 13.6816 26.0059 14.0723 25.7227 14.6289C25.3911 15.3641 24.9596 16.0587 24.4382 16.6895L9.45109 16.6895C8.07334 15.0154 7.24609 12.872 7.24609 10.5371C7.24609 6.28906 9.99023 2.94922 13.1152 1.62109C13.6914 1.36719 14.1211 1.72852 13.877 2.33398Z", fillAlpha = 0.425f)
            addSfPath("M2.72461 24.2773L28.5059 24.2773C28.8965 24.2773 29.209 23.9746 29.209 23.584C29.209 23.1836 28.8965 22.8906 28.5059 22.8906L2.72461 22.8906C2.33398 22.8906 2.02148 23.1836 2.02148 23.584C2.02148 23.9746 2.33398 24.2773 2.72461 24.2773ZM4.61914 29.1113L30.4102 29.1113C30.8008 29.1113 31.1035 28.8184 31.1035 28.4277C31.1035 28.0273 30.8008 27.7344 30.4102 27.7344L4.61914 27.7344C4.22852 27.7344 3.92578 28.0273 3.92578 28.4277C3.92578 28.8184 4.22852 29.1113 4.61914 29.1113ZM4.61914 19.4336L30.4102 19.4336C30.8008 19.4336 31.1035 19.1309 31.1035 18.7402C31.1035 18.3496 30.8008 18.0469 30.4102 18.0469L4.61914 18.0469C4.22852 18.0469 3.92578 18.3496 3.92578 18.7402C3.92578 19.1309 4.22852 19.4336 4.61914 19.4336Z", fillAlpha = 0.85f)
        }
        return _sFMoonHazeFill!!
    }

private var _sFMoonHazeFill: ImageVector? = null
