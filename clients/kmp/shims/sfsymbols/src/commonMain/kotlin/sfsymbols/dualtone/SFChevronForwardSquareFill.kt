package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFChevronForwardSquareFill (dualtone)
 * Viewport: 23.3203 x 22.959
 */
public val SfSymbols.Dualtone.SFChevronForwardSquareFill: ImageVector
    get() {
        if (_sFChevronForwardSquareFill != null) {
            return _sFChevronForwardSquareFill!!
        }
        _sFChevronForwardSquareFill = sfIcon(
            name = "Dualtone.SFChevronForwardSquareFill",
            viewportWidth = 23.3203f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M3.79883 22.959L19.1504 22.959C21.6797 22.959 22.959 21.6797 22.959 19.1992L22.959 3.76953C22.959 1.2793 21.6797 0 19.1504 0L3.79883 0C1.2793 0 0 1.26953 0 3.76953L0 19.1992C0 21.6992 1.2793 22.959 3.79883 22.959Z", fillAlpha = 0.2125f)
            addSfPath("M8.58398 17.8418C8.24219 17.5 8.25195 16.9629 8.58398 16.6504L14.0137 11.4844L8.58398 6.32812C8.24219 6.00586 8.25195 5.45898 8.59375 5.12695C8.91602 4.82422 9.43359 4.83398 9.77539 5.16602L15.2832 10.3906C15.918 10.9863 15.9082 11.9922 15.2832 12.5781L9.77539 17.8027C9.46289 18.1152 8.87695 18.125 8.58398 17.8418Z", fillAlpha = 0.85f)
        }
        return _sFChevronForwardSquareFill!!
    }

private var _sFChevronForwardSquareFill: ImageVector? = null
