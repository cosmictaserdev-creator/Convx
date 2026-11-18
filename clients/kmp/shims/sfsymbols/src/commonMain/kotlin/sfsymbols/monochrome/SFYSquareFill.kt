package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFYSquareFill (monochrome)
 * Viewport: 23.3203 x 22.959
 */
public val SfSymbols.Monochrome.SFYSquareFill: ImageVector
    get() {
        if (_sFYSquareFill != null) {
            return _sFYSquareFill!!
        }
        _sFYSquareFill = sfIcon(
            name = "Monochrome.SFYSquareFill",
            viewportWidth = 23.3203f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M22.959 3.76953L22.959 19.1992C22.959 21.6797 21.6797 22.959 19.1504 22.959L3.79883 22.959C1.2793 22.959 0 21.6992 0 19.1992L0 3.76953C0 1.26953 1.2793 0 3.79883 0L19.1504 0C21.6797 0 22.959 1.2793 22.959 3.76953ZM14.6289 5.95703L11.5332 11.0156L11.4551 11.0156L8.35938 5.95703C8.125 5.53711 7.91016 5.40039 7.51953 5.40039C7.05078 5.40039 6.68945 5.75195 6.68945 6.18164C6.68945 6.35742 6.74805 6.5332 6.85547 6.70898L10.5957 12.7246L10.5957 16.3672C10.5957 16.9629 10.9082 17.3633 11.4746 17.3633C12.0605 17.3633 12.3828 16.9922 12.3828 16.3672L12.3828 12.7051L16.1035 6.70898C16.2109 6.55273 16.2598 6.36719 16.2598 6.20117C16.2598 5.74219 15.9277 5.40039 15.4492 5.40039C15.0684 5.40039 14.8535 5.54688 14.6289 5.95703Z", fillAlpha = 0.85f)
        }
        return _sFYSquareFill!!
    }

private var _sFYSquareFill: ImageVector? = null
