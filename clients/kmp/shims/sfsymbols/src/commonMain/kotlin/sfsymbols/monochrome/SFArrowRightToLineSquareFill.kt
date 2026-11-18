package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFArrowRightToLineSquareFill (monochrome)
 * Viewport: 23.3203 x 22.959
 */
public val SfSymbols.Monochrome.SFArrowRightToLineSquareFill: ImageVector
    get() {
        if (_sFArrowRightToLineSquareFill != null) {
            return _sFArrowRightToLineSquareFill!!
        }
        _sFArrowRightToLineSquareFill = sfIcon(
            name = "Monochrome.SFArrowRightToLineSquareFill",
            viewportWidth = 23.3203f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M22.959 3.76953L22.959 19.1992C22.959 21.6797 21.6797 22.959 19.1504 22.959L3.79883 22.959C1.2793 22.959 0 21.6992 0 19.1992L0 3.76953C0 1.26953 1.2793 0 3.79883 0L19.1504 0C21.6797 0 22.959 1.2793 22.959 3.76953ZM16.4355 6.64062L16.4355 16.3184C16.4355 16.748 16.748 17.0605 17.168 17.0605C17.6172 17.0605 17.9395 16.748 17.9395 16.3184L17.9395 6.64062C17.9395 6.20117 17.6172 5.89844 17.168 5.89844C16.748 5.89844 16.4355 6.20117 16.4355 6.64062ZM9.30664 6.95312C9.30664 7.16797 9.375 7.37305 9.54102 7.5293L10.5371 8.50586L13.125 10.8496L10.752 10.7422L4.53125 10.7422C4.08203 10.7422 3.76953 11.0547 3.76953 11.4844C3.76953 11.9434 4.08203 12.2559 4.53125 12.2559L10.752 12.2559L13.1348 12.1191L10.5566 14.4727L9.54102 15.4297C9.375 15.5664 9.30664 15.752 9.30664 15.9961C9.30664 16.4258 9.60938 16.7285 10.0488 16.7285C10.2441 16.7285 10.4785 16.6113 10.6348 16.4551L15.1074 12.0508C15.2832 11.8945 15.3711 11.6992 15.3711 11.4844C15.3711 11.2793 15.2637 11.084 15.1074 10.918L10.6152 6.47461C10.459 6.29883 10.2539 6.23047 10.0488 6.23047C9.62891 6.23047 9.30664 6.52344 9.30664 6.95312Z", fillAlpha = 0.85f)
        }
        return _sFArrowRightToLineSquareFill!!
    }

private var _sFArrowRightToLineSquareFill: ImageVector? = null
