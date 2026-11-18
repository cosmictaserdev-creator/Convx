package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFAlignVerticalCenterFill (dualtone)
 * Viewport: 29.0039 x 23.7012
 */
public val SfSymbols.Dualtone.SFAlignVerticalCenterFill: ImageVector
    get() {
        if (_sFAlignVerticalCenterFill != null) {
            return _sFAlignVerticalCenterFill!!
        }
        _sFAlignVerticalCenterFill = sfIcon(
            name = "Dualtone.SFAlignVerticalCenterFill",
            viewportWidth = 29.0039f,
            viewportHeight = 23.7012f
        ) {
            addSfPath("M3.27148 12.666L0.751953 12.666C0.332031 12.666 0 12.2949 0 11.8652C0 11.4453 0.332031 11.0742 0.751953 11.0742L3.27148 11.0742ZM28.6426 11.8652C28.6426 12.2949 28.3105 12.666 27.8906 12.666L25.4004 12.666L25.4004 11.0742L27.8906 11.0742C28.3105 11.0742 28.6426 11.4453 28.6426 11.8652ZM15.6348 12.666L13.0371 12.666L13.0371 11.0742L15.6348 11.0742Z", fillAlpha = 0.85f)
            addSfPath("M5.84961 23.7012L10.4492 23.7012C12.1094 23.7012 13.0371 22.7539 13.0371 21.0742L13.0371 2.66602C13.0371 0.986328 12.1094 0.0488281 10.4492 0.0488281L5.84961 0.0488281C4.19922 0.0488281 3.27148 0.986328 3.27148 2.66602L3.27148 21.0742C3.27148 22.7539 4.19922 23.7012 5.84961 23.7012ZM18.2129 19.3262L22.8223 19.3262C24.4727 19.3262 25.4004 18.3887 25.4004 16.6992L25.4004 7.03125C25.4004 5.35156 24.4727 4.41406 22.8223 4.41406L18.2129 4.41406C16.5625 4.41406 15.6348 5.35156 15.6348 7.03125L15.6348 16.6992C15.6348 18.3887 16.5625 19.3262 18.2129 19.3262Z", fillAlpha = 0.425f)
        }
        return _sFAlignVerticalCenterFill!!
    }

private var _sFAlignVerticalCenterFill: ImageVector? = null
