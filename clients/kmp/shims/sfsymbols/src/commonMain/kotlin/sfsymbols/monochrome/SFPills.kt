package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFPills (monochrome)
 * Viewport: 29.3677 x 26.8066
 */
public val SfSymbols.Monochrome.SFPills: ImageVector
    get() {
        if (_sFPills != null) {
            return _sFPills!!
        }
        _sFPills = sfIcon(
            name = "Monochrome.SFPills",
            viewportWidth = 29.3677f,
            viewportHeight = 26.8066f
        ) {
            addSfPath("M22.2669 26.8066C26.0071 26.8066 29.0051 23.7988 29.0051 20.0684C29.0051 16.3867 25.9876 13.3691 22.2669 13.3691C18.5657 13.3691 15.5481 16.3867 15.5481 20.0684C15.5481 23.7891 18.5657 26.8066 22.2669 26.8066ZM22.2669 25.0781C19.513 25.0781 17.2766 22.832 17.2766 20.0684C17.2766 17.3242 19.5227 15.0977 22.2669 15.0977C25.0305 15.0977 27.2766 17.3242 27.2766 20.0684C27.2766 22.8516 25.0501 25.0781 22.2669 25.0781ZM16.4954 20.957L28.0383 20.957L28.0383 19.209L16.4954 19.209Z", fillAlpha = 0.85f)
            addSfPath("M1.74928 19.6875C4.09303 22.0117 7.13991 22.0117 9.45436 19.6973L18.8489 10.3027C21.1633 7.98828 21.1536 4.91211 18.8294 2.60742C16.4856 0.283203 13.4387 0.273438 11.1243 2.58789L1.72975 11.9824C-0.584699 14.2969-0.574934 17.373 1.74928 19.6875ZM2.98952 18.4473C1.42702 16.875 1.47585 14.6973 3.09694 13.0762L12.218 3.95508C13.8489 2.32422 16.0266 2.27539 17.5891 3.83789C19.1516 5.41016 19.1028 7.58789 17.4817 9.21875L8.36061 18.3301C6.72975 19.9609 4.55202 20.0098 2.98952 18.4473ZM6.55397 8.63281L12.804 14.873L14.0344 13.6426L7.79421 7.39258Z", fillAlpha = 0.85f)
        }
        return _sFPills!!
    }

private var _sFPills: ImageVector? = null
