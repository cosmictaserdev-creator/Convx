package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFTorus (monochrome)
 * Viewport: 35.8887 x 21.9238
 */
public val SfSymbols.Monochrome.SFTorus: ImageVector
    get() {
        if (_sFTorus != null) {
            return _sFTorus!!
        }
        _sFTorus = sfIcon(
            name = "Monochrome.SFTorus",
            viewportWidth = 35.8887f,
            viewportHeight = 21.9238f
        ) {
            addSfPath("M17.7734 21.9238C27.6172 21.9238 35.5273 17.0605 35.5273 10.957C35.5273 4.86328 27.5977 0 17.7734 0C7.91992 0 0 4.86328 0 10.957C0 17.0605 7.91016 21.9238 17.7734 21.9238ZM17.7734 20.1855C8.94531 20.1855 1.72852 15.9961 1.72852 10.957C1.72852 5.92773 8.96484 1.73828 17.7734 1.73828C26.5625 1.73828 33.7891 5.92773 33.7891 10.957C33.7891 15.9961 26.5723 20.1855 17.7734 20.1855ZM26.123 11.0938C23.9844 9.16992 20.9473 8.05664 17.7637 8.05664C14.5801 8.05664 11.543 9.16992 9.41406 11.0938L10.7324 12.2559C12.5488 10.6738 15.0684 9.78516 17.7637 9.78516C20.4688 9.78516 22.9883 10.6738 24.7949 12.2559ZM7.58789 10.3027C8.7793 12.3633 12.6758 13.623 17.7637 13.623C22.8613 13.623 26.748 12.3633 27.9492 10.3027C28.418 9.41406 27.3828 8.96484 26.9434 9.46289C25.4297 11.2305 21.5234 11.8945 17.7637 11.8945C14.1016 11.8945 10.1172 11.25 8.59375 9.46289C8.14453 8.96484 7.11914 9.41406 7.58789 10.3027Z", fillAlpha = 0.85f)
        }
        return _sFTorus!!
    }

private var _sFTorus: ImageVector? = null
