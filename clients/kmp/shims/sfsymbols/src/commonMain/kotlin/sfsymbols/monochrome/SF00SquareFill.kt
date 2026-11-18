package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SF00SquareFill (monochrome)
 * Viewport: 23.3203 x 22.959
 */
public val SfSymbols.Monochrome.SF00SquareFill: ImageVector
    get() {
        if (_sF00SquareFill != null) {
            return _sF00SquareFill!!
        }
        _sF00SquareFill = sfIcon(
            name = "Monochrome.SF00SquareFill",
            viewportWidth = 23.3203f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M22.959 3.76953L22.959 19.1992C22.959 21.6797 21.6797 22.959 19.1504 22.959L3.79883 22.959C1.2793 22.959 0 21.6992 0 19.1992L0 3.76953C0 1.26953 1.2793 0 3.79883 0L19.1504 0C21.6797 0 22.959 1.2793 22.959 3.76953ZM4.23828 11.4746C4.23828 13.9941 4.95117 16.9434 7.49023 16.9434C10.0098 16.9434 10.7227 13.9941 10.7227 11.4746C10.7227 8.95508 10.0098 6.00586 7.49023 6.00586C4.95117 6.00586 4.23828 8.95508 4.23828 11.4746ZM12.2461 11.4746C12.2461 13.9941 12.959 16.9434 15.4883 16.9434C18.0176 16.9434 18.7305 13.9941 18.7305 11.4746C18.7305 8.95508 18.0176 6.00586 15.4883 6.00586C12.959 6.00586 12.2461 8.95508 12.2461 11.4746ZM9.07227 11.4746C9.07227 13.623 8.69141 15.5859 7.49023 15.5859C6.2793 15.5859 5.9082 13.623 5.9082 11.4746C5.9082 9.31641 6.2793 7.36328 7.49023 7.36328C8.69141 7.36328 9.07227 9.31641 9.07227 11.4746ZM17.0703 11.4746C17.0703 13.623 16.6895 15.5859 15.4883 15.5859C14.2773 15.5859 13.9062 13.623 13.9062 11.4746C13.9062 9.31641 14.2773 7.36328 15.4883 7.36328C16.6895 7.36328 17.0703 9.31641 17.0703 11.4746Z", fillAlpha = 0.85f)
        }
        return _sF00SquareFill!!
    }

private var _sF00SquareFill: ImageVector? = null
