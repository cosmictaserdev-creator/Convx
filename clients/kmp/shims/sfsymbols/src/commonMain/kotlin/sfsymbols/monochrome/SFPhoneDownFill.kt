package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFPhoneDownFill (monochrome)
 * Viewport: 29.7949 x 10.7129
 */
public val SfSymbols.Monochrome.SFPhoneDownFill: ImageVector
    get() {
        if (_sFPhoneDownFill != null) {
            return _sFPhoneDownFill!!
        }
        _sFPhoneDownFill = sfIcon(
            name = "Monochrome.SFPhoneDownFill",
            viewportWidth = 29.7949f,
            viewportHeight = 10.7129f
        ) {
            addSfPath("M14.7168 0C9.86328 0 4.33594 0.898438 1.64062 3.66211C0.615234 4.70703 0 6.00586 0 7.64648C0 8.75 0.341797 10.3223 1.51367 10.5469C1.9043 10.7227 2.32422 10.6836 2.88086 10.5957L6.5332 9.98047C7.79297 9.77539 8.41797 9.29688 8.75 8.07617L9.35547 5.87891C9.47266 5.43945 9.59961 5.27344 10.1074 5.08789C11.123 4.72656 12.7344 4.54102 14.7168 4.53125C16.6992 4.53125 18.3105 4.72656 19.3262 5.08789C19.834 5.27344 19.9609 5.43945 20.0781 5.87891L20.6836 8.07617C21.0254 9.29688 21.6406 9.77539 22.9004 9.98047L26.5527 10.5957C27.1191 10.6836 27.5293 10.7227 27.9199 10.5469C29.0918 10.3223 29.4336 8.75 29.4336 7.64648C29.4336 6.00586 28.8184 4.70703 27.793 3.66211C25.0977 0.898438 19.5801 0 14.7168 0Z", fillAlpha = 0.85f)
        }
        return _sFPhoneDownFill!!
    }

private var _sFPhoneDownFill: ImageVector? = null
