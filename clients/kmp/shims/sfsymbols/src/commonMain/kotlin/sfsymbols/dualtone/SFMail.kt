package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFMail (dualtone)
 * Viewport: 29.9512 x 22.959
 */
public val SfSymbols.Dualtone.SFMail: ImageVector
    get() {
        if (_sFMail != null) {
            return _sFMail!!
        }
        _sFMail = sfIcon(
            name = "Dualtone.SFMail",
            viewportWidth = 29.9512f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M3.79883 22.959L25.7812 22.959C28.3105 22.959 29.5898 21.6797 29.5898 19.1992L29.5898 3.76953C29.5898 1.2793 28.3105 0 25.7812 0L3.79883 0C1.2793 0 0 1.26953 0 3.76953L0 19.1992C0 21.6992 1.2793 22.959 3.79883 22.959ZM3.83789 21.2305C2.4707 21.2305 1.72852 20.5078 1.72852 19.1016L1.72852 3.85742C1.72852 2.46094 2.4707 1.72852 3.83789 1.72852L25.752 1.72852C27.0898 1.72852 27.8516 2.46094 27.8516 3.85742L27.8516 19.1016C27.8516 20.5078 27.0898 21.2305 25.752 21.2305Z", fillAlpha = 0.85f)
            addSfPath("M5.72266 7.28516L14.0723 7.28516C14.4922 7.28516 14.8242 6.94336 14.8242 6.52344C14.8242 6.10352 14.4922 5.77148 14.0723 5.77148L5.72266 5.77148C5.29297 5.77148 4.9707 6.10352 4.9707 6.52344C4.9707 6.94336 5.29297 7.28516 5.72266 7.28516Z", fillAlpha = 0.85f)
            addSfPath("M5.72266 10.5469L11.8359 10.5469C12.2559 10.5469 12.5977 10.2148 12.5977 9.79492C12.5977 9.36523 12.2559 9.02344 11.8359 9.02344L5.72266 9.02344C5.29297 9.02344 4.9707 9.36523 4.9707 9.79492C4.9707 10.2148 5.29297 10.5469 5.72266 10.5469Z", fillAlpha = 0.85f)
            addSfPath("M21.6016 11.0449C23.2617 11.0449 24.6094 9.69727 24.6094 8.02734C24.6094 6.34766 23.2617 5 21.6016 5C19.9316 5 18.584 6.34766 18.584 8.02734C18.584 9.69727 19.9316 11.0449 21.6016 11.0449Z", fillAlpha = 0.85f)
        }
        return _sFMail!!
    }

private var _sFMail: ImageVector? = null
