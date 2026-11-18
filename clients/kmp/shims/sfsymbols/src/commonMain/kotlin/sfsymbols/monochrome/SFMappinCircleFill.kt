package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFMappinCircleFill (monochrome)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Monochrome.SFMappinCircleFill: ImageVector
    get() {
        if (_sFMappinCircleFill != null) {
            return _sFMappinCircleFill!!
        }
        _sFMappinCircleFill = sfIcon(
            name = "Monochrome.SFMappinCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M25.4395 12.7344C25.4395 19.7461 19.7266 25.459 12.7148 25.459C5.71289 25.459 0 19.7461 0 12.7344C0 5.73242 5.71289 0.0195312 12.7148 0.0195312C19.7266 0.0195312 25.4395 5.73242 25.4395 12.7344ZM9.89258 7.77344C9.89258 9.0625 10.7715 10.1367 11.9336 10.4688L11.9336 17.5293C11.9336 19.2871 12.2168 20.7422 12.7148 20.7422C13.2129 20.7422 13.4863 19.3164 13.4863 17.5293L13.4863 10.4688C14.668 10.1465 15.5371 9.07227 15.5371 7.77344C15.5371 6.2207 14.2676 4.96094 12.7148 4.96094C11.1621 4.96094 9.89258 6.2207 9.89258 7.77344ZM12.8906 7.05078C12.8906 7.53906 12.4902 7.95898 11.9922 7.95898C11.5137 7.95898 11.0938 7.53906 11.084 7.05078C11.0742 6.5625 11.5137 6.13281 11.9922 6.13281C12.4902 6.13281 12.9004 6.5625 12.8906 7.05078Z", fillAlpha = 0.85f)
        }
        return _sFMappinCircleFill!!
    }

private var _sFMappinCircleFill: ImageVector? = null
