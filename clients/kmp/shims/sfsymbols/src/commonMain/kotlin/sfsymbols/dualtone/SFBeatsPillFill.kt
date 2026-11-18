package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFBeatsPillFill (dualtone)
 * Viewport: 34.209 x 14.502
 */
public val SfSymbols.Dualtone.SFBeatsPillFill: ImageVector
    get() {
        if (_sFBeatsPillFill != null) {
            return _sFBeatsPillFill!!
        }
        _sFBeatsPillFill = sfIcon(
            name = "Dualtone.SFBeatsPillFill",
            viewportWidth = 34.209f,
            viewportHeight = 14.502f
        ) {
            addSfPath("M7.23633 0C3.22266 0 0 3.23242 0 7.24609C0 11.2598 3.22266 14.4824 7.23633 14.4824L26.6016 14.4824C30.6152 14.4824 33.8477 11.2598 33.8477 7.24609C33.8477 3.23242 30.6152 0 26.6016 0ZM16.9238 10.625C15.459 10.625 14.3359 9.50195 14.3359 8.08594L14.3359 4.35547L15.498 3.84766L15.498 5.9082C15.9375 5.58594 16.4062 5.44922 16.9238 5.44922C18.3301 5.44922 19.5215 6.64062 19.5215 8.08594C19.5215 9.48242 18.3496 10.625 16.9238 10.625ZM16.9238 9.48242C17.6953 9.48242 18.3301 8.83789 18.3301 8.08594C18.3301 7.32422 17.6953 6.66992 16.9238 6.66992C16.1621 6.66992 15.498 7.30469 15.498 8.08594C15.498 8.83789 16.1426 9.48242 16.9238 9.48242Z", fillAlpha = 0.85f)
        }
        return _sFBeatsPillFill!!
    }

private var _sFBeatsPillFill: ImageVector? = null
