package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFBSquare (monochrome)
 * Viewport: 23.3203 x 22.959
 */
public val SfSymbols.Monochrome.SFBSquare: ImageVector
    get() {
        if (_sFBSquare != null) {
            return _sFBSquare!!
        }
        _sFBSquare = sfIcon(
            name = "Monochrome.SFBSquare",
            viewportWidth = 23.3203f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M3.79883 22.959L19.1504 22.959C21.6797 22.959 22.959 21.6797 22.959 19.1992L22.959 3.76953C22.959 1.2793 21.6797 0 19.1504 0L3.79883 0C1.2793 0 0 1.26953 0 3.76953L0 19.1992C0 21.6992 1.2793 22.959 3.79883 22.959ZM3.83789 21.2305C2.4707 21.2305 1.72852 20.5078 1.72852 19.1016L1.72852 3.85742C1.72852 2.46094 2.4707 1.72852 3.83789 1.72852L19.1211 1.72852C20.459 1.72852 21.2305 2.46094 21.2305 3.85742L21.2305 19.1016C21.2305 20.5078 20.459 21.2305 19.1211 21.2305Z", fillAlpha = 0.85f)
            addSfPath("M8.39844 17.1191L12.0801 17.1191C14.6387 17.1191 16.2695 15.8789 16.2695 13.9062C16.2695 12.3828 15.2539 11.2988 13.6133 11.0938L13.6133 11.0254C14.8242 10.7324 15.5859 9.72656 15.5859 8.47656C15.5859 6.77734 14.1992 5.6543 12.0898 5.6543L8.39844 5.6543C7.80273 5.6543 7.45117 6.00586 7.45117 6.61133L7.45117 16.1523C7.45117 16.7578 7.80273 17.1191 8.39844 17.1191ZM9.13086 10.5957L9.13086 6.95312L11.748 6.95312C13.1152 6.95312 13.9258 7.62695 13.9258 8.75C13.9258 9.90234 12.9492 10.5957 11.2793 10.5957ZM9.13086 15.8105L9.13086 11.8262L12.0117 11.8262C13.5938 11.8262 14.5312 12.5781 14.5312 13.8574C14.5312 15.1758 13.5254 15.8105 11.3965 15.8105Z", fillAlpha = 0.85f)
        }
        return _sFBSquare!!
    }

private var _sFBSquare: ImageVector? = null
