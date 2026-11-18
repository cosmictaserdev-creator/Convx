package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFSpeakerWave1Fill (monochrome)
 * Viewport: 22.4902 x 21.9434
 */
public val SfSymbols.Monochrome.SFSpeakerWave1Fill: ImageVector
    get() {
        if (_sFSpeakerWave1Fill != null) {
            return _sFSpeakerWave1Fill!!
        }
        _sFSpeakerWave1Fill = sfIcon(
            name = "Monochrome.SFSpeakerWave1Fill",
            viewportWidth = 22.4902f,
            viewportHeight = 21.9434f
        ) {
            addSfPath("M19.3945 16.2891C19.7656 16.543 20.2734 16.4551 20.5566 16.0449C21.5234 14.7754 22.1289 12.9297 22.1289 10.9668C22.1289 8.99414 21.5234 7.1582 20.5566 5.87891C20.2734 5.46875 19.7656 5.39062 19.3945 5.64453C18.9648 5.94727 18.8965 6.47461 19.2285 6.92383C20.0293 8.01758 20.459 9.41406 20.459 10.9668C20.459 12.5098 20.0195 13.8965 19.2285 15.0098C18.9062 15.459 18.9648 15.9766 19.3945 16.2891Z", fillAlpha = 0.85f)
            addSfPath("M13.4668 21.9434C14.2285 21.9434 14.7461 21.3867 14.7461 20.6445L14.7461 1.35742C14.7461 0.615234 14.2285 0.00976562 13.4473 0.00976562C12.9102 0.00976562 12.5391 0.244141 11.9824 0.771484L6.52344 5.94727C6.43555 6.03516 6.31836 6.07422 6.17188 6.07422L2.4707 6.07422C0.908203 6.07422 0 6.98242 0 8.64258L0 13.3691C0 15.0195 0.908203 15.9277 2.4707 15.9277L6.17188 15.9277C6.31836 15.9277 6.43555 15.9766 6.52344 16.0645L11.9824 21.2305C12.4902 21.709 12.9297 21.9434 13.4668 21.9434Z", fillAlpha = 0.85f)
        }
        return _sFSpeakerWave1Fill!!
    }

private var _sFSpeakerWave1Fill: ImageVector? = null
