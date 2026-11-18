package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFXmarkDiamondFill (monochrome)
 * Viewport: 28.6086 x 28.2642
 */
public val SfSymbols.Monochrome.SFXmarkDiamondFill: ImageVector
    get() {
        if (_sFXmarkDiamondFill != null) {
            return _sFXmarkDiamondFill!!
        }
        _sFXmarkDiamondFill = sfIcon(
            name = "Monochrome.SFXmarkDiamondFill",
            viewportWidth = 28.6086f,
            viewportHeight = 28.2642f
        ) {
            addSfPath("M16.8189 1.35378L26.9068 11.4417C28.6939 13.2288 28.6939 15.0257 26.9264 16.7932L16.7994 26.93C15.0318 28.6975 13.235 28.6878 11.4478 26.9007L1.35995 16.8128C-0.436922 15.0257-0.456453 13.2385 1.32089 11.4612L11.4674 1.33425C13.2447-0.452858 15.0318-0.443093 16.8189 1.35378ZM17.9713 9.05886L14.1313 12.8821L10.2955 9.05886C10.1197 8.88308 9.90488 8.79519 9.6705 8.79519C9.16269 8.79519 8.7623 9.17605 8.7623 9.66433C8.7623 9.91824 8.85995 10.1428 9.03573 10.3186L12.8625 14.1453L9.03573 17.9553C8.85995 18.1409 8.7623 18.3557 8.7623 18.5999C8.7623 19.0979 9.16269 19.4983 9.6705 19.4983C9.92441 19.4983 10.1393 19.4007 10.315 19.2249L14.1285 15.4114L17.942 19.2249C18.1178 19.4007 18.3326 19.4983 18.5963 19.4983C19.0943 19.4983 19.4947 19.0979 19.4947 18.5999C19.4947 18.3557 19.3971 18.1409 19.2213 17.9553L15.3966 14.1433L19.2213 10.3186C19.3971 10.1428 19.4947 9.91824 19.4947 9.66433C19.4947 9.17605 19.0943 8.79519 18.5963 8.79519C18.3521 8.79519 18.1373 8.88308 17.9713 9.05886Z", fillAlpha = 0.85f)
        }
        return _sFXmarkDiamondFill!!
    }

private var _sFXmarkDiamondFill: ImageVector? = null
