package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFArrowDownCircleFill (dualtone)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Dualtone.SFArrowDownCircleFill: ImageVector
    get() {
        if (_sFArrowDownCircleFill != null) {
            return _sFArrowDownCircleFill!!
        }
        _sFArrowDownCircleFill = sfIcon(
            name = "Dualtone.SFArrowDownCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.4395C19.7266 25.4395 25.4395 19.7266 25.4395 12.7246C25.4395 5.71289 19.7266 0 12.7148 0C5.71289 0 0 5.71289 0 12.7246C0 19.7266 5.71289 25.4395 12.7148 25.4395Z", fillAlpha = 0.2125f)
            addSfPath("M12.7246 6.25977C12.2461 6.25977 11.8457 6.65039 11.8457 7.11914L11.8457 14.8242L11.9141 17.3633C11.9336 17.8613 12.3242 18.1445 12.7246 18.1445C13.125 18.1445 13.5254 17.8613 13.5352 17.3633L13.6133 14.8242L13.6133 7.11914C13.6133 6.65039 13.2129 6.25977 12.7246 6.25977ZM12.7246 19.1895C12.959 19.1895 13.1445 19.1113 13.3691 18.8867L17.6465 14.7266C17.8223 14.5605 17.9102 14.375 17.9102 14.1406C17.9102 13.6914 17.5586 13.3594 17.1094 13.3594C16.8848 13.3594 16.6504 13.4473 16.5039 13.6328L14.375 15.8496L12.7246 17.5879L12.7246 17.5879L11.0742 15.8496L8.94531 13.6328C8.79883 13.4473 8.54492 13.3594 8.33008 13.3594C7.87109 13.3594 7.53906 13.6914 7.53906 14.1406C7.53906 14.375 7.62695 14.5605 7.80273 14.7266L12.0898 18.8867C12.3047 19.1113 12.5 19.1895 12.7246 19.1895Z", fillAlpha = 0.85f)
        }
        return _sFArrowDownCircleFill!!
    }

private var _sFArrowDownCircleFill: ImageVector? = null
