package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFBookmarkSquareFill (dualtone)
 * Viewport: 23.3203 x 22.959
 */
public val SfSymbols.Dualtone.SFBookmarkSquareFill: ImageVector
    get() {
        if (_sFBookmarkSquareFill != null) {
            return _sFBookmarkSquareFill!!
        }
        _sFBookmarkSquareFill = sfIcon(
            name = "Dualtone.SFBookmarkSquareFill",
            viewportWidth = 23.3203f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M3.79883 22.959L19.1504 22.959C21.6797 22.959 22.959 21.6797 22.959 19.1992L22.959 3.76953C22.959 1.2793 21.6797 0 19.1504 0L3.79883 0C1.2793 0 0 1.26953 0 3.76953L0 19.1992C0 21.6992 1.2793 22.959 3.79883 22.959Z", fillAlpha = 0.2125f)
            addSfPath("M7.63672 18.3105C7.23633 18.3105 6.98242 18.0273 6.98242 17.5586L6.98242 6.63086C6.98242 5.40039 7.61719 4.75586 8.83789 4.75586L14.1309 4.75586C15.3516 4.75586 15.9863 5.40039 15.9863 6.63086L15.9863 17.5586C15.9863 18.0273 15.7324 18.3105 15.3418 18.3105C15.0391 18.3105 14.8633 18.1445 14.3555 17.6465L11.543 14.8438C11.5137 14.8047 11.4551 14.8047 11.4258 14.8438L8.61328 17.6465C8.10547 18.1445 7.92969 18.3105 7.63672 18.3105Z", fillAlpha = 0.85f)
        }
        return _sFBookmarkSquareFill!!
    }

private var _sFBookmarkSquareFill: ImageVector? = null
