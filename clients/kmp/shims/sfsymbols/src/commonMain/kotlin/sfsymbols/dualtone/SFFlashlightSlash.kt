package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFFlashlightSlash (dualtone)
 * Viewport: 23.2275 x 29.5801
 */
public val SfSymbols.Dualtone.SFFlashlightSlash: ImageVector
    get() {
        if (_sFFlashlightSlash != null) {
            return _sFFlashlightSlash!!
        }
        _sFFlashlightSlash = sfIcon(
            name = "Dualtone.SFFlashlightSlash",
            viewportWidth = 23.2275f,
            viewportHeight = 29.5801f
        ) {
            addSfPath("M9.25049 16.2995L9.25049 17.5391C9.25049 18.7598 10.2075 19.7168 11.438 19.7168C11.8011 19.7168 12.1404 19.6335 12.4372 19.4828L14.9731 22.0161L14.9731 27.1289C14.9731 28.7207 14.1333 29.5703 12.6001 29.5703L10.2661 29.5703C8.73291 29.5703 7.89307 28.7207 7.89307 27.1289L7.89307 14.9435ZM17.5415 5.51758C17.5415 6.64062 17.1216 7.48047 16.5161 8.4375L15.7935 9.57031C15.2954 10.3418 14.9731 11.2891 14.9731 12.6855L14.9731 15.8105L13.6255 14.4629L13.6255 13.8281C13.6255 12.6074 12.6685 11.6406 11.438 11.6406C11.2425 11.6406 11.0539 11.665 10.8761 11.7135L5.37269 6.2101C5.33459 5.99019 5.31494 5.76051 5.31494 5.51758L5.31494 4.23828L17.5415 4.23828ZM17.5415 2.43164L17.5415 2.88086L5.31494 2.88086L5.31494 2.43164C5.31494 0.800781 6.11572 0 7.68799 0L15.1782 0C16.7505 0 17.5415 0.800781 17.5415 2.43164Z", fillAlpha = 0.425f)
            addSfPath("M1.59424 6.72852L20.1196 25.2344C20.4419 25.5566 20.979 25.5566 21.3013 25.2344C21.6235 24.9023 21.6235 24.3848 21.3013 24.0527L2.78564 5.55664C2.46338 5.23438 1.93604 5.22461 1.59424 5.55664C1.28174 5.86914 1.28174 6.41602 1.59424 6.72852Z", fillAlpha = 0.85f)
        }
        return _sFFlashlightSlash!!
    }

private var _sFFlashlightSlash: ImageVector? = null
