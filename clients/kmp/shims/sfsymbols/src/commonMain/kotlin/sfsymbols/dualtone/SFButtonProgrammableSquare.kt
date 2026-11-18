package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFButtonProgrammableSquare (dualtone)
 * Viewport: 23.3203 x 22.959
 */
public val SfSymbols.Dualtone.SFButtonProgrammableSquare: ImageVector
    get() {
        if (_sFButtonProgrammableSquare != null) {
            return _sFButtonProgrammableSquare!!
        }
        _sFButtonProgrammableSquare = sfIcon(
            name = "Dualtone.SFButtonProgrammableSquare",
            viewportWidth = 23.3203f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M3.79883 22.959L19.1504 22.959C21.6797 22.959 22.959 21.6797 22.959 19.1992L22.959 3.76953C22.959 1.2793 21.6797 0 19.1504 0L3.79883 0C1.2793 0 0 1.26953 0 3.76953L0 19.1992C0 21.6992 1.2793 22.959 3.79883 22.959ZM3.83789 21.2305C2.4707 21.2305 1.72852 20.5078 1.72852 19.1016L1.72852 3.85742C1.72852 2.46094 2.4707 1.72852 3.83789 1.72852L19.1211 1.72852C20.459 1.72852 21.2305 2.46094 21.2305 3.85742L21.2305 19.1016C21.2305 20.5078 20.459 21.2305 19.1211 21.2305Z", fillAlpha = 0.425f)
            addSfPath("M11.4746 18.5547C15.3809 18.5547 18.5449 15.3906 18.5449 11.4941C18.5449 7.59766 15.3809 4.43359 11.4746 4.43359C7.57812 4.43359 4.41406 7.59766 4.41406 11.4941C4.41406 15.3906 7.57812 18.5547 11.4746 18.5547ZM11.4746 17.0312C8.41797 17.0312 5.9375 14.5605 5.9375 11.4941C5.9375 8.42773 8.41797 5.95703 11.4746 5.95703C14.541 5.95703 17.0215 8.42773 17.0215 11.4941C17.0215 14.5605 14.541 17.0312 11.4746 17.0312ZM11.4746 15.8008C13.8574 15.8008 15.7812 13.8672 15.7812 11.4941C15.7812 9.12109 13.8574 7.1875 11.4746 7.1875C9.10156 7.1875 7.17773 9.12109 7.17773 11.4941C7.17773 13.8672 9.10156 15.8008 11.4746 15.8008Z", fillAlpha = 0.85f)
        }
        return _sFButtonProgrammableSquare!!
    }

private var _sFButtonProgrammableSquare: ImageVector? = null
