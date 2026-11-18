package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SF9AltCircleFill (monochrome)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Monochrome.SF9AltCircleFill: ImageVector
    get() {
        if (_sF9AltCircleFill != null) {
            return _sF9AltCircleFill!!
        }
        _sF9AltCircleFill = sfIcon(
            name = "Monochrome.SF9AltCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M25.4395 12.7246C25.4395 19.7266 19.7266 25.4395 12.7148 25.4395C5.71289 25.4395 0 19.7266 0 12.7246C0 5.71289 5.71289 0 12.7148 0C19.7266 0 25.4395 5.71289 25.4395 12.7246ZM8.37891 10.6641C8.37891 12.8125 10.0098 14.375 12.2461 14.375C13.125 14.375 14.043 14.0723 14.5117 13.5938L14.668 13.6035C14.541 13.8184 14.3457 14.0918 14.0527 14.4531C13.3887 15.3027 12.5488 16.3281 11.6309 17.4707C11.4453 17.6953 11.3477 17.9004 11.3477 18.1152C11.3477 18.457 11.6309 18.7793 12.0898 18.7793C12.5293 18.7793 12.7148 18.5059 13.0371 18.0957C13.7207 17.2461 14.7852 15.8789 15.4199 15.0293C16.6211 13.418 17.1875 12.1289 17.1875 10.752C17.1875 8.31055 15.3906 6.64062 12.7832 6.64062C10.1758 6.64062 8.37891 8.30078 8.37891 10.6641ZM15.6055 10.6152C15.6055 12.0605 14.4043 13.0957 12.793 13.0957C11.1621 13.0957 9.96094 12.0508 9.96094 10.5957C9.96094 9.15039 11.1621 8.07617 12.7734 8.07617C14.375 8.07617 15.6055 9.16016 15.6055 10.6152Z", fillAlpha = 0.85f)
        }
        return _sF9AltCircleFill!!
    }

private var _sF9AltCircleFill: ImageVector? = null
