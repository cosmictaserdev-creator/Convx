package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFOSquareFill (dualtone)
 * Viewport: 23.3203 x 22.959
 */
public val SfSymbols.Dualtone.SFOSquareFill: ImageVector
    get() {
        if (_sFOSquareFill != null) {
            return _sFOSquareFill!!
        }
        _sFOSquareFill = sfIcon(
            name = "Dualtone.SFOSquareFill",
            viewportWidth = 23.3203f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M3.79883 22.959L19.1504 22.959C21.6797 22.959 22.959 21.6797 22.959 19.1992L22.959 3.76953C22.959 1.2793 21.6797 0 19.1504 0L3.79883 0C1.2793 0 0 1.26953 0 3.76953L0 19.1992C0 21.6992 1.2793 22.959 3.79883 22.959Z", fillAlpha = 0.2125f)
            addSfPath("M11.4844 17.4902C8.23242 17.4902 5.99609 15 5.99609 11.3867C5.99609 7.76367 8.23242 5.27344 11.4844 5.27344C14.7363 5.27344 16.9727 7.76367 16.9727 11.3867C16.9727 15 14.7363 17.4902 11.4844 17.4902ZM11.4844 16.0742C13.7207 16.0742 15.1953 14.1992 15.1953 11.3867C15.1953 8.56445 13.7207 6.69922 11.4844 6.69922C9.25781 6.69922 7.77344 8.56445 7.77344 11.3867C7.77344 14.1992 9.25781 16.0742 11.4844 16.0742Z", fillAlpha = 0.85f)
        }
        return _sFOSquareFill!!
    }

private var _sFOSquareFill: ImageVector? = null
