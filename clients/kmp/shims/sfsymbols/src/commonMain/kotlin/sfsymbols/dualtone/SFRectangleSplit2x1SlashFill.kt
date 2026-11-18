package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFRectangleSplit2x1SlashFill (dualtone)
 * Viewport: 29.9512 x 29.6607
 */
public val SfSymbols.Dualtone.SFRectangleSplit2x1SlashFill: ImageVector
    get() {
        if (_sFRectangleSplit2x1SlashFill != null) {
            return _sFRectangleSplit2x1SlashFill!!
        }
        _sFRectangleSplit2x1SlashFill = sfIcon(
            name = "Dualtone.SFRectangleSplit2x1SlashFill",
            viewportWidth = 29.9512f,
            viewportHeight = 29.6607f
        ) {
            addSfPath("M15.6641 18.7689L23.2108 26.3098L15.6641 26.3098ZM13.9355 17.0417L13.9355 26.3098L3.80859 26.3098C1.28906 26.3098 0 25.0501 0 22.5501L0 7.12037C0 5.79721 0.361089 4.81873 1.07825 4.19438ZM29.5898 7.12037L29.5898 22.5501C29.5898 23.8442 29.2442 24.8114 28.5511 25.4373L15.6641 12.5552L15.6641 3.35084L13.9355 3.35084L13.9355 10.8274L6.45612 3.35084L25.791 3.35084C28.3203 3.35084 29.5898 4.63013 29.5898 7.12037Z", fillAlpha = 0.425f)
            addSfPath("M26.875 28.0481C27.207 28.3801 27.7344 28.3801 28.0566 28.0481C28.3789 27.7161 28.3789 27.1985 28.0566 26.8762L2.79297 1.62232C2.4707 1.30005 1.94336 1.29029 1.61133 1.62232C1.28906 1.93482 1.28906 2.48169 1.61133 2.80396Z", fillAlpha = 0.85f)
        }
        return _sFRectangleSplit2x1SlashFill!!
    }

private var _sFRectangleSplit2x1SlashFill: ImageVector? = null
