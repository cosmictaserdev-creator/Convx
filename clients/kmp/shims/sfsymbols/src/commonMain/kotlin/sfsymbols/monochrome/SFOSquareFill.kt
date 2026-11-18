package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFOSquareFill (monochrome)
 * Viewport: 23.3203 x 22.959
 */
public val SfSymbols.Monochrome.SFOSquareFill: ImageVector
    get() {
        if (_sFOSquareFill != null) {
            return _sFOSquareFill!!
        }
        _sFOSquareFill = sfIcon(
            name = "Monochrome.SFOSquareFill",
            viewportWidth = 23.3203f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M22.959 3.76953L22.959 19.1992C22.959 21.6797 21.6797 22.959 19.1504 22.959L3.79883 22.959C1.2793 22.959 0 21.6992 0 19.1992L0 3.76953C0 1.26953 1.2793 0 3.79883 0L19.1504 0C21.6797 0 22.959 1.2793 22.959 3.76953ZM5.99609 11.3867C5.99609 15 8.23242 17.4902 11.4844 17.4902C14.7363 17.4902 16.9727 15 16.9727 11.3867C16.9727 7.76367 14.7363 5.27344 11.4844 5.27344C8.23242 5.27344 5.99609 7.76367 5.99609 11.3867ZM15.1953 11.3867C15.1953 14.1992 13.7207 16.0742 11.4844 16.0742C9.25781 16.0742 7.77344 14.1992 7.77344 11.3867C7.77344 8.56445 9.25781 6.69922 11.4844 6.69922C13.7207 6.69922 15.1953 8.56445 15.1953 11.3867Z", fillAlpha = 0.85f)
        }
        return _sFOSquareFill!!
    }

private var _sFOSquareFill: ImageVector? = null
