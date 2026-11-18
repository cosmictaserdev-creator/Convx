package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFSpeakerFill (monochrome)
 * Viewport: 17.8711 x 21.9434
 */
public val SfSymbols.Monochrome.SFSpeakerFill: ImageVector
    get() {
        if (_sFSpeakerFill != null) {
            return _sFSpeakerFill!!
        }
        _sFSpeakerFill = sfIcon(
            name = "Monochrome.SFSpeakerFill",
            viewportWidth = 17.8711f,
            viewportHeight = 21.9434f
        ) {
            addSfPath("M13.4668 21.9434C14.2285 21.9434 14.7461 21.3867 14.7461 20.6445L14.7461 1.35742C14.7461 0.615234 14.2285 0.00976562 13.4473 0.00976562C12.9102 0.00976562 12.5391 0.244141 11.9824 0.771484L6.52344 5.94727C6.43555 6.03516 6.31836 6.07422 6.17188 6.07422L2.4707 6.07422C0.908203 6.07422 0 6.98242 0 8.64258L0 13.3691C0 15.0195 0.908203 15.9277 2.4707 15.9277L6.17188 15.9277C6.31836 15.9277 6.43555 15.9766 6.52344 16.0645L11.9824 21.2305C12.4902 21.709 12.9297 21.9434 13.4668 21.9434Z", fillAlpha = 0.85f)
        }
        return _sFSpeakerFill!!
    }

private var _sFSpeakerFill: ImageVector? = null
