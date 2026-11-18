package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFHSquare (dualtone)
 * Viewport: 23.3203 x 22.959
 */
public val SfSymbols.Dualtone.SFHSquare: ImageVector
    get() {
        if (_sFHSquare != null) {
            return _sFHSquare!!
        }
        _sFHSquare = sfIcon(
            name = "Dualtone.SFHSquare",
            viewportWidth = 23.3203f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M3.79883 22.959L19.1504 22.959C21.6797 22.959 22.959 21.6797 22.959 19.1992L22.959 3.76953C22.959 1.2793 21.6797 0 19.1504 0L3.79883 0C1.2793 0 0 1.26953 0 3.76953L0 19.1992C0 21.6992 1.2793 22.959 3.79883 22.959ZM3.83789 21.2305C2.4707 21.2305 1.72852 20.5078 1.72852 19.1016L1.72852 3.85742C1.72852 2.46094 2.4707 1.72852 3.83789 1.72852L19.1211 1.72852C20.459 1.72852 21.2305 2.46094 21.2305 3.85742L21.2305 19.1016C21.2305 20.5078 20.459 21.2305 19.1211 21.2305Z", fillAlpha = 0.425f)
            addSfPath("M7.54883 17.2168C8.0957 17.2168 8.41797 16.8652 8.41797 16.2695L8.41797 11.875L14.541 11.875L14.541 16.2695C14.541 16.8555 14.8633 17.2168 15.3906 17.2168C15.9473 17.2168 16.2598 16.8652 16.2598 16.2695L16.2598 6.50391C16.2598 5.89844 15.9473 5.55664 15.3906 5.55664C14.8633 5.55664 14.541 5.9082 14.541 6.50391L14.541 10.5078L8.41797 10.5078L8.41797 6.50391C8.41797 5.89844 8.0957 5.55664 7.54883 5.55664C7.01172 5.55664 6.68945 5.9082 6.68945 6.50391L6.68945 16.2695C6.68945 16.8555 7.01172 17.2168 7.54883 17.2168Z", fillAlpha = 0.85f)
        }
        return _sFHSquare!!
    }

private var _sFHSquare: ImageVector? = null
