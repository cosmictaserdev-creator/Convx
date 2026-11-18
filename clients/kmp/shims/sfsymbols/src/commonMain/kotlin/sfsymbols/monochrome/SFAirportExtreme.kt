package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFAirportExtreme (monochrome)
 * Viewport: 30.9863 x 14.5996
 */
public val SfSymbols.Monochrome.SFAirportExtreme: ImageVector
    get() {
        if (_sFAirportExtreme != null) {
            return _sFAirportExtreme!!
        }
        _sFAirportExtreme = sfIcon(
            name = "Monochrome.SFAirportExtreme",
            viewportWidth = 30.9863f,
            viewportHeight = 14.5996f
        ) {
            addSfPath("M24.6973 10.3906C25.5371 10.3711 26.2402 9.67773 26.2402 8.83789C26.2402 7.97852 25.5371 7.26562 24.6973 7.26562C23.8574 7.26562 23.1348 7.97852 23.1348 8.83789C23.1348 9.67773 23.8574 10.4102 24.6973 10.3906ZM3.79883 14.4824L26.8262 14.4824C29.3457 14.4824 30.625 13.2031 30.625 10.7227L30.625 3.75977C30.625 1.2793 29.3457 0 26.8262 0L3.79883 0C1.26953 0 0 1.26953 0 3.75977L0 10.7227C0 13.2227 1.26953 14.4824 3.79883 14.4824ZM3.83789 12.7539C2.4707 12.7539 1.72852 12.0312 1.72852 10.625L1.72852 3.85742C1.72852 2.45117 2.4707 1.72852 3.83789 1.72852L26.7871 1.72852C28.125 1.72852 28.8965 2.45117 28.8965 3.85742L28.8965 10.625C28.8965 12.0312 28.125 12.7539 26.7871 12.7539ZM29.6191 5.30273L29.6191 3.86719L1.01562 3.86719L1.01562 5.30273Z", fillAlpha = 0.85f)
        }
        return _sFAirportExtreme!!
    }

private var _sFAirportExtreme: ImageVector? = null
