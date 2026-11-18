package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFBackpackFill (monochrome)
 * Viewport: 20.6061 x 32.1582
 */
public val SfSymbols.Monochrome.SFBackpackFill: ImageVector
    get() {
        if (_sFBackpackFill != null) {
            return _sFBackpackFill!!
        }
        _sFBackpackFill = sfIcon(
            name = "Monochrome.SFBackpackFill",
            viewportWidth = 20.6061f,
            viewportHeight = 32.1582f
        ) {
            addSfPath("M6.32843 30.8496L13.9163 30.8496C18.1839 30.8496 20.2737 28.7109 20.2444 24.3848C20.1956 19.3848 20.1663 15.752 20.0394 13.0371C19.6878 5.07812 17.4808 2.77344 10.1175 2.77344C2.76398 2.77344 0.556944 5.07812 0.205382 13.0371C0.0784288 15.752 0.0491319 19.3848 0.00030375 24.3848C-0.0289931 28.7109 2.06085 30.8496 6.32843 30.8496ZM6.03546 25.9375C4.98077 25.9375 4.29718 25.2441 4.29718 24.1895L4.29718 17.9297C4.29718 16.875 4.98077 16.1816 6.03546 16.1816L14.2093 16.1816C15.264 16.1816 15.9476 16.875 15.9476 17.9297L15.9476 24.1895C15.9476 25.2441 15.264 25.9375 14.2093 25.9375ZM4.29718 20.8203L15.9476 20.8203L15.9476 19.502L4.29718 19.502ZM10.1273 0C7.90069 0 6.28937 1.5332 6.28937 3.60352L7.82257 3.60352C7.82257 2.40234 8.78937 1.5332 10.1273 1.5332C11.4651 1.5332 12.4222 2.40234 12.4222 3.60352L13.9554 3.60352C13.9554 1.5332 12.3441 0 10.1273 0Z", fillAlpha = 0.85f)
        }
        return _sFBackpackFill!!
    }

private var _sFBackpackFill: ImageVector? = null
