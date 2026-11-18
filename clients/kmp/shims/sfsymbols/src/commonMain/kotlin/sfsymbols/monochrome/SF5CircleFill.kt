package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SF5CircleFill (monochrome)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Monochrome.SF5CircleFill: ImageVector
    get() {
        if (_sF5CircleFill != null) {
            return _sF5CircleFill!!
        }
        _sF5CircleFill = sfIcon(
            name = "Monochrome.SF5CircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M25.4395 12.7246C25.4395 19.7266 19.7266 25.4395 12.7148 25.4395C5.71289 25.4395 0 19.7266 0 12.7246C0 5.71289 5.71289 0 12.7148 0C19.7266 0 25.4395 5.71289 25.4395 12.7246ZM10.4395 6.89453C9.70703 6.89453 9.23828 7.31445 9.18945 7.96875L8.81836 12.4902C8.76953 13.1348 9.14062 13.5645 9.76562 13.5645C10.1758 13.5645 10.4297 13.4668 10.8594 13.125C11.5332 12.5 12.0801 12.2656 12.8223 12.2656C14.2773 12.2656 15.2539 13.2812 15.2539 14.7754C15.2539 16.2695 14.1406 17.3828 12.6465 17.3828C11.4941 17.3828 10.5176 16.8066 10.1172 15.8887C9.93164 15.5176 9.66797 15.332 9.32617 15.332C8.87695 15.332 8.56445 15.6445 8.56445 16.0742C8.56445 16.2695 8.61328 16.4453 8.69141 16.6211C9.16992 17.9395 10.6348 18.8086 12.6465 18.8086C15.2051 18.8086 16.9629 17.1875 16.9629 14.7461C16.9629 12.4902 15.3418 10.918 13.0273 10.918C11.9629 10.918 10.8887 11.3867 10.3906 12.0801L10.3027 12.0801L10.6152 8.30078L15.8301 8.30078C16.2207 8.30078 16.5137 8.00781 16.5137 7.60742C16.5137 7.1875 16.2305 6.89453 15.8301 6.89453Z", fillAlpha = 0.85f)
        }
        return _sF5CircleFill!!
    }

private var _sF5CircleFill: ImageVector? = null
