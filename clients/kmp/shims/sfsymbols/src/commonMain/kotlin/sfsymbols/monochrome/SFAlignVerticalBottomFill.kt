package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFAlignVerticalBottomFill (monochrome)
 * Viewport: 29.0039 x 27.666
 */
public val SfSymbols.Monochrome.SFAlignVerticalBottomFill: ImageVector
    get() {
        if (_sFAlignVerticalBottomFill != null) {
            return _sFAlignVerticalBottomFill!!
        }
        _sFAlignVerticalBottomFill = sfIcon(
            name = "Monochrome.SFAlignVerticalBottomFill",
            viewportWidth = 29.0039f,
            viewportHeight = 27.666f
        ) {
            addSfPath("M5.84961 23.6719L10.4492 23.6719C12.1094 23.6719 13.0371 22.7344 13.0371 21.0449L13.0371 2.64648C13.0371 0.966797 12.1094 0.0292969 10.4492 0.0292969L5.84961 0.0292969C4.19922 0.0292969 3.27148 0.966797 3.27148 2.64648L3.27148 21.0449C3.27148 22.7344 4.19922 23.6719 5.84961 23.6719ZM18.2129 23.6719L22.8223 23.6719C24.4727 23.6719 25.4004 22.7344 25.4004 21.0449L25.4004 11.377C25.4004 9.69727 24.4727 8.75977 22.8223 8.75977L18.2129 8.75977C16.5625 8.75977 15.6348 9.69727 15.6348 11.377L15.6348 21.0449C15.6348 22.7344 16.5625 23.6719 18.2129 23.6719Z", fillAlpha = 0.85f)
            addSfPath("M0.751953 27.666L27.8906 27.666C28.3105 27.666 28.6426 27.2949 28.6426 26.8652C28.6426 26.4453 28.3105 26.0742 27.8906 26.0742L0.751953 26.0742C0.332031 26.0742 0 26.4453 0 26.8652C0 27.2949 0.332031 27.666 0.751953 27.666Z", fillAlpha = 0.85f)
        }
        return _sFAlignVerticalBottomFill!!
    }

private var _sFAlignVerticalBottomFill: ImageVector? = null
