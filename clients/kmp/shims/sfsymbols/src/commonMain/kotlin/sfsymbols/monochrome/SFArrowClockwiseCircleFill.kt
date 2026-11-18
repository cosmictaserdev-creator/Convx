package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFArrowClockwiseCircleFill (monochrome)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Monochrome.SFArrowClockwiseCircleFill: ImageVector
    get() {
        if (_sFArrowClockwiseCircleFill != null) {
            return _sFArrowClockwiseCircleFill!!
        }
        _sFArrowClockwiseCircleFill = sfIcon(
            name = "Monochrome.SFArrowClockwiseCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M25.4395 12.7246C25.4395 19.7266 19.7266 25.4395 12.7148 25.4395C5.71289 25.4395 0 19.7266 0 12.7246C0 5.71289 5.71289 0 12.7148 0C19.7266 0 25.4395 5.71289 25.4395 12.7246ZM11.709 6.05469C11.709 6.26953 11.7871 6.45508 11.9434 6.60156L13.0687 7.71125C12.8809 7.70634 12.6983 7.70508 12.5195 7.70508C9.58984 7.70508 7.03125 10.1758 7.03125 13.3105C7.03125 16.5039 9.56055 19.0527 12.7148 19.0527C15.8789 19.0527 18.3984 16.5039 18.3984 13.3496C18.3984 12.9297 18.0371 12.5781 17.627 12.5781C17.1973 12.5781 16.8457 12.9297 16.8457 13.3496C16.8457 15.6543 15.0098 17.5 12.7148 17.5C10.4102 17.5 8.58398 15.6543 8.58398 13.3496C8.58398 11.0449 10.4102 9.21875 12.7148 9.21875C12.9767 9.21875 13.2307 9.22654 13.4645 9.23953L11.9434 10.7324C11.7871 10.8887 11.709 11.0645 11.709 11.2793C11.709 11.6797 12.0215 11.9922 12.4414 11.9922C12.6172 11.9922 12.8223 11.9043 12.9297 11.7871L15.498 9.19922C15.6445 9.0625 15.7129 8.86719 15.7129 8.67188C15.7129 8.47656 15.6445 8.27148 15.498 8.14453L12.9297 5.55664C12.832 5.44922 12.6172 5.3418 12.4414 5.3418C12.0215 5.3418 11.709 5.66406 11.709 6.05469Z", fillAlpha = 0.85f)
        }
        return _sFArrowClockwiseCircleFill!!
    }

private var _sFArrowClockwiseCircleFill: ImageVector? = null
