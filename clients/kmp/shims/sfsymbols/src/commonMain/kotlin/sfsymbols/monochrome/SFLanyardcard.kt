package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFLanyardcard (monochrome)
 * Viewport: 19.7168 x 26.9238
 */
public val SfSymbols.Monochrome.SFLanyardcard: ImageVector
    get() {
        if (_sFLanyardcard != null) {
            return _sFLanyardcard!!
        }
        _sFLanyardcard = sfIcon(
            name = "Monochrome.SFLanyardcard",
            viewportWidth = 19.7168f,
            viewportHeight = 26.9238f
        ) {
            addSfPath("M0 23.125C0 25.6445 1.25977 26.9238 3.75977 26.9238L15.5957 26.9238C18.0957 26.9238 19.3555 25.6445 19.3555 23.125L19.3555 3.80859C19.3555 1.28906 18.0957 0.00976562 15.5957 0.00976562L3.75977 0.00976562C1.25977 0.00976562 0 1.28906 0 3.80859ZM1.72852 23.0859L1.72852 3.84766C1.72852 2.48047 2.45117 1.73828 3.85742 1.73828L15.5078 1.73828C16.9043 1.73828 17.627 2.48047 17.627 3.84766L17.627 23.0859C17.627 24.4531 16.9043 25.1953 15.5078 25.1953L3.85742 25.1953C2.45117 25.1953 1.72852 24.4531 1.72852 23.0859ZM6.70898 4.77539L12.666 4.77539C13.0762 4.77539 13.4082 4.43359 13.4082 4.02344C13.4082 3.61328 13.0762 3.27148 12.666 3.27148L6.70898 3.27148C6.28906 3.27148 5.95703 3.61328 5.95703 4.02344C5.95703 4.43359 6.28906 4.77539 6.70898 4.77539Z", fillAlpha = 0.85f)
        }
        return _sFLanyardcard!!
    }

private var _sFLanyardcard: ImageVector? = null
