package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFRectangle3GroupFill (dualtone)
 * Viewport: 34.8047 x 22.5098
 */
public val SfSymbols.Dualtone.SFRectangle3GroupFill: ImageVector
    get() {
        if (_sFRectangle3GroupFill != null) {
            return _sFRectangle3GroupFill!!
        }
        _sFRectangle3GroupFill = sfIcon(
            name = "Dualtone.SFRectangle3GroupFill",
            viewportWidth = 34.8047f,
            viewportHeight = 22.5098f
        ) {
            addSfPath("M5.41992 22.5098L16.1035 22.5098C17.6562 22.5098 18.4473 21.748 18.4473 20.1367L18.4473 15.8008C18.4473 14.1797 17.6562 13.418 16.1035 13.418L5.41992 13.418C3.86719 13.418 3.07617 14.1797 3.07617 15.8008L3.07617 20.1367C3.07617 21.748 3.86719 22.5098 5.41992 22.5098Z", fillAlpha = 0.85f)
            addSfPath("M23.2031 20.9473L32.0996 20.9473C33.6523 20.9473 34.4434 20.1855 34.4434 18.5742L34.4434 4.11133C34.4434 2.5 33.6523 1.72852 32.0996 1.72852L23.2031 1.72852C21.6504 1.72852 20.8594 2.5 20.8594 4.11133L20.8594 18.5742C20.8594 20.1855 21.6504 20.9473 23.2031 20.9473Z", fillAlpha = 0.85f)
            addSfPath("M2.33398 10.7227L14.5508 10.7227C16.0938 10.7227 16.8848 9.95117 16.8848 8.33984L16.8848 2.39258C16.8848 0.771484 16.0938 0.0195312 14.5508 0.0195312L2.33398 0.0195312C0.791016 0.0195312 0 0.771484 0 2.39258L0 8.33984C0 9.95117 0.791016 10.7227 2.33398 10.7227Z", fillAlpha = 0.85f)
        }
        return _sFRectangle3GroupFill!!
    }

private var _sFRectangle3GroupFill: ImageVector? = null
