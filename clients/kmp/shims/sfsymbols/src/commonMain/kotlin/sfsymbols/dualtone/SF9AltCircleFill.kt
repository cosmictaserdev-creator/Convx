package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SF9AltCircleFill (dualtone)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Dualtone.SF9AltCircleFill: ImageVector
    get() {
        if (_sF9AltCircleFill != null) {
            return _sF9AltCircleFill!!
        }
        _sF9AltCircleFill = sfIcon(
            name = "Dualtone.SF9AltCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.4395C19.7266 25.4395 25.4395 19.7266 25.4395 12.7246C25.4395 5.71289 19.7266 0 12.7148 0C5.71289 0 0 5.71289 0 12.7246C0 19.7266 5.71289 25.4395 12.7148 25.4395Z", fillAlpha = 0.2125f)
            addSfPath("M8.37891 10.6641C8.37891 8.30078 10.1758 6.64062 12.7832 6.64062C15.3906 6.64062 17.1875 8.31055 17.1875 10.752C17.1875 12.1289 16.6211 13.418 15.4199 15.0293C14.7852 15.8789 13.7207 17.2461 13.0371 18.0957C12.7148 18.5059 12.5293 18.7793 12.0898 18.7793C11.6309 18.7793 11.3477 18.457 11.3477 18.1152C11.3477 17.9004 11.4453 17.6953 11.6309 17.4707C12.5488 16.3281 13.3887 15.3027 14.0527 14.4531C14.3457 14.0918 14.541 13.8184 14.668 13.6035L14.5117 13.5938C14.043 14.0723 13.125 14.375 12.2461 14.375C10.0098 14.375 8.37891 12.8125 8.37891 10.6641ZM9.96094 10.5957C9.96094 12.0508 11.1621 13.0957 12.793 13.0957C14.4043 13.0957 15.6055 12.0605 15.6055 10.6152C15.6055 9.16016 14.375 8.07617 12.7734 8.07617C11.1621 8.07617 9.96094 9.15039 9.96094 10.5957Z", fillAlpha = 0.85f)
        }
        return _sF9AltCircleFill!!
    }

private var _sF9AltCircleFill: ImageVector? = null
