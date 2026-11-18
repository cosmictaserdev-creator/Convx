package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFChevronUpSquare (dualtone)
 * Viewport: 23.3203 x 22.959
 */
public val SfSymbols.Dualtone.SFChevronUpSquare: ImageVector
    get() {
        if (_sFChevronUpSquare != null) {
            return _sFChevronUpSquare!!
        }
        _sFChevronUpSquare = sfIcon(
            name = "Dualtone.SFChevronUpSquare",
            viewportWidth = 23.3203f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M3.79883 22.959L19.1504 22.959C21.6797 22.959 22.959 21.6797 22.959 19.1992L22.959 3.76953C22.959 1.2793 21.6797 0 19.1504 0L3.79883 0C1.2793 0 0 1.26953 0 3.76953L0 19.1992C0 21.6992 1.2793 22.959 3.79883 22.959ZM3.83789 21.2305C2.4707 21.2305 1.72852 20.5078 1.72852 19.1016L1.72852 3.85742C1.72852 2.46094 2.4707 1.72852 3.83789 1.72852L19.1211 1.72852C20.459 1.72852 21.2305 2.46094 21.2305 3.85742L21.2305 19.1016C21.2305 20.5078 20.459 21.2305 19.1211 21.2305Z", fillAlpha = 0.425f)
            addSfPath("M5.29297 14.3555C5.60547 14.6973 6.14258 14.6973 6.44531 14.3652L11.4941 9.07227L16.543 14.3652C16.8555 14.6973 17.3828 14.6973 17.7051 14.3555C18.0078 14.0527 17.998 13.5547 17.6758 13.2129L12.5586 7.83203C11.8848 7.11914 11.1035 7.11914 10.4395 7.83203L5.32227 13.2129C4.99023 13.5547 4.99023 14.0527 5.29297 14.3555Z", fillAlpha = 0.85f)
        }
        return _sFChevronUpSquare!!
    }

private var _sFChevronUpSquare: ImageVector? = null
