package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SF5CircleFill (dualtone)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Dualtone.SF5CircleFill: ImageVector
    get() {
        if (_sF5CircleFill != null) {
            return _sF5CircleFill!!
        }
        _sF5CircleFill = sfIcon(
            name = "Dualtone.SF5CircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.4395C19.7266 25.4395 25.4395 19.7266 25.4395 12.7246C25.4395 5.71289 19.7266 0 12.7148 0C5.71289 0 0 5.71289 0 12.7246C0 19.7266 5.71289 25.4395 12.7148 25.4395Z", fillAlpha = 0.2125f)
            addSfPath("M12.6465 18.8086C10.6348 18.8086 9.16992 17.9395 8.69141 16.6211C8.61328 16.4453 8.56445 16.2695 8.56445 16.0742C8.56445 15.6445 8.87695 15.332 9.32617 15.332C9.66797 15.332 9.93164 15.5176 10.1172 15.8887C10.5176 16.8066 11.4941 17.3828 12.6465 17.3828C14.1406 17.3828 15.2539 16.2695 15.2539 14.7754C15.2539 13.2812 14.2773 12.2656 12.8223 12.2656C12.0801 12.2656 11.5332 12.5 10.8594 13.125C10.4297 13.4668 10.1758 13.5645 9.76562 13.5645C9.14062 13.5645 8.76953 13.1348 8.81836 12.4902L9.18945 7.96875C9.23828 7.31445 9.70703 6.89453 10.4395 6.89453L15.8301 6.89453C16.2305 6.89453 16.5137 7.1875 16.5137 7.60742C16.5137 8.00781 16.2207 8.30078 15.8301 8.30078L10.6152 8.30078L10.3027 12.0801L10.3906 12.0801C10.8887 11.3867 11.9629 10.918 13.0273 10.918C15.3418 10.918 16.9629 12.4902 16.9629 14.7461C16.9629 17.1875 15.2051 18.8086 12.6465 18.8086Z", fillAlpha = 0.85f)
        }
        return _sF5CircleFill!!
    }

private var _sF5CircleFill: ImageVector? = null
