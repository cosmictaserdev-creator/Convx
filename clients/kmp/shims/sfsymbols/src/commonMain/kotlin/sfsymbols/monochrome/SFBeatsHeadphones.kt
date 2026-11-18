package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFBeatsHeadphones (monochrome)
 * Viewport: 25.1855 x 26.5137
 */
public val SfSymbols.Monochrome.SFBeatsHeadphones: ImageVector
    get() {
        if (_sFBeatsHeadphones != null) {
            return _sFBeatsHeadphones!!
        }
        _sFBeatsHeadphones = sfIcon(
            name = "Monochrome.SFBeatsHeadphones",
            viewportWidth = 25.1855f,
            viewportHeight = 26.5137f
        ) {
            addSfPath("M0 12.5C0 14.6582 0.556641 16.875 1.51367 18.8477C1.16211 19.1992 1.10352 19.8926 1.42578 20.5469L3.99414 25.7617C4.18945 26.1621 4.64844 26.377 5.08789 26.377C5.56641 26.377 6.02539 26.1133 6.11328 25.5273L3.29102 19.7754C2.48047 18.1348 1.63086 15.3906 1.63086 12.5098C1.63086 6.12305 6.00586 1.81641 12.4121 1.81641C18.8184 1.81641 23.1934 6.12305 23.1934 12.5098C23.1934 15.3906 22.3438 18.1348 21.5332 19.7754L18.7109 25.5273C18.7988 26.1133 19.2578 26.377 19.7363 26.377C20.1758 26.377 20.6348 26.1621 20.8301 25.7617L23.3984 20.5469C23.7207 19.8926 23.6621 19.1992 23.3105 18.8477C24.2676 16.875 24.8242 14.6582 24.8242 12.5C24.8242 5.41992 19.9512 0 12.4121 0C4.87305 0 0 5.41992 0 12.5Z", fillAlpha = 0.85f)
            addSfPath("M5.89844 17.1387C5.50781 16.3379 4.78516 16.1621 4.20898 16.3672C3.52539 16.5918 3.00781 17.4023 3.51562 18.4277L6.79688 25.0977C7.2168 25.9473 7.97852 26.2695 8.70117 25.918C9.39453 25.6445 9.7168 24.873 9.30664 24.0332ZM18.9258 17.1387L15.5176 24.0332C15.1074 24.873 15.4297 25.6445 16.123 25.918C16.8457 26.2695 17.6074 25.9473 18.0273 25.0977L21.3086 18.4277C21.8164 17.4023 21.2988 16.5918 20.6152 16.3672C20.0391 16.1621 19.3164 16.3379 18.9258 17.1387Z", fillAlpha = 0.85f)
        }
        return _sFBeatsHeadphones!!
    }

private var _sFBeatsHeadphones: ImageVector? = null
