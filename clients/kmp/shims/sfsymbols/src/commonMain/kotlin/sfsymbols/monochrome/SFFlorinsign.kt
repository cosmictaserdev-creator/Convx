package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFFlorinsign (monochrome)
 * Viewport: 18.9551 x 22.3926
 */
public val SfSymbols.Monochrome.SFFlorinsign: ImageVector
    get() {
        if (_sFFlorinsign != null) {
            return _sFFlorinsign!!
        }
        _sFFlorinsign = sfIcon(
            name = "Monochrome.SFFlorinsign",
            viewportWidth = 18.9551f,
            viewportHeight = 22.3926f
        ) {
            addSfPath("M3.29102 22.3828C7.07031 22.3828 8.81836 20.2734 9.45312 16.6309L10.3906 11.3574L16.1719 11.3574C16.6602 11.3574 17.0215 11.0254 17.0215 10.5566C17.0215 10.0684 16.6602 9.7168 16.1719 9.7168L10.6641 9.7168L11.3477 5.84961C11.7969 3.33984 12.9395 1.98242 15.498 1.98242C16.582 1.98242 17.0996 2.24609 17.7539 2.24609C18.2617 2.24609 18.5938 1.94336 18.5938 1.43555C18.5938 0.878906 18.0859 0.517578 17.5781 0.341797C16.875 0.078125 15.9766 0 15.3027 0C11.6309 0 9.69727 2.03125 9.07227 5.6543L8.34961 9.7168L2.86133 9.7168C2.37305 9.7168 2.01172 10.0684 2.01172 10.5566C2.01172 11.0254 2.37305 11.3574 2.86133 11.3574L8.07617 11.3574L7.17773 16.3672C6.71875 18.9258 5.69336 20.3711 3.07617 20.3711C2.24609 20.3711 1.37695 20.127 0.878906 20.127C0.361328 20.127 0 20.4297 0 20.9375C0 21.4648 0.322266 21.7871 0.849609 22.002C1.60156 22.2852 2.5 22.3828 3.29102 22.3828Z", fillAlpha = 0.85f)
        }
        return _sFFlorinsign!!
    }

private var _sFFlorinsign: ImageVector? = null
