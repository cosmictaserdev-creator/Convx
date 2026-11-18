package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFArrowLeftToLineSquareFill (monochrome)
 * Viewport: 23.3203 x 22.959
 */
public val SfSymbols.Monochrome.SFArrowLeftToLineSquareFill: ImageVector
    get() {
        if (_sFArrowLeftToLineSquareFill != null) {
            return _sFArrowLeftToLineSquareFill!!
        }
        _sFArrowLeftToLineSquareFill = sfIcon(
            name = "Monochrome.SFArrowLeftToLineSquareFill",
            viewportWidth = 23.3203f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M22.959 3.76953L22.959 19.1992C22.959 21.6797 21.6797 22.959 19.1504 22.959L3.79883 22.959C1.2793 22.959 0 21.6992 0 19.1992L0 3.76953C0 1.26953 1.2793 0 3.79883 0L19.1504 0C21.6797 0 22.959 1.2793 22.959 3.76953ZM5.06836 6.64062L5.06836 16.3184C5.06836 16.748 5.38086 17.0605 5.83008 17.0605C6.25977 17.0605 6.57227 16.748 6.57227 16.3184L6.57227 6.64062C6.57227 6.20117 6.25977 5.89844 5.83008 5.89844C5.38086 5.89844 5.06836 6.20117 5.06836 6.64062ZM12.3926 6.47461L7.90039 10.918C7.73438 11.084 7.63672 11.2793 7.63672 11.4844C7.63672 11.6992 7.71484 11.8945 7.90039 12.0508L12.373 16.4551C12.5293 16.6113 12.7637 16.7285 12.959 16.7285C13.3984 16.7285 13.7012 16.4258 13.7012 15.9961C13.7012 15.752 13.6328 15.5664 13.4668 15.4297L12.4512 14.4727L9.87305 12.1191L12.2559 12.2559L18.4766 12.2559C18.916 12.2559 19.2383 11.9434 19.2383 11.4844C19.2383 11.0547 18.916 10.7422 18.4766 10.7422L12.2559 10.7422L9.88281 10.8496L12.4707 8.50586L13.4668 7.5293C13.6328 7.37305 13.7012 7.16797 13.7012 6.95312C13.7012 6.52344 13.3789 6.23047 12.9492 6.23047C12.7539 6.23047 12.5488 6.29883 12.3926 6.47461Z", fillAlpha = 0.85f)
        }
        return _sFArrowLeftToLineSquareFill!!
    }

private var _sFArrowLeftToLineSquareFill: ImageVector? = null
