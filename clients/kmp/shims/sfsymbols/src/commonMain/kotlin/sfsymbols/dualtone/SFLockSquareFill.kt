package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFLockSquareFill (dualtone)
 * Viewport: 23.3203 x 22.959
 */
public val SfSymbols.Dualtone.SFLockSquareFill: ImageVector
    get() {
        if (_sFLockSquareFill != null) {
            return _sFLockSquareFill!!
        }
        _sFLockSquareFill = sfIcon(
            name = "Dualtone.SFLockSquareFill",
            viewportWidth = 23.3203f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M3.79883 22.959L19.1504 22.959C21.6797 22.959 22.959 21.6797 22.959 19.1992L22.959 3.76953C22.959 1.2793 21.6797 0 19.1504 0L3.79883 0C1.2793 0 0 1.26953 0 3.76953L0 19.1992C0 21.6992 1.2793 22.959 3.79883 22.959Z", fillAlpha = 0.2125f)
            addSfPath("M6.80664 16.6211L6.80664 11.416C6.80664 10.4688 7.20703 10.0195 8.00781 9.98047L8.00781 8.37891C8.00781 6.05469 9.41406 4.51172 11.4844 4.51172C13.5547 4.51172 14.9609 6.05469 14.9609 8.37891L14.9609 9.98047C15.7617 10.0195 16.1621 10.4688 16.1621 11.416L16.1621 16.6211C16.1621 17.6074 15.7227 18.0566 14.8145 18.0566L8.1543 18.0566C7.24609 18.0566 6.80664 17.6074 6.80664 16.6211ZM9.21875 9.9707L13.7598 9.9707L13.7598 8.26172C13.7598 6.70898 12.8418 5.67383 11.4844 5.67383C10.127 5.67383 9.21875 6.70898 9.21875 8.26172Z", fillAlpha = 0.85f)
        }
        return _sFLockSquareFill!!
    }

private var _sFLockSquareFill: ImageVector? = null
