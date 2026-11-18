package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFCloudFogFill (dualtone)
 * Viewport: 29.5605 x 28.5059
 */
public val SfSymbols.Dualtone.SFCloudFogFill: ImageVector
    get() {
        if (_sFCloudFogFill != null) {
            return _sFCloudFogFill!!
        }
        _sFCloudFogFill = sfIcon(
            name = "Dualtone.SFCloudFogFill",
            viewportWidth = 29.5605f,
            viewportHeight = 28.5059f
        ) {
            addSfPath("M23.6621 26.2402L7.08008 26.2402C6.68945 26.2402 6.38672 26.5332 6.38672 26.9238C6.38672 27.3242 6.68945 27.6172 7.08008 27.6172L23.6621 27.6172C24.0527 27.6172 24.3652 27.3242 24.3652 26.9238C24.3652 26.5332 24.0527 26.2402 23.6621 26.2402Z", fillAlpha = 0.425f)
            addSfPath("M23.6621 21.9531L7.08008 21.9531C6.68945 21.9531 6.38672 22.2559 6.38672 22.6465C6.38672 23.0469 6.68945 23.3398 7.08008 23.3398L23.6621 23.3398C24.0527 23.3398 24.3652 23.0469 24.3652 22.6465C24.3652 22.2559 24.0527 21.9531 23.6621 21.9531Z", fillAlpha = 0.425f)
            addSfPath("M6.73828 18.9355L22.4316 18.9355C26.5039 18.9355 29.5605 15.8496 29.5605 12.0215C29.5605 8.06641 26.3184 5.17578 22.0996 5.17578C20.5176 2.03125 17.6465 0 13.9551 0C9.26758 0 5.41016 3.7207 5 8.39844C2.69531 9.05273 0.927734 10.9961 0.927734 13.6523C0.927734 16.5625 3.05664 18.9355 6.73828 18.9355Z", fillAlpha = 0.85f)
        }
        return _sFCloudFogFill!!
    }

private var _sFCloudFogFill: ImageVector? = null
