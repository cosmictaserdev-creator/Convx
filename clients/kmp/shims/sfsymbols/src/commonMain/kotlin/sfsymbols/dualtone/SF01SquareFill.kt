package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SF01SquareFill (dualtone)
 * Viewport: 23.3203 x 22.959
 */
public val SfSymbols.Dualtone.SF01SquareFill: ImageVector
    get() {
        if (_sF01SquareFill != null) {
            return _sF01SquareFill!!
        }
        _sF01SquareFill = sfIcon(
            name = "Dualtone.SF01SquareFill",
            viewportWidth = 23.3203f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M3.79883 22.959L19.1504 22.959C21.6797 22.959 22.959 21.6797 22.959 19.1992L22.959 3.76953C22.959 1.2793 21.6797 0 19.1504 0L3.79883 0C1.2793 0 0 1.26953 0 3.76953L0 19.1992C0 21.6992 1.2793 22.959 3.79883 22.959Z", fillAlpha = 0.2125f)
            addSfPath("M8.41797 16.9434C5.89844 16.9434 5.18555 13.9941 5.18555 11.4746C5.18555 8.95508 5.89844 6.00586 8.41797 6.00586C10.9473 6.00586 11.6699 8.95508 11.6699 11.4746C11.6699 13.9941 10.9473 16.9434 8.41797 16.9434ZM8.41797 15.5859C9.61914 15.5859 10 13.623 10 11.4746C10 9.31641 9.61914 7.36328 8.41797 7.36328C7.2168 7.36328 6.83594 9.31641 6.83594 11.4746C6.83594 13.623 7.2168 15.5859 8.41797 15.5859ZM15.9961 16.8262C15.5273 16.8262 15.1562 16.4551 15.1562 15.9863L15.1562 7.86133L13.8672 8.88672C13.75 8.98438 13.6426 9.04297 13.457 9.04297C13.1055 9.04297 12.8613 8.78906 12.8613 8.42773C12.8613 8.13477 13.0176 7.91992 13.2129 7.76367L14.7363 6.5625C15 6.35742 15.3906 6.09375 15.8594 6.09375C16.4453 6.09375 16.8066 6.44531 16.8066 7.02148L16.8066 15.9863C16.8066 16.4551 16.4355 16.8262 15.9961 16.8262Z", fillAlpha = 0.85f)
        }
        return _sF01SquareFill!!
    }

private var _sF01SquareFill: ImageVector? = null
