package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFShippingboxFill (monochrome)
 * Viewport: 25.7324 x 27.2754
 */
public val SfSymbols.Monochrome.SFShippingboxFill: ImageVector
    get() {
        if (_sFShippingboxFill != null) {
            return _sFShippingboxFill!!
        }
        _sFShippingboxFill = sfIcon(
            name = "Monochrome.SFShippingboxFill",
            viewportWidth = 25.7324f,
            viewportHeight = 27.2754f
        ) {
            addSfPath("M13.4668 27.2754C13.5547 27.2559 13.6426 27.207 13.7305 27.1582L23.7109 21.5332C24.8145 20.9082 25.3711 20.2637 25.3711 18.6328L25.3711 8.67188C25.3711 8.32031 25.3418 8.04688 25.2734 7.80273L13.4668 14.4824ZM11.9043 27.2754L11.9043 14.4824L0.0976562 7.80273C0.0292969 8.04688 0 8.32031 0 8.67188L0 18.6328C0 20.2637 0.556641 20.9082 1.66016 21.5332L11.6406 27.1582C11.7285 27.207 11.8164 27.2559 11.9043 27.2754ZM12.6855 13.1152L18.1348 10.0488L6.16211 3.29102L1.45508 5.95703C1.18164 6.10352 0.976562 6.25 0.810547 6.41602ZM19.7266 9.15039L24.5605 6.41602C24.3945 6.25 24.1992 6.10352 23.916 5.95703L14.7461 0.751953C14.043 0.351562 13.3594 0.146484 12.6855 0.146484C12.0117 0.146484 11.3281 0.351562 10.625 0.751953L7.73438 2.39258Z", fillAlpha = 0.85f)
        }
        return _sFShippingboxFill!!
    }

private var _sFShippingboxFill: ImageVector? = null
