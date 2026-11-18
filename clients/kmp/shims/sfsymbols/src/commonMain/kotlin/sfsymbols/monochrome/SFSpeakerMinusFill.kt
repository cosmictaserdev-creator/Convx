package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFSpeakerMinusFill (monochrome)
 * Viewport: 31.3086 x 21.9434
 */
public val SfSymbols.Monochrome.SFSpeakerMinusFill: ImageVector
    get() {
        if (_sFSpeakerMinusFill != null) {
            return _sFSpeakerMinusFill!!
        }
        _sFSpeakerMinusFill = sfIcon(
            name = "Monochrome.SFSpeakerMinusFill",
            viewportWidth = 31.3086f,
            viewportHeight = 21.9434f
        ) {
            addSfPath("M19.6191 11.875L29.9414 11.875C30.5273 11.875 30.9473 11.543 30.9473 10.9863C30.9473 10.4102 30.5566 10.0684 29.9414 10.0684L19.6191 10.0684C18.9941 10.0684 18.5938 10.4102 18.5938 10.9863C18.5938 11.543 19.0234 11.875 19.6191 11.875Z", fillAlpha = 0.85f)
            addSfPath("M13.4668 21.9434C14.2285 21.9434 14.7461 21.3867 14.7461 20.6445L14.7461 1.35742C14.7461 0.615234 14.2285 0.00976562 13.4473 0.00976562C12.9102 0.00976562 12.5391 0.244141 11.9824 0.771484L6.52344 5.94727C6.43555 6.03516 6.31836 6.07422 6.17188 6.07422L2.4707 6.07422C0.908203 6.07422 0 6.98242 0 8.64258L0 13.3691C0 15.0195 0.908203 15.9277 2.4707 15.9277L6.17188 15.9277C6.31836 15.9277 6.43555 15.9766 6.52344 16.0645L11.9824 21.2305C12.4902 21.709 12.9297 21.9434 13.4668 21.9434Z", fillAlpha = 0.85f)
        }
        return _sFSpeakerMinusFill!!
    }

private var _sFSpeakerMinusFill: ImageVector? = null
