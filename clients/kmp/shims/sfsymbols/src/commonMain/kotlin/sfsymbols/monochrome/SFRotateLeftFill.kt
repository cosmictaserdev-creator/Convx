package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFRotateLeftFill (monochrome)
 * Viewport: 30.7031 x 31.1094
 */
public val SfSymbols.Monochrome.SFRotateLeftFill: ImageVector
    get() {
        if (_sFRotateLeftFill != null) {
            return _sFRotateLeftFill!!
        }
        _sFRotateLeftFill = sfIcon(
            name = "Monochrome.SFRotateLeftFill",
            viewportWidth = 30.7031f,
            viewportHeight = 31.1094f
        ) {
            addSfPath("M8.88672 27.0342L21.4551 27.0342C23.4863 27.0342 24.5117 26.0479 24.5117 23.9873L24.5117 11.458C24.5117 9.38771 23.4863 8.41115 21.4551 8.41115L8.88672 8.41115C6.85547 8.41115 5.83008 9.38771 5.83008 11.458L5.83008 23.9873C5.83008 26.0479 6.85547 27.0342 8.88672 27.0342Z", fillAlpha = 0.85f)
            addSfPath("M24.3164 5.4424L24.3164 0.803729C24.3164-0.0165833 23.6523-0.221661 23.0176 0.247089L19.8535 2.53224C19.3457 2.9131 19.3359 3.33303 19.8535 3.71389L23.0176 5.97951C23.6523 6.44826 24.3164 6.24318 24.3164 5.4424ZM23.0273 2.46389C22.6758 2.46389 22.3828 2.75685 22.3828 3.09865C22.3828 3.45998 22.6758 3.75295 23.0273 3.75295L24.5508 3.75295C27.5195 3.75295 29.3262 5.77443 29.3262 8.51857L29.3262 10.4131C29.3262 10.794 29.6387 11.1065 30.0098 11.1065C30.3906 11.1065 30.7031 10.794 30.7031 10.4131L30.7031 8.50881C30.7031 4.90529 28.2617 2.46389 24.5605 2.46389Z", fillAlpha = 0.85f)
        }
        return _sFRotateLeftFill!!
    }

private var _sFRotateLeftFill: ImageVector? = null
