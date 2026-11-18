package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFRSquare (monochrome)
 * Viewport: 23.3203 x 22.959
 */
public val SfSymbols.Monochrome.SFRSquare: ImageVector
    get() {
        if (_sFRSquare != null) {
            return _sFRSquare!!
        }
        _sFRSquare = sfIcon(
            name = "Monochrome.SFRSquare",
            viewportWidth = 23.3203f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M3.79883 22.959L19.1504 22.959C21.6797 22.959 22.959 21.6797 22.959 19.1992L22.959 3.76953C22.959 1.2793 21.6797 0 19.1504 0L3.79883 0C1.2793 0 0 1.26953 0 3.76953L0 19.1992C0 21.6992 1.2793 22.959 3.79883 22.959ZM3.83789 21.2305C2.4707 21.2305 1.72852 20.5078 1.72852 19.1016L1.72852 3.85742C1.72852 2.46094 2.4707 1.72852 3.83789 1.72852L19.1211 1.72852C20.459 1.72852 21.2305 2.46094 21.2305 3.85742L21.2305 19.1016C21.2305 20.5078 20.459 21.2305 19.1211 21.2305Z", fillAlpha = 0.85f)
            addSfPath("M8.28125 17.2168C8.83789 17.2168 9.15039 16.8555 9.15039 16.2695L9.15039 12.8516L11.6699 12.8516L13.9453 16.6406C14.1992 17.0605 14.4238 17.2168 14.8047 17.2168C15.2734 17.2168 15.5859 16.9141 15.5859 16.4844C15.5859 16.2402 15.5273 16.0645 15.3809 15.8398L13.3691 12.6172C14.873 12.168 15.8496 10.8691 15.8496 9.28711C15.8496 7.09961 14.2773 5.68359 11.8457 5.68359L8.26172 5.68359C7.71484 5.68359 7.42188 6.07422 7.42188 6.65039L7.42188 16.2695C7.42188 16.8359 7.72461 17.2168 8.28125 17.2168ZM9.15039 11.5625L9.15039 7.03125L11.6602 7.03125C13.1348 7.03125 14.1309 7.88086 14.1309 9.32617C14.1309 10.7324 13.1543 11.5625 11.6211 11.5625Z", fillAlpha = 0.85f)
        }
        return _sFRSquare!!
    }

private var _sFRSquare: ImageVector? = null
