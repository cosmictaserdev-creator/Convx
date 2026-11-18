package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFLockSquareFill (monochrome)
 * Viewport: 23.3203 x 22.959
 */
public val SfSymbols.Monochrome.SFLockSquareFill: ImageVector
    get() {
        if (_sFLockSquareFill != null) {
            return _sFLockSquareFill!!
        }
        _sFLockSquareFill = sfIcon(
            name = "Monochrome.SFLockSquareFill",
            viewportWidth = 23.3203f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M22.959 3.76953L22.959 19.1992C22.959 21.6797 21.6797 22.959 19.1504 22.959L3.79883 22.959C1.2793 22.959 0 21.6992 0 19.1992L0 3.76953C0 1.26953 1.2793 0 3.79883 0L19.1504 0C21.6797 0 22.959 1.2793 22.959 3.76953ZM8.00781 8.37891L8.00781 9.98047C7.20703 10.0195 6.80664 10.4688 6.80664 11.416L6.80664 16.6211C6.80664 17.6074 7.24609 18.0566 8.1543 18.0566L14.8145 18.0566C15.7227 18.0566 16.1621 17.6074 16.1621 16.6211L16.1621 11.416C16.1621 10.4688 15.7617 10.0195 14.9609 9.98047L14.9609 8.37891C14.9609 6.05469 13.5547 4.51172 11.4844 4.51172C9.41406 4.51172 8.00781 6.05469 8.00781 8.37891ZM13.7598 8.26172L13.7598 9.9707L9.21875 9.9707L9.21875 8.26172C9.21875 6.70898 10.127 5.67383 11.4844 5.67383C12.8418 5.67383 13.7598 6.70898 13.7598 8.26172Z", fillAlpha = 0.85f)
        }
        return _sFLockSquareFill!!
    }

private var _sFLockSquareFill: ImageVector? = null
