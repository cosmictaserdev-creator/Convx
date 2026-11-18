package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFNSquare (dualtone)
 * Viewport: 23.3203 x 22.959
 */
public val SfSymbols.Dualtone.SFNSquare: ImageVector
    get() {
        if (_sFNSquare != null) {
            return _sFNSquare!!
        }
        _sFNSquare = sfIcon(
            name = "Dualtone.SFNSquare",
            viewportWidth = 23.3203f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M3.79883 22.959L19.1504 22.959C21.6797 22.959 22.959 21.6797 22.959 19.1992L22.959 3.76953C22.959 1.2793 21.6797 0 19.1504 0L3.79883 0C1.2793 0 0 1.26953 0 3.76953L0 19.1992C0 21.6992 1.2793 22.959 3.79883 22.959ZM3.83789 21.2305C2.4707 21.2305 1.72852 20.5078 1.72852 19.1016L1.72852 3.85742C1.72852 2.46094 2.4707 1.72852 3.83789 1.72852L19.1211 1.72852C20.459 1.72852 21.2305 2.46094 21.2305 3.85742L21.2305 19.1016C21.2305 20.5078 20.459 21.2305 19.1211 21.2305Z", fillAlpha = 0.425f)
            addSfPath("M7.68555 17.2168C8.20312 17.2168 8.50586 16.8945 8.50586 16.3574L8.50586 8.4668L8.57422 8.4668L14.2871 16.6602C14.5898 17.0898 14.8145 17.2168 15.1953 17.2168C15.7422 17.2168 16.084 16.875 16.084 16.2891L16.084 6.41602C16.084 5.86914 15.7812 5.55664 15.2637 5.55664C14.7559 5.55664 14.4629 5.88867 14.4629 6.41602L14.4629 14.4043L14.3848 14.4043L8.68164 6.10352C8.38867 5.68359 8.13477 5.55664 7.75391 5.55664C7.20703 5.55664 6.88477 5.9082 6.88477 6.50391L6.88477 16.3574C6.88477 16.875 7.17773 17.2168 7.68555 17.2168Z", fillAlpha = 0.85f)
        }
        return _sFNSquare!!
    }

private var _sFNSquare: ImageVector? = null
