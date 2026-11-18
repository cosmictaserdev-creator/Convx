package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFLampCeilingFill (dualtone)
 * Viewport: 23.6523 x 28.6621
 */
public val SfSymbols.Dualtone.SFLampCeilingFill: ImageVector
    get() {
        if (_sFLampCeilingFill != null) {
            return _sFLampCeilingFill!!
        }
        _sFLampCeilingFill = sfIcon(
            name = "Dualtone.SFLampCeilingFill",
            viewportWidth = 23.6523f,
            viewportHeight = 28.6621f
        ) {
            addSfPath("M11.6504 6.25977C14.4629 6.25977 16.8652 4.59961 17.2363 2.28516C17.3633 1.47461 16.9434 0.859375 16.1328 0.625C13.7207-0.0585938 9.57031-0.0585938 7.1582 0.625C6.34766 0.859375 5.9375 1.47461 6.06445 2.28516C6.43555 4.59961 8.83789 6.25977 11.6504 6.25977ZM10.7812 12.9785L12.5098 12.9785L12.5098 5.625L10.7812 5.625ZM11.6504 17.9004C16.2207 17.9004 20.0586 18.75 22.3828 20.2441C21.25 18.0273 19.1406 15.8887 16.6797 14.6875C15.9961 12.3145 13.9453 10.8008 11.6504 10.8008C9.3457 10.8008 7.29492 12.3145 6.61133 14.6875C4.15039 15.8887 2.04102 18.0273 0.908203 20.2441C3.23242 18.75 7.07031 17.9004 11.6504 17.9004ZM11.6504 28.6621C18.6328 28.6621 23.291 26.6895 23.291 23.7793C23.291 20.8984 17.5195 19.3652 11.6504 19.3652C5.78125 19.3652 0 20.8496 0 23.7793C0 26.6895 4.6582 28.6621 11.6504 28.6621ZM11.6504 24.5801C9.24805 24.5801 7.20703 23.2227 7.07031 21.4648C8.4375 21.2305 9.99023 21.0938 11.6504 21.0938C13.2812 21.0938 14.8438 21.2402 16.2207 21.4746C16.0742 23.2129 14.0625 24.5801 11.6504 24.5801Z", fillAlpha = 0.85f)
        }
        return _sFLampCeilingFill!!
    }

private var _sFLampCeilingFill: ImageVector? = null
