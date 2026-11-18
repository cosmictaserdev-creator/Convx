package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFGCircleFill (dualtone)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Dualtone.SFGCircleFill: ImageVector
    get() {
        if (_sFGCircleFill != null) {
            return _sFGCircleFill!!
        }
        _sFGCircleFill = sfIcon(
            name = "Dualtone.SFGCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.4395C19.7266 25.4395 25.4395 19.7266 25.4395 12.7246C25.4395 5.71289 19.7266 0 12.7148 0C5.71289 0 0 5.71289 0 12.7246C0 19.7266 5.71289 25.4395 12.7148 25.4395Z", fillAlpha = 0.2125f)
            addSfPath("M12.8027 18.8574C9.44336 18.8574 7.29492 16.4258 7.29492 12.6367C7.29492 8.85742 9.44336 6.41602 12.7637 6.41602C15.2832 6.41602 16.9434 7.77344 17.4902 9.35547C17.5586 9.55078 17.5879 9.70703 17.5879 9.91211C17.5879 10.4004 17.2949 10.7031 16.8066 10.7031C16.4453 10.7031 16.2305 10.5273 16.0547 10.1172C15.5762 8.73047 14.3945 7.91992 12.793 7.91992C10.5957 7.91992 9.11133 9.82422 9.11133 12.6367C9.11133 15.459 10.6055 17.3535 12.8223 17.3535C14.873 17.3535 15.9766 16.2109 15.9766 14.4629L15.9766 13.6621L13.2812 13.6621C12.8711 13.6621 12.6074 13.3496 12.6074 12.998C12.6074 12.6074 12.8711 12.334 13.2812 12.334L16.8262 12.334C17.4121 12.334 17.7539 12.6953 17.7539 13.3008L17.7539 14.248C17.7539 16.9824 15.8984 18.8574 12.8027 18.8574Z", fillAlpha = 0.85f)
        }
        return _sFGCircleFill!!
    }

private var _sFGCircleFill: ImageVector? = null
