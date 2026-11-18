package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFPersonCropCircleBadge (dualtone)
 * Viewport: 38.4082 x 28.8574
 */
public val SfSymbols.Dualtone.SFPersonCropCircleBadge: ImageVector
    get() {
        if (_sFPersonCropCircleBadge != null) {
            return _sFPersonCropCircleBadge!!
        }
        _sFPersonCropCircleBadge = sfIcon(
            name = "Dualtone.SFPersonCropCircleBadge",
            viewportWidth = 38.4082f,
            viewportHeight = 28.8574f
        ) {
            addSfPath("M31.748 14.4238C31.748 21.4453 26.0449 27.1387 19.0234 27.1387C17.1862 27.1387 15.44 26.7489 13.8673 26.0395C15.0344 24.6674 15.7422 22.8984 15.7422 20.9863C15.7422 20.3375 15.6622 19.7063 15.503 19.1053C16.5531 18.8257 17.7383 18.6621 19.0234 18.6621C22.9212 18.6621 25.8924 20.1615 27.0998 21.728C28.8577 19.8038 29.9219 17.2396 29.9219 14.4238C29.9219 8.39844 25.0488 3.52539 19.0234 3.52539C13.4358 3.52539 8.83923 7.71602 8.20801 13.1325C8.09788 13.1177 7.98475 13.1152 7.87109 13.1152C7.35704 13.1152 6.85379 13.1657 6.36711 13.265C6.94707 6.7846 12.3931 1.69922 19.0234 1.69922C26.0449 1.69922 31.748 7.40234 31.748 14.4238ZM23.3301 11.7676C23.3301 14.4629 21.4453 16.5332 19.0234 16.5137C16.6016 16.4941 14.7266 14.4629 14.7168 11.7676C14.707 9.22852 16.6211 7.11914 19.0234 7.11914C21.4258 7.11914 23.3301 9.22852 23.3301 11.7676Z", fillAlpha = 0.425f)
            addSfPath("M7.87109 27.3047C11.2988 27.3047 14.1895 24.4434 14.1895 20.9863C14.1895 17.5195 11.3379 14.668 7.87109 14.668C4.41406 14.668 1.55273 17.5293 1.55273 20.9863C1.55273 24.4629 4.41406 27.3047 7.87109 27.3047Z", fillAlpha = 0.85f)
        }
        return _sFPersonCropCircleBadge!!
    }

private var _sFPersonCropCircleBadge: ImageVector? = null
