package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFIcloudSquareFill (dualtone)
 * Viewport: 23.3203 x 22.959
 */
public val SfSymbols.Dualtone.SFIcloudSquareFill: ImageVector
    get() {
        if (_sFIcloudSquareFill != null) {
            return _sFIcloudSquareFill!!
        }
        _sFIcloudSquareFill = sfIcon(
            name = "Dualtone.SFIcloudSquareFill",
            viewportWidth = 23.3203f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M3.79883 22.959L19.1504 22.959C21.6797 22.959 22.959 21.6797 22.959 19.1992L22.959 3.76953C22.959 1.2793 21.6797 0 19.1504 0L3.79883 0C1.2793 0 0 1.26953 0 3.76953L0 19.1992C0 21.6992 1.2793 22.959 3.79883 22.959Z", fillAlpha = 0.2125f)
            addSfPath("M6.8457 16.1914C4.92188 16.1914 3.53516 15.0195 3.53516 12.959C3.53516 11.5234 4.44336 10.3711 5.64453 9.89258C5.78125 8.56445 6.89453 7.59766 8.23242 7.59766C8.39844 7.59766 8.63281 7.62695 8.81836 7.66602C9.66797 6.44531 11.084 5.68359 12.6758 5.68359C15.3809 5.68359 17.4316 7.80273 17.4316 10.4199C17.4316 10.5566 17.4219 10.7324 17.4121 10.8887C18.5742 11.123 19.4531 12.1973 19.4531 13.4961C19.4531 15 18.291 16.1914 16.7383 16.1914Z", fillAlpha = 0.85f)
        }
        return _sFIcloudSquareFill!!
    }

private var _sFIcloudSquareFill: ImageVector? = null
