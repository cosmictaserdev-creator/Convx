package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SF17SquareFill (monochrome)
 * Viewport: 23.3203 x 22.959
 */
public val SfSymbols.Monochrome.SF17SquareFill: ImageVector
    get() {
        if (_sF17SquareFill != null) {
            return _sF17SquareFill!!
        }
        _sF17SquareFill = sfIcon(
            name = "Monochrome.SF17SquareFill",
            viewportWidth = 23.3203f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M22.959 3.76953L22.959 19.1992C22.959 21.6797 21.6797 22.959 19.1504 22.959L3.79883 22.959C1.2793 22.959 0 21.6992 0 19.1992L0 3.76953C0 1.26953 1.2793 0 3.79883 0L19.1504 0C21.6797 0 22.959 1.2793 22.959 3.76953ZM11.9727 6.25C11.582 6.25 11.3086 6.5332 11.3086 6.92383C11.3086 7.32422 11.582 7.60742 11.9727 7.60742L15.4395 7.60742L15.4395 7.71484L12.4609 15.4688C12.3633 15.7324 12.3242 15.8789 12.3242 16.1426C12.3242 16.4941 12.5488 16.8555 13.0664 16.8555C13.4961 16.8555 13.6816 16.6895 13.8379 16.2695L16.8945 8.10547C17.0117 7.76367 17.0996 7.48047 17.0996 7.13867C17.0996 6.60156 16.6992 6.25 16.1621 6.25ZM7.36328 6.5625L5.85938 7.76367C5.6543 7.91992 5.50781 8.13477 5.50781 8.42773C5.50781 8.78906 5.74219 9.04297 6.09375 9.04297C6.26953 9.04297 6.38672 8.98438 6.50391 8.88672L7.79297 7.86133L7.79297 15.9863C7.79297 16.4551 8.16406 16.8262 8.62305 16.8262C9.07227 16.8262 9.44336 16.4551 9.44336 15.9863L9.44336 7.02148C9.44336 6.44531 9.08203 6.09375 8.49609 6.09375C8.01758 6.09375 7.63672 6.35742 7.36328 6.5625Z", fillAlpha = 0.85f)
        }
        return _sF17SquareFill!!
    }

private var _sF17SquareFill: ImageVector? = null
