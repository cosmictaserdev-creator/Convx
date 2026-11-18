package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFCheckmarkSquare (dualtone)
 * Viewport: 23.3203 x 22.959
 */
public val SfSymbols.Dualtone.SFCheckmarkSquare: ImageVector
    get() {
        if (_sFCheckmarkSquare != null) {
            return _sFCheckmarkSquare!!
        }
        _sFCheckmarkSquare = sfIcon(
            name = "Dualtone.SFCheckmarkSquare",
            viewportWidth = 23.3203f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M3.79883 22.959L19.1504 22.959C21.6797 22.959 22.959 21.6797 22.959 19.1992L22.959 3.76953C22.959 1.2793 21.6797 0 19.1504 0L3.79883 0C1.2793 0 0 1.26953 0 3.76953L0 19.1992C0 21.6992 1.2793 22.959 3.79883 22.959ZM3.83789 21.2305C2.4707 21.2305 1.72852 20.5078 1.72852 19.1016L1.72852 3.85742C1.72852 2.46094 2.4707 1.72852 3.83789 1.72852L19.1211 1.72852C20.459 1.72852 21.2305 2.46094 21.2305 3.85742L21.2305 19.1016C21.2305 20.5078 20.459 21.2305 19.1211 21.2305Z", fillAlpha = 0.425f)
            addSfPath("M10.1172 17.4512C10.4785 17.4512 10.7715 17.2852 10.9863 16.9531L16.9727 7.56836C17.0996 7.37305 17.2266 7.12891 17.2266 6.89453C17.2266 6.41602 16.7969 6.09375 16.3477 6.09375C16.0742 6.09375 15.8008 6.26953 15.6055 6.58203L10.0781 15.3613L7.32422 11.9141C7.07031 11.582 6.82617 11.4844 6.5332 11.4844C6.06445 11.4844 5.69336 11.8652 5.69336 12.3438C5.69336 12.5781 5.79102 12.8125 5.94727 13.0176L9.20898 16.9531C9.48242 17.3047 9.75586 17.4512 10.1172 17.4512Z", fillAlpha = 0.85f)
        }
        return _sFCheckmarkSquare!!
    }

private var _sFCheckmarkSquare: ImageVector? = null
