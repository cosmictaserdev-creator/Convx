package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFArrowDownRightAndArrowUpLeft (monochrome)
 * Viewport: 24.9707 x 25.2637
 */
public val SfSymbols.Monochrome.SFArrowDownRightAndArrowUpLeft: ImageVector
    get() {
        if (_sFArrowDownRightAndArrowUpLeft != null) {
            return _sFArrowDownRightAndArrowUpLeft!!
        }
        _sFArrowDownRightAndArrowUpLeft = sfIcon(
            name = "Monochrome.SFArrowDownRightAndArrowUpLeft",
            viewportWidth = 24.9707f,
            viewportHeight = 25.2637f
        ) {
            addSfPath("M2.05078 11.3086L9.375 11.3086C10.459 11.3086 11.0938 10.6641 11.0938 9.58008L11.0938 2.26562C11.0938 1.70898 10.6836 1.26953 10.1074 1.26953C9.55078 1.26953 9.14062 1.69922 9.14062 2.26562L9.14062 3.98438L9.36523 8.29102L5.73242 4.53125L1.72852 0.488281C1.54297 0.302734 1.30859 0.205078 1.04492 0.205078C0.449219 0.205078 0 0.605469 0 1.21094C0 1.48438 0.0976562 1.74805 0.292969 1.93359L4.32617 5.9668L8.08594 9.58008L3.76953 9.35547L2.05078 9.35547C1.48438 9.35547 1.05469 9.74609 1.04492 10.3223C1.04492 10.8984 1.47461 11.3086 2.05078 11.3086ZM14.3457 24.3066C14.9121 24.3066 15.3125 23.877 15.3125 23.3105L15.3125 21.3379L15.0879 17.041L18.7207 20.8008L22.8809 24.9805C23.0566 25.1758 23.3008 25.2637 23.5547 25.2637C24.1602 25.2637 24.6094 24.873 24.6094 24.2676C24.6094 23.9941 24.502 23.7305 24.3066 23.5352L20.1367 19.3652L16.3672 15.7422L20.6836 15.9766L22.6562 15.9766C23.2129 15.9766 23.6523 15.5762 23.6621 15C23.6621 14.4336 23.2324 14.0234 22.6562 14.0234L15.0781 14.0234C13.9941 14.0234 13.3594 14.6582 13.3594 15.7422L13.3594 23.3105C13.3594 23.8672 13.7695 24.3066 14.3457 24.3066Z", fillAlpha = 0.85f)
        }
        return _sFArrowDownRightAndArrowUpLeft!!
    }

private var _sFArrowDownRightAndArrowUpLeft: ImageVector? = null
