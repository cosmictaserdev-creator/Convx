package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFPhotoCircleFill (monochrome)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Monochrome.SFPhotoCircleFill: ImageVector
    get() {
        if (_sFPhotoCircleFill != null) {
            return _sFPhotoCircleFill!!
        }
        _sFPhotoCircleFill = sfIcon(
            name = "Monochrome.SFPhotoCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M25.4395 12.7246C25.4395 19.7266 19.7266 25.4395 12.7148 25.4395C5.71289 25.4395 0 19.7266 0 12.7246C0 5.71289 5.71289 0 12.7148 0C19.7266 0 25.4395 5.71289 25.4395 12.7246ZM7.28516 7.08984C6.05469 7.08984 5.41016 7.70508 5.41016 8.94531L5.41016 16.5527C5.41016 17.7734 6.05469 18.4082 7.28516 18.4082L18.125 18.4082C19.3652 18.4082 20 17.7637 20 16.5527L20 8.94531C20 7.71484 19.3652 7.08984 18.125 7.08984ZM16.2012 12.8906L18.9355 15.3613L18.9355 16.4844C18.9355 17.002 18.6035 17.334 18.0566 17.334L7.34375 17.334C6.80664 17.334 6.47461 17.002 6.47461 16.4844L6.47461 15.9375L8.27148 14.4336C8.55469 14.1992 8.84766 14.0625 9.12109 14.0625C9.42383 14.0625 9.73633 14.1992 10.0195 14.4531L11.2012 15.5078L14.1699 12.8906C14.4824 12.6172 14.8242 12.4707 15.1855 12.4805C15.5273 12.4707 15.8984 12.627 16.2012 12.8906ZM11.4746 11.3672C11.4746 12.1973 10.791 12.8809 9.96094 12.8809C9.12109 12.8809 8.4375 12.1973 8.4375 11.3672C8.4375 10.5273 9.12109 9.84375 9.96094 9.84375C10.791 9.84375 11.4746 10.5273 11.4746 11.3672Z", fillAlpha = 0.85f)
        }
        return _sFPhotoCircleFill!!
    }

private var _sFPhotoCircleFill: ImageVector? = null
