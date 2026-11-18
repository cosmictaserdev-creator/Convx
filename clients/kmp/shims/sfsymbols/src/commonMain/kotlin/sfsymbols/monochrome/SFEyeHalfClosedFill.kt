package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFEyeHalfClosedFill (monochrome)
 * Viewport: 33.8379 x 21.3379
 */
public val SfSymbols.Monochrome.SFEyeHalfClosedFill: ImageVector
    get() {
        if (_sFEyeHalfClosedFill != null) {
            return _sFEyeHalfClosedFill!!
        }
        _sFEyeHalfClosedFill = sfIcon(
            name = "Monochrome.SFEyeHalfClosedFill",
            viewportWidth = 33.8379f,
            viewportHeight = 21.3379f
        ) {
            addSfPath("M0.146484 9.58984C4.41406 8.90625 10.9863 8.0957 16.8359 8.0957C22.6953 8.0957 29.2773 8.90625 33.3105 9.56055C32.0996 6.5332 25.5859 0 16.7383 0C7.94922 0 1.38672 6.52344 0.146484 9.58984ZM16.7383 21.3184C25.7812 21.3184 32.3535 14.5996 33.4766 11.4844C30.293 11.0547 26.9238 10.7031 23.75 10.4688C23.8965 14.3066 20.7324 17.6758 16.7383 17.6758C12.7539 17.6758 9.58984 14.3164 9.73633 10.4883C6.54297 10.7129 3.17383 11.0742 0 11.5137C1.16211 14.6387 7.80273 21.3184 16.7383 21.3184ZM16.7383 13.4668C18.2715 13.4668 19.5801 12.2266 19.5117 10.6934C19.5117 10.5371 19.4824 10.3809 19.4531 10.2441C18.6523 10.2148 17.5781 10.1855 16.8359 10.1855C16.0352 10.1953 14.8926 10.2148 14.0234 10.2441C13.9844 10.3809 13.9746 10.5371 13.9746 10.6934C13.9746 12.2266 15.2051 13.4668 16.7383 13.4668Z", fillAlpha = 0.85f)
        }
        return _sFEyeHalfClosedFill!!
    }

private var _sFEyeHalfClosedFill: ImageVector? = null
