package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFButtonProgrammableSquareFill (monochrome)
 * Viewport: 23.3203 x 22.959
 */
public val SfSymbols.Monochrome.SFButtonProgrammableSquareFill: ImageVector
    get() {
        if (_sFButtonProgrammableSquareFill != null) {
            return _sFButtonProgrammableSquareFill!!
        }
        _sFButtonProgrammableSquareFill = sfIcon(
            name = "Monochrome.SFButtonProgrammableSquareFill",
            viewportWidth = 23.3203f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M22.959 3.76953L22.959 19.1992C22.959 21.6797 21.6797 22.959 19.1504 22.959L3.79883 22.959C1.2793 22.959 0 21.6992 0 19.1992L0 3.76953C0 1.26953 1.2793 0 3.79883 0L19.1504 0C21.6797 0 22.959 1.2793 22.959 3.76953ZM4.23828 11.4941C4.23828 15.4883 7.48047 18.7402 11.4746 18.7402C15.4785 18.7402 18.7207 15.4883 18.7207 11.4941C18.7207 7.49023 15.4785 4.24805 11.4746 4.24805C7.48047 4.24805 4.23828 7.49023 4.23828 11.4941ZM17.1484 11.4941C17.1484 14.6289 14.6094 17.168 11.4746 17.168C8.34961 17.168 5.81055 14.6289 5.81055 11.4941C5.81055 8.35938 8.34961 5.82031 11.4746 5.82031C14.6094 5.82031 17.1484 8.35938 17.1484 11.4941ZM7.08008 11.4941C7.08008 13.916 9.05273 15.8984 11.4746 15.8984C13.9062 15.8984 15.8789 13.916 15.8789 11.4941C15.8789 9.0625 13.9062 7.08984 11.4746 7.08984C9.05273 7.08984 7.08008 9.0625 7.08008 11.4941Z", fillAlpha = 0.85f)
        }
        return _sFButtonProgrammableSquareFill!!
    }

private var _sFButtonProgrammableSquareFill: ImageVector? = null
