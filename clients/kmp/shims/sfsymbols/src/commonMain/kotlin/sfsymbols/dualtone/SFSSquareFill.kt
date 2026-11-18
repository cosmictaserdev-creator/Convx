package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFSSquareFill (dualtone)
 * Viewport: 23.3203 x 22.959
 */
public val SfSymbols.Dualtone.SFSSquareFill: ImageVector
    get() {
        if (_sFSSquareFill != null) {
            return _sFSSquareFill!!
        }
        _sFSSquareFill = sfIcon(
            name = "Dualtone.SFSSquareFill",
            viewportWidth = 23.3203f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M3.79883 22.959L19.1504 22.959C21.6797 22.959 22.959 21.6797 22.959 19.1992L22.959 3.76953C22.959 1.2793 21.6797 0 19.1504 0L3.79883 0C1.2793 0 0 1.26953 0 3.76953L0 19.1992C0 21.6992 1.2793 22.959 3.79883 22.959Z", fillAlpha = 0.2125f)
            addSfPath("M11.3672 17.4902C9.0918 17.4902 7.50977 16.3672 7.08008 15.0879C6.98242 14.8633 6.95312 14.668 6.95312 14.4922C6.95312 13.9844 7.26562 13.6816 7.76367 13.6816C8.17383 13.6816 8.41797 13.8867 8.61328 14.3066C9.00391 15.4004 10.0488 16.0156 11.4941 16.0156C12.9883 16.0156 14.1797 15.2734 14.1797 14.1016C14.1797 13.1445 13.6035 12.7148 11.9922 12.2656L10.127 11.748C8.1543 11.2109 7.14844 10.3125 7.14844 8.73047C7.14844 6.70898 8.94531 5.2832 11.5137 5.2832C13.6523 5.2832 15.1855 6.2793 15.6348 7.67578C15.7129 7.89062 15.752 8.08594 15.752 8.24219C15.752 8.76953 15.4199 9.02344 14.9512 9.02344C14.5117 9.02344 14.2773 8.81836 14.1211 8.42773C13.7207 7.30469 12.8125 6.71875 11.4453 6.71875C10 6.71875 9.00391 7.50977 9.00391 8.64258C9.00391 9.47266 9.55078 9.96094 10.8691 10.3223L12.5977 10.791C14.8633 11.3965 16.0352 12.3047 16.0352 13.9453C16.0352 16.1133 14.1895 17.4902 11.3672 17.4902Z", fillAlpha = 0.85f)
        }
        return _sFSSquareFill!!
    }

private var _sFSSquareFill: ImageVector? = null
