package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFWSquareFill (monochrome)
 * Viewport: 23.3203 x 22.959
 */
public val SfSymbols.Monochrome.SFWSquareFill: ImageVector
    get() {
        if (_sFWSquareFill != null) {
            return _sFWSquareFill!!
        }
        _sFWSquareFill = sfIcon(
            name = "Monochrome.SFWSquareFill",
            viewportWidth = 23.3203f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M22.959 3.76953L22.959 19.1992C22.959 21.6797 21.6797 22.959 19.1504 22.959L3.79883 22.959C1.2793 22.959 0 21.6992 0 19.1992L0 3.76953C0 1.26953 1.2793 0 3.79883 0L19.1504 0C21.6797 0 22.959 1.2793 22.959 3.76953ZM16.8945 6.28906L14.8242 14.9121L14.7461 14.9121L12.4609 6.19141C12.3242 5.69336 12.0117 5.44922 11.4648 5.44922C10.9375 5.44922 10.6055 5.69336 10.4785 6.19141L8.19336 14.9121L8.11523 14.9121L6.05469 6.28906C5.91797 5.6543 5.67383 5.41016 5.15625 5.41016C4.63867 5.41016 4.30664 5.75195 4.30664 6.25C4.30664 6.35742 4.31641 6.46484 4.3457 6.5918L6.91406 16.5527C7.07031 17.1484 7.45117 17.4609 8.05664 17.4609C8.67188 17.4609 9.08203 17.1582 9.21875 16.582L11.4258 8.07617L11.5137 8.07617L13.7207 16.582C13.8574 17.1582 14.2676 17.4609 14.873 17.4609C15.4883 17.4609 15.8691 17.1484 16.0254 16.5527L18.6035 6.5918C18.6328 6.46484 18.6426 6.35742 18.6426 6.25C18.6426 5.75195 18.3105 5.41016 17.793 5.41016C17.2754 5.41016 17.0312 5.6543 16.8945 6.28906Z", fillAlpha = 0.85f)
        }
        return _sFWSquareFill!!
    }

private var _sFWSquareFill: ImageVector? = null
