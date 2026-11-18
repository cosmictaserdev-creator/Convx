package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFArrowLeftCircleFill (dualtone)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Dualtone.SFArrowLeftCircleFill: ImageVector
    get() {
        if (_sFArrowLeftCircleFill != null) {
            return _sFArrowLeftCircleFill!!
        }
        _sFArrowLeftCircleFill = sfIcon(
            name = "Dualtone.SFArrowLeftCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.4395C19.7266 25.4395 25.4395 19.7266 25.4395 12.7246C25.4395 5.71289 19.7266 0 12.7148 0C5.71289 0 0 5.71289 0 12.7246C0 19.7266 5.71289 25.4395 12.7148 25.4395Z", fillAlpha = 0.2125f)
            addSfPath("M18.3496 13.5938C18.8281 13.5938 19.2188 13.1934 19.2188 12.7148C19.2188 12.2266 18.8281 11.8262 18.3496 11.8262L10.2734 11.8359L8.21289 11.9043C7.77344 11.9238 7.41211 12.2754 7.41211 12.7148C7.41211 13.1445 7.77344 13.5059 8.21289 13.5254L10.2734 13.5938ZM11.3379 17.9004C11.7773 17.9004 12.1094 17.5684 12.1094 17.1094C12.1094 16.8848 12.0215 16.6406 11.8457 16.4941L9.61914 14.3652L7.89062 12.7148L7.89062 12.7148L9.61914 11.0645L11.8457 8.93555C12.0215 8.78906 12.1094 8.55469 12.1094 8.33008C12.1094 7.88086 11.7773 7.5293 11.3379 7.5293C11.0938 7.5293 10.9082 7.61719 10.752 7.79297L6.58203 12.0703C6.35742 12.2949 6.28906 12.4805 6.28906 12.7148C6.28906 12.9395 6.35742 13.125 6.58203 13.3496L10.752 17.6367C10.9082 17.8125 11.0938 17.9004 11.3379 17.9004Z", fillAlpha = 0.85f)
        }
        return _sFArrowLeftCircleFill!!
    }

private var _sFArrowLeftCircleFill: ImageVector? = null
