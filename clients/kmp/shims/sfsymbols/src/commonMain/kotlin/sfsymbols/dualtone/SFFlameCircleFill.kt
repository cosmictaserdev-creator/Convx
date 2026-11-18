package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFFlameCircleFill (dualtone)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Dualtone.SFFlameCircleFill: ImageVector
    get() {
        if (_sFFlameCircleFill != null) {
            return _sFFlameCircleFill!!
        }
        _sFFlameCircleFill = sfIcon(
            name = "Dualtone.SFFlameCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.459C19.7266 25.459 25.4395 19.7461 25.4395 12.7344C25.4395 5.73242 19.7266 0.0195312 12.7148 0.0195312C5.71289 0.0195312 0 5.73242 0 12.7344C0 19.7461 5.71289 25.459 12.7148 25.459Z", fillAlpha = 0.2125f)
            addSfPath("M7.25586 15.8887C7.25586 13.3691 8.47656 12.4805 8.47656 10.8105C8.47656 10.4883 8.42773 10.0488 8.42773 9.87305C8.42773 9.59961 8.58398 9.4043 8.84766 9.4043C9.45312 9.4043 10.2246 9.94141 10.6152 10.752C10.7031 10.5566 10.7422 10.3613 10.7422 10.1367C10.7324 8.7793 9.87305 7.55859 9.0332 6.54297C8.89648 6.38672 8.83789 6.2207 8.83789 6.06445C8.83789 5.64453 9.18945 5.42969 9.80469 5.42969C13.584 5.42969 18.9355 8.06641 18.9355 14.375C18.9355 18.1445 16.4746 20.6641 12.7734 20.6641C9.43359 20.6641 7.25586 18.7598 7.25586 15.8887ZM10.7617 16.709C10.7617 17.8906 11.6309 18.6719 12.9297 18.6719C14.5801 18.6719 15.3711 17.4512 15.3711 16.0742C15.3711 14.7363 14.5898 13.2324 13.0664 12.5098C13.0078 12.4902 12.9688 12.5098 12.9785 12.6074C13.1055 13.7305 12.9395 14.7754 12.5781 15.2734C12.373 14.873 12.1777 14.502 11.8457 14.2383C11.7969 14.1895 11.7578 14.1992 11.7578 14.2871C11.6309 15.0586 10.7617 15.498 10.7617 16.709Z", fillAlpha = 0.85f)
        }
        return _sFFlameCircleFill!!
    }

private var _sFFlameCircleFill: ImageVector? = null
