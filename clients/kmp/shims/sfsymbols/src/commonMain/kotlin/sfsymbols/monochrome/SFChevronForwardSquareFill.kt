package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFChevronForwardSquareFill (monochrome)
 * Viewport: 23.3203 x 22.959
 */
public val SfSymbols.Monochrome.SFChevronForwardSquareFill: ImageVector
    get() {
        if (_sFChevronForwardSquareFill != null) {
            return _sFChevronForwardSquareFill!!
        }
        _sFChevronForwardSquareFill = sfIcon(
            name = "Monochrome.SFChevronForwardSquareFill",
            viewportWidth = 23.3203f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M22.959 3.76953L22.959 19.1992C22.959 21.6797 21.6797 22.959 19.1504 22.959L3.79883 22.959C1.2793 22.959 0 21.6992 0 19.1992L0 3.76953C0 1.26953 1.2793 0 3.79883 0L19.1504 0C21.6797 0 22.959 1.2793 22.959 3.76953ZM8.59375 5.12695C8.25195 5.45898 8.24219 6.00586 8.58398 6.32812L14.0137 11.4844L8.58398 16.6504C8.25195 16.9629 8.24219 17.5 8.58398 17.8418C8.87695 18.125 9.46289 18.1152 9.77539 17.8027L15.2832 12.5781C15.9082 11.9922 15.918 10.9863 15.2832 10.3906L9.77539 5.16602C9.43359 4.83398 8.91602 4.82422 8.59375 5.12695Z", fillAlpha = 0.85f)
        }
        return _sFChevronForwardSquareFill!!
    }

private var _sFChevronForwardSquareFill: ImageVector? = null
