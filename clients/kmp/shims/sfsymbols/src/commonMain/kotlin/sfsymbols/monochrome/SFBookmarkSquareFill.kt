package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFBookmarkSquareFill (monochrome)
 * Viewport: 23.3203 x 22.959
 */
public val SfSymbols.Monochrome.SFBookmarkSquareFill: ImageVector
    get() {
        if (_sFBookmarkSquareFill != null) {
            return _sFBookmarkSquareFill!!
        }
        _sFBookmarkSquareFill = sfIcon(
            name = "Monochrome.SFBookmarkSquareFill",
            viewportWidth = 23.3203f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M22.959 3.76953L22.959 19.1992C22.959 21.6797 21.6797 22.959 19.1504 22.959L3.79883 22.959C1.2793 22.959 0 21.6992 0 19.1992L0 3.76953C0 1.26953 1.2793 0 3.79883 0L19.1504 0C21.6797 0 22.959 1.2793 22.959 3.76953ZM8.83789 4.75586C7.61719 4.75586 6.98242 5.40039 6.98242 6.63086L6.98242 17.5586C6.98242 18.0273 7.23633 18.3105 7.63672 18.3105C7.92969 18.3105 8.10547 18.1445 8.61328 17.6465L11.4258 14.8438C11.4551 14.8047 11.5137 14.8047 11.543 14.8438L14.3555 17.6465C14.8633 18.1445 15.0391 18.3105 15.3418 18.3105C15.7324 18.3105 15.9863 18.0273 15.9863 17.5586L15.9863 6.63086C15.9863 5.40039 15.3516 4.75586 14.1309 4.75586Z", fillAlpha = 0.85f)
        }
        return _sFBookmarkSquareFill!!
    }

private var _sFBookmarkSquareFill: ImageVector? = null
