package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SF10SquareFill (dualtone)
 * Viewport: 23.3203 x 22.959
 */
public val SfSymbols.Dualtone.SF10SquareFill: ImageVector
    get() {
        if (_sF10SquareFill != null) {
            return _sF10SquareFill!!
        }
        _sF10SquareFill = sfIcon(
            name = "Dualtone.SF10SquareFill",
            viewportWidth = 23.3203f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M3.79883 22.959L19.1504 22.959C21.6797 22.959 22.959 21.6797 22.959 19.1992L22.959 3.76953C22.959 1.2793 21.6797 0 19.1504 0L3.79883 0C1.2793 0 0 1.26953 0 3.76953L0 19.1992C0 21.6992 1.2793 22.959 3.79883 22.959Z", fillAlpha = 0.2125f)
            addSfPath("M8.08594 16.8262C7.61719 16.8262 7.24609 16.4551 7.24609 15.9863L7.24609 7.86133L5.9668 8.88672C5.83984 8.98438 5.73242 9.04297 5.54688 9.04297C5.19531 9.04297 4.96094 8.78906 4.96094 8.42773C4.96094 8.13477 5.10742 7.91992 5.3125 7.76367L6.82617 6.5625C7.09961 6.35742 7.48047 6.09375 7.95898 6.09375C8.53516 6.09375 8.90625 6.44531 8.90625 7.02148L8.90625 15.9863C8.90625 16.4551 8.52539 16.8262 8.08594 16.8262ZM14.1992 16.9434C11.6699 16.9434 10.957 13.9941 10.957 11.4746C10.957 8.95508 11.6699 6.00586 14.1992 6.00586C16.7188 6.00586 17.4316 8.95508 17.4316 11.4746C17.4316 13.9941 16.7188 16.9434 14.1992 16.9434ZM14.1992 15.5859C15.4004 15.5859 15.7715 13.623 15.7715 11.4746C15.7715 9.31641 15.4004 7.36328 14.1992 7.36328C12.9883 7.36328 12.6172 9.31641 12.6172 11.4746C12.6172 13.623 12.9883 15.5859 14.1992 15.5859Z", fillAlpha = 0.85f)
        }
        return _sF10SquareFill!!
    }

private var _sF10SquareFill: ImageVector? = null
