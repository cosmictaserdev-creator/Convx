package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFTrapezoidAndLineHorizontal (monochrome)
 * Viewport: 30.4102 x 21.9046
 */
public val SfSymbols.Monochrome.SFTrapezoidAndLineHorizontal: ImageVector
    get() {
        if (_sFTrapezoidAndLineHorizontal != null) {
            return _sFTrapezoidAndLineHorizontal!!
        }
        _sFTrapezoidAndLineHorizontal = sfIcon(
            name = "Monochrome.SFTrapezoidAndLineHorizontal",
            viewportWidth = 30.4102f,
            viewportHeight = 21.9046f
        ) {
            addSfPath("M4.51172 11.6994L0.859375 11.6994C0.322266 11.6994 0 11.3771 0 10.8888C0 10.3712 0.322266 10.0587 0.859375 10.0587L4.51172 10.0587ZM23.9062 11.6994L6.14258 11.6994L6.14258 10.0587L23.9062 10.0587ZM30.0488 10.8888C30.0488 11.3771 29.7266 11.6994 29.1895 11.6994L25.5371 11.6994L25.5371 10.0587L29.1895 10.0587C29.7266 10.0587 30.0488 10.3712 30.0488 10.8888Z", fillAlpha = 0.85f)
            addSfPath("M8.0957 19.2775L21.9531 21.6896C24.1895 22.0802 25.5371 20.6154 25.5371 18.1349L25.5371 3.62318C25.5371 1.04506 24.2676-0.331895 21.9531 0.0684952L8.0957 2.4806C5.78125 2.89076 4.51172 4.01381 4.51172 6.03529L4.51172 15.7228C4.51172 17.7345 5.78125 18.8673 8.0957 19.2775ZM8.03711 17.6564C6.79688 17.4513 6.14258 16.8654 6.14258 15.7423L6.14258 6.01576C6.14258 4.89271 6.79688 4.30678 8.03711 4.1017L22.0117 1.68959C23.252 1.48451 23.9062 2.2267 23.9062 3.60365L23.9062 18.1544C23.9062 19.4923 23.2227 20.2736 22.0117 20.0685Z", fillAlpha = 0.85f)
        }
        return _sFTrapezoidAndLineHorizontal!!
    }

private var _sFTrapezoidAndLineHorizontal: ImageVector? = null
