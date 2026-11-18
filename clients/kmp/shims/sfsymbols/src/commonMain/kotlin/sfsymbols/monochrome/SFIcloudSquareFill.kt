package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFIcloudSquareFill (monochrome)
 * Viewport: 23.3203 x 22.959
 */
public val SfSymbols.Monochrome.SFIcloudSquareFill: ImageVector
    get() {
        if (_sFIcloudSquareFill != null) {
            return _sFIcloudSquareFill!!
        }
        _sFIcloudSquareFill = sfIcon(
            name = "Monochrome.SFIcloudSquareFill",
            viewportWidth = 23.3203f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M22.959 3.76953L22.959 19.1992C22.959 21.6797 21.6797 22.959 19.1504 22.959L3.79883 22.959C1.2793 22.959 0 21.6992 0 19.1992L0 3.76953C0 1.26953 1.2793 0 3.79883 0L19.1504 0C21.6797 0 22.959 1.2793 22.959 3.76953ZM8.81836 7.66602C8.63281 7.62695 8.39844 7.59766 8.23242 7.59766C6.89453 7.59766 5.78125 8.56445 5.64453 9.89258C4.44336 10.3711 3.53516 11.5234 3.53516 12.959C3.53516 15.0195 4.92188 16.1914 6.8457 16.1914L16.7383 16.1914C18.291 16.1914 19.4531 15 19.4531 13.4961C19.4531 12.1973 18.5742 11.123 17.4121 10.8887C17.4219 10.7324 17.4316 10.5566 17.4316 10.4199C17.4316 7.80273 15.3809 5.68359 12.6758 5.68359C11.084 5.68359 9.66797 6.44531 8.81836 7.66602Z", fillAlpha = 0.85f)
        }
        return _sFIcloudSquareFill!!
    }

private var _sFIcloudSquareFill: ImageVector? = null
