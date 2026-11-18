package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFWaveformPathEcg (monochrome)
 * Viewport: 25.2832 x 26.7944
 */
public val SfSymbols.Monochrome.SFWaveformPathEcg: ImageVector
    get() {
        if (_sFWaveformPathEcg != null) {
            return _sFWaveformPathEcg!!
        }
        _sFWaveformPathEcg = sfIcon(
            name = "Monochrome.SFWaveformPathEcg",
            viewportWidth = 25.2832f,
            viewportHeight = 26.7944f
        ) {
            addSfPath("M0.771484 15.7068L6.34766 15.7068C6.8457 15.7068 7.1582 15.4822 7.25586 15.033L9.3457 5.55054L9.21875 5.55054L12.6953 26.2048C12.832 26.9959 14.043 26.9861 14.1992 26.2048L17.5684 10.658L17.4512 10.6482L18.3887 14.9451C18.4961 15.4627 18.7891 15.7068 19.3164 15.7068L24.1504 15.7068C24.5801 15.7068 24.9219 15.3748 24.9219 14.9548C24.9219 14.5154 24.5898 14.1736 24.1504 14.1736L19.4629 14.1736L19.7168 14.3298L18.1836 7.70875C18.0078 6.94703 16.9043 6.95679 16.7188 7.73804L13.3496 22.6892L13.5059 22.6892L10.0195 1.83961C9.89258 1.06812 8.76953 1.04859 8.59375 1.83961L5.85938 14.3396L6.01562 14.1736L0.771484 14.1736C0.341797 14.1736 0 14.5252 0 14.9548C0 15.3748 0.341797 15.7068 0.771484 15.7068Z", fillAlpha = 0.85f)
        }
        return _sFWaveformPathEcg!!
    }

private var _sFWaveformPathEcg: ImageVector? = null
