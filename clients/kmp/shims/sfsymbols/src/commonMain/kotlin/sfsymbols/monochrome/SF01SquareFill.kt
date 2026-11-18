package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SF01SquareFill (monochrome)
 * Viewport: 23.3203 x 22.959
 */
public val SfSymbols.Monochrome.SF01SquareFill: ImageVector
    get() {
        if (_sF01SquareFill != null) {
            return _sF01SquareFill!!
        }
        _sF01SquareFill = sfIcon(
            name = "Monochrome.SF01SquareFill",
            viewportWidth = 23.3203f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M22.959 3.76953L22.959 19.1992C22.959 21.6797 21.6797 22.959 19.1504 22.959L3.79883 22.959C1.2793 22.959 0 21.6992 0 19.1992L0 3.76953C0 1.26953 1.2793 0 3.79883 0L19.1504 0C21.6797 0 22.959 1.2793 22.959 3.76953ZM5.18555 11.4746C5.18555 13.9941 5.89844 16.9434 8.41797 16.9434C10.9473 16.9434 11.6699 13.9941 11.6699 11.4746C11.6699 8.95508 10.9473 6.00586 8.41797 6.00586C5.89844 6.00586 5.18555 8.95508 5.18555 11.4746ZM14.7363 6.5625L13.2129 7.76367C13.0176 7.91992 12.8613 8.13477 12.8613 8.42773C12.8613 8.78906 13.1055 9.04297 13.457 9.04297C13.6426 9.04297 13.75 8.98438 13.8672 8.88672L15.1562 7.86133L15.1562 15.9863C15.1562 16.4551 15.5273 16.8262 15.9961 16.8262C16.4355 16.8262 16.8066 16.4551 16.8066 15.9863L16.8066 7.02148C16.8066 6.44531 16.4453 6.09375 15.8594 6.09375C15.3906 6.09375 15 6.35742 14.7363 6.5625ZM10 11.4746C10 13.623 9.61914 15.5859 8.41797 15.5859C7.2168 15.5859 6.83594 13.623 6.83594 11.4746C6.83594 9.31641 7.2168 7.36328 8.41797 7.36328C9.61914 7.36328 10 9.31641 10 11.4746Z", fillAlpha = 0.85f)
        }
        return _sF01SquareFill!!
    }

private var _sF01SquareFill: ImageVector? = null
