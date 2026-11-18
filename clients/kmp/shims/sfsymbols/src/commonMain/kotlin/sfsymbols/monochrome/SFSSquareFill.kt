package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFSSquareFill (monochrome)
 * Viewport: 23.3203 x 22.959
 */
public val SfSymbols.Monochrome.SFSSquareFill: ImageVector
    get() {
        if (_sFSSquareFill != null) {
            return _sFSSquareFill!!
        }
        _sFSSquareFill = sfIcon(
            name = "Monochrome.SFSSquareFill",
            viewportWidth = 23.3203f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M22.959 3.76953L22.959 19.1992C22.959 21.6797 21.6797 22.959 19.1504 22.959L3.79883 22.959C1.2793 22.959 0 21.6992 0 19.1992L0 3.76953C0 1.26953 1.2793 0 3.79883 0L19.1504 0C21.6797 0 22.959 1.2793 22.959 3.76953ZM7.14844 8.73047C7.14844 10.3125 8.1543 11.2109 10.127 11.748L11.9922 12.2656C13.6035 12.7148 14.1797 13.1445 14.1797 14.1016C14.1797 15.2734 12.9883 16.0156 11.4941 16.0156C10.0488 16.0156 9.00391 15.4004 8.61328 14.3066C8.41797 13.8867 8.17383 13.6816 7.76367 13.6816C7.26562 13.6816 6.95312 13.9844 6.95312 14.4922C6.95312 14.668 6.98242 14.8633 7.08008 15.0879C7.50977 16.3672 9.0918 17.4902 11.3672 17.4902C14.1895 17.4902 16.0352 16.1133 16.0352 13.9453C16.0352 12.3047 14.8633 11.3965 12.5977 10.791L10.8691 10.3223C9.55078 9.96094 9.00391 9.47266 9.00391 8.64258C9.00391 7.50977 10 6.71875 11.4453 6.71875C12.8125 6.71875 13.7207 7.30469 14.1211 8.42773C14.2773 8.81836 14.5117 9.02344 14.9512 9.02344C15.4199 9.02344 15.752 8.76953 15.752 8.24219C15.752 8.08594 15.7129 7.89062 15.6348 7.67578C15.1855 6.2793 13.6523 5.2832 11.5137 5.2832C8.94531 5.2832 7.14844 6.70898 7.14844 8.73047Z", fillAlpha = 0.85f)
        }
        return _sFSSquareFill!!
    }

private var _sFSSquareFill: ImageVector? = null
