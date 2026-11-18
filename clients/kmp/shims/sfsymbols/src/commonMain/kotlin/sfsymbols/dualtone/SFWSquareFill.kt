package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFWSquareFill (dualtone)
 * Viewport: 23.3203 x 22.959
 */
public val SfSymbols.Dualtone.SFWSquareFill: ImageVector
    get() {
        if (_sFWSquareFill != null) {
            return _sFWSquareFill!!
        }
        _sFWSquareFill = sfIcon(
            name = "Dualtone.SFWSquareFill",
            viewportWidth = 23.3203f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M3.79883 22.959L19.1504 22.959C21.6797 22.959 22.959 21.6797 22.959 19.1992L22.959 3.76953C22.959 1.2793 21.6797 0 19.1504 0L3.79883 0C1.2793 0 0 1.26953 0 3.76953L0 19.1992C0 21.6992 1.2793 22.959 3.79883 22.959Z", fillAlpha = 0.2125f)
            addSfPath("M8.05664 17.4609C7.45117 17.4609 7.07031 17.1484 6.91406 16.5527L4.3457 6.5918C4.31641 6.46484 4.30664 6.35742 4.30664 6.25C4.30664 5.75195 4.63867 5.41016 5.15625 5.41016C5.67383 5.41016 5.91797 5.6543 6.05469 6.28906L8.11523 14.9121L8.19336 14.9121L10.4785 6.19141C10.6055 5.69336 10.9375 5.44922 11.4648 5.44922C12.0117 5.44922 12.3242 5.69336 12.4609 6.19141L14.7461 14.9121L14.8242 14.9121L16.8945 6.28906C17.0312 5.6543 17.2754 5.41016 17.793 5.41016C18.3105 5.41016 18.6426 5.75195 18.6426 6.25C18.6426 6.35742 18.6328 6.46484 18.6035 6.5918L16.0254 16.5527C15.8691 17.1484 15.4883 17.4609 14.873 17.4609C14.2676 17.4609 13.8574 17.1582 13.7207 16.582L11.5137 8.07617L11.4258 8.07617L9.21875 16.582C9.08203 17.1582 8.67188 17.4609 8.05664 17.4609Z", fillAlpha = 0.85f)
        }
        return _sFWSquareFill!!
    }

private var _sFWSquareFill: ImageVector? = null
