package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFSuitSpadeFill (dualtone)
 * Viewport: 23.7695 x 25.0879
 */
public val SfSymbols.Dualtone.SFSuitSpadeFill: ImageVector
    get() {
        if (_sFSuitSpadeFill != null) {
            return _sFSuitSpadeFill!!
        }
        _sFSuitSpadeFill = sfIcon(
            name = "Dualtone.SFSuitSpadeFill",
            viewportWidth = 23.7695f,
            viewportHeight = 25.0879f
        ) {
            addSfPath("M0 14.7949C0 17.793 2.25586 20.0586 5.27344 20.0586C6.98242 20.0586 8.67188 19.4531 9.41406 18.0078L9.54102 18.0078C9.48242 19.707 7.72461 21.1035 6.86523 21.9727C5.99609 22.8711 6.52344 24.1406 7.66602 24.1406L15.7422 24.1406C16.8848 24.1406 17.4121 22.8711 16.543 21.9727C15.6836 21.1035 13.9258 19.707 13.8574 18.0078L13.9941 18.0078C14.7363 19.4531 16.4258 20.0586 18.1348 20.0586C21.1523 20.0586 23.4082 17.793 23.4082 14.7949C23.4082 9.18945 15.8398 6.9043 12.8516 0.830078C12.6172 0.351562 12.2852 0 11.6992 0C11.1133 0 10.7812 0.351562 10.5469 0.830078C7.56836 6.9043 0 9.18945 0 14.7949Z", fillAlpha = 0.85f)
        }
        return _sFSuitSpadeFill!!
    }

private var _sFSuitSpadeFill: ImageVector? = null
