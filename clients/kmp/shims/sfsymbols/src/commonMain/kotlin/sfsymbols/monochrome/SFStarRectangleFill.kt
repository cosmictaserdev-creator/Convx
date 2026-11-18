package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFStarRectangleFill (monochrome)
 * Viewport: 29.9512 x 22.959
 */
public val SfSymbols.Monochrome.SFStarRectangleFill: ImageVector
    get() {
        if (_sFStarRectangleFill != null) {
            return _sFStarRectangleFill!!
        }
        _sFStarRectangleFill = sfIcon(
            name = "Monochrome.SFStarRectangleFill",
            viewportWidth = 29.9512f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M29.5898 3.76953L29.5898 19.1992C29.5898 21.6797 28.3105 22.959 25.7812 22.959L3.79883 22.959C1.2793 22.959 0 21.6992 0 19.1992L0 3.76953C0 1.26953 1.2793 0 3.79883 0L25.7812 0C28.3105 0 29.5898 1.2793 29.5898 3.76953ZM14.1211 4.38477L12.6953 8.85742L8.00781 8.82812C7.16797 8.81836 6.95312 9.7168 7.57812 10.166L11.3867 12.8906L9.91211 17.334C9.64844 18.125 10.3613 18.6426 11.0254 18.1445L14.8047 15.3809L18.5742 18.1445C19.248 18.6426 19.9609 18.125 19.6973 17.334L18.2129 12.8906L22.0312 10.1562C22.6172 9.73633 22.4414 8.81836 21.5918 8.82812L16.9043 8.85742L15.4785 4.38477C15.2344 3.62305 14.3652 3.62305 14.1211 4.38477Z", fillAlpha = 0.85f)
        }
        return _sFStarRectangleFill!!
    }

private var _sFStarRectangleFill: ImageVector? = null
