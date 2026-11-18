package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFBookClosedCircleFill (monochrome)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Monochrome.SFBookClosedCircleFill: ImageVector
    get() {
        if (_sFBookClosedCircleFill != null) {
            return _sFBookClosedCircleFill!!
        }
        _sFBookClosedCircleFill = sfIcon(
            name = "Monochrome.SFBookClosedCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M25.4395 12.7344C25.4395 19.7461 19.7266 25.459 12.7148 25.459C5.71289 25.459 0 19.7461 0 12.7344C0 5.73242 5.71289 0.0195312 12.7148 0.0195312C19.7266 0.0195312 25.4395 5.73242 25.4395 12.7344ZM9.30664 5.85938C8.00781 5.85938 7.34375 6.52344 7.34375 7.85156L7.34375 17.6953C7.34375 18.9648 8.00781 19.6191 9.26758 19.6191L17.5977 19.6191C17.8613 19.6191 18.0859 19.4043 18.0859 19.1309C18.0859 18.9258 17.9395 18.7305 17.7734 18.6621C16.9336 18.2715 16.7969 17.2559 17.5586 16.4746C17.8223 16.1914 18.0859 15.8203 18.0859 15.2246L18.0859 7.85156C18.0859 6.5332 17.4219 5.85938 16.123 5.85938ZM16.3477 18.6328L9.31641 18.6328C8.68164 18.6328 8.33008 18.291 8.33008 17.6855C8.33008 17.1289 8.73047 16.7578 9.35547 16.7578L16.0156 16.7578C16.0938 16.7578 16.1621 16.748 16.2305 16.7383C15.9668 17.4023 16.0156 18.0762 16.3477 18.6328ZM9.49219 7.35352L9.49219 15.293C9.49219 15.5078 9.32617 15.6738 9.10156 15.6738C8.89648 15.6738 8.73047 15.5176 8.73047 15.293L8.73047 7.35352C8.73047 7.13867 8.89648 6.97266 9.10156 6.97266C9.32617 6.97266 9.49219 7.14844 9.49219 7.35352Z", fillAlpha = 0.85f)
        }
        return _sFBookClosedCircleFill!!
    }

private var _sFBookClosedCircleFill: ImageVector? = null
