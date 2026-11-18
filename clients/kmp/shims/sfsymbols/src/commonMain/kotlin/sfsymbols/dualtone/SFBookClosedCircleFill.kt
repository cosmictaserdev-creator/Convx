package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFBookClosedCircleFill (dualtone)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Dualtone.SFBookClosedCircleFill: ImageVector
    get() {
        if (_sFBookClosedCircleFill != null) {
            return _sFBookClosedCircleFill!!
        }
        _sFBookClosedCircleFill = sfIcon(
            name = "Dualtone.SFBookClosedCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.459C19.7266 25.459 25.4395 19.7461 25.4395 12.7344C25.4395 5.73242 19.7266 0.0195312 12.7148 0.0195312C5.71289 0.0195312 0 5.73242 0 12.7344C0 19.7461 5.71289 25.459 12.7148 25.459Z", fillAlpha = 0.2125f)
            addSfPath("M9.26758 19.6191C8.00781 19.6191 7.34375 18.9648 7.34375 17.6953L7.34375 7.85156C7.34375 6.52344 8.00781 5.85938 9.30664 5.85938L16.123 5.85938C17.4219 5.85938 18.0859 6.5332 18.0859 7.85156L18.0859 15.2246C18.0859 15.8203 17.8223 16.1914 17.5586 16.4746C16.7969 17.2559 16.9336 18.2715 17.7734 18.6621C17.9395 18.7305 18.0859 18.9258 18.0859 19.1309C18.0859 19.4043 17.8613 19.6191 17.5977 19.6191ZM9.31641 18.6328L16.3477 18.6328C16.0156 18.0762 15.9668 17.4023 16.2305 16.7383C16.1621 16.748 16.0938 16.7578 16.0156 16.7578L9.35547 16.7578C8.73047 16.7578 8.33008 17.1289 8.33008 17.6855C8.33008 18.291 8.68164 18.6328 9.31641 18.6328ZM9.10156 15.6738C9.32617 15.6738 9.49219 15.5078 9.49219 15.293L9.49219 7.35352C9.49219 7.14844 9.32617 6.97266 9.10156 6.97266C8.89648 6.97266 8.73047 7.13867 8.73047 7.35352L8.73047 15.293C8.73047 15.5176 8.89648 15.6738 9.10156 15.6738Z", fillAlpha = 0.85f)
        }
        return _sFBookClosedCircleFill!!
    }

private var _sFBookClosedCircleFill: ImageVector? = null
