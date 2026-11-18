package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFArrowForwardCircleFill (dualtone)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Dualtone.SFArrowForwardCircleFill: ImageVector
    get() {
        if (_sFArrowForwardCircleFill != null) {
            return _sFArrowForwardCircleFill!!
        }
        _sFArrowForwardCircleFill = sfIcon(
            name = "Dualtone.SFArrowForwardCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.4395C19.7266 25.4395 25.4395 19.7266 25.4395 12.7246C25.4395 5.71289 19.7266 0 12.7148 0C5.71289 0 0 5.71289 0 12.7246C0 19.7266 5.71289 25.4395 12.7148 25.4395Z", fillAlpha = 0.2125f)
            addSfPath("M7.1582 13.5938L15.2344 13.5938L17.2949 13.5254C17.7344 13.5059 18.0957 13.1445 18.0957 12.7148C18.0957 12.2754 17.7344 11.9238 17.2949 11.9043L15.2344 11.8359L7.1582 11.8262C6.67969 11.8262 6.28906 12.2266 6.28906 12.7148C6.28906 13.1934 6.67969 13.5938 7.1582 13.5938ZM14.1699 17.9004C14.4141 17.9004 14.5898 17.8125 14.7559 17.6367L18.9258 13.3496C19.1406 13.125 19.2188 12.9395 19.2188 12.7148C19.2188 12.4805 19.1406 12.2949 18.9258 12.0703L14.7559 7.79297C14.5898 7.61719 14.4141 7.5293 14.1699 7.5293C13.7207 7.5293 13.3887 7.88086 13.3887 8.33008C13.3887 8.55469 13.4766 8.78906 13.6621 8.93555L15.8789 11.0645L17.6172 12.7148L17.6172 12.7148L15.8789 14.3652L13.6621 16.4941C13.4766 16.6406 13.3887 16.8848 13.3887 17.1094C13.3887 17.5684 13.7207 17.9004 14.1699 17.9004Z", fillAlpha = 0.85f)
        }
        return _sFArrowForwardCircleFill!!
    }

private var _sFArrowForwardCircleFill: ImageVector? = null
