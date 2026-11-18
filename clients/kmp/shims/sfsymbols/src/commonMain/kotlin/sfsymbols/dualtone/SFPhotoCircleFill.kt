package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFPhotoCircleFill (dualtone)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Dualtone.SFPhotoCircleFill: ImageVector
    get() {
        if (_sFPhotoCircleFill != null) {
            return _sFPhotoCircleFill!!
        }
        _sFPhotoCircleFill = sfIcon(
            name = "Dualtone.SFPhotoCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.4395C19.7266 25.4395 25.4395 19.7266 25.4395 12.7246C25.4395 5.71289 19.7266 0 12.7148 0C5.71289 0 0 5.71289 0 12.7246C0 19.7266 5.71289 25.4395 12.7148 25.4395Z", fillAlpha = 0.2125f)
            addSfPath("M7.28516 18.4082C6.05469 18.4082 5.41016 17.7734 5.41016 16.5527L5.41016 8.94531C5.41016 7.70508 6.05469 7.08984 7.28516 7.08984L18.125 7.08984C19.3652 7.08984 20 7.71484 20 8.94531L20 16.5527C20 17.7637 19.3652 18.4082 18.125 18.4082ZM7.34375 17.334L18.0566 17.334C18.6035 17.334 18.9355 17.002 18.9355 16.4844L18.9355 15.3613L16.2012 12.8906C15.8984 12.627 15.5273 12.4707 15.1855 12.4805C14.8242 12.4707 14.4824 12.6172 14.1699 12.8906L11.2012 15.5078L10.0195 14.4531C9.73633 14.1992 9.42383 14.0625 9.12109 14.0625C8.84766 14.0625 8.55469 14.1992 8.27148 14.4336L6.47461 15.9375L6.47461 16.4844C6.47461 17.002 6.80664 17.334 7.34375 17.334ZM9.96094 12.8809C10.791 12.8809 11.4746 12.1973 11.4746 11.3672C11.4746 10.5273 10.791 9.84375 9.96094 9.84375C9.12109 9.84375 8.4375 10.5273 8.4375 11.3672C8.4375 12.1973 9.12109 12.8809 9.96094 12.8809Z", fillAlpha = 0.85f)
        }
        return _sFPhotoCircleFill!!
    }

private var _sFPhotoCircleFill: ImageVector? = null
