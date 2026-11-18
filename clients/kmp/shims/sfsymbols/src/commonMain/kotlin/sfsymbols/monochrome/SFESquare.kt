package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFESquare (monochrome)
 * Viewport: 23.3203 x 22.959
 */
public val SfSymbols.Monochrome.SFESquare: ImageVector
    get() {
        if (_sFESquare != null) {
            return _sFESquare!!
        }
        _sFESquare = sfIcon(
            name = "Monochrome.SFESquare",
            viewportWidth = 23.3203f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M3.79883 22.959L19.1504 22.959C21.6797 22.959 22.959 21.6797 22.959 19.1992L22.959 3.76953C22.959 1.2793 21.6797 0 19.1504 0L3.79883 0C1.2793 0 0 1.26953 0 3.76953L0 19.1992C0 21.6992 1.2793 22.959 3.79883 22.959ZM3.83789 21.2305C2.4707 21.2305 1.72852 20.5078 1.72852 19.1016L1.72852 3.85742C1.72852 2.46094 2.4707 1.72852 3.83789 1.72852L19.1211 1.72852C20.459 1.72852 21.2305 2.46094 21.2305 3.85742L21.2305 19.1016C21.2305 20.5078 20.459 21.2305 19.1211 21.2305Z", fillAlpha = 0.85f)
            addSfPath("M8.54492 17.0898L14.541 17.0898C14.9805 17.0898 15.2734 16.8066 15.2734 16.3672C15.2734 15.9277 14.9805 15.6543 14.541 15.6543L9.42383 15.6543L9.42383 11.9727L14.2285 11.9727C14.6582 11.9727 14.9512 11.7188 14.9512 11.2891C14.9512 10.8496 14.6582 10.5957 14.2285 10.5957L9.42383 10.5957L9.42383 7.11914L14.541 7.11914C14.9805 7.11914 15.2734 6.82617 15.2734 6.39648C15.2734 5.95703 14.9805 5.68359 14.541 5.68359L8.54492 5.68359C7.99805 5.68359 7.70508 6.07422 7.70508 6.65039L7.70508 16.1133C7.70508 16.6895 7.99805 17.0898 8.54492 17.0898Z", fillAlpha = 0.85f)
        }
        return _sFESquare!!
    }

private var _sFESquare: ImageVector? = null
