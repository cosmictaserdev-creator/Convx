package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFLocationSlash (dualtone)
 * Viewport: 25.9205 x 26.2329
 */
public val SfSymbols.Dualtone.SFLocationSlash: ImageVector
    get() {
        if (_sFLocationSlash != null) {
            return _sFLocationSlash!!
        }
        _sFLocationSlash = sfIcon(
            name = "Dualtone.SFLocationSlash",
            viewportWidth = 25.9205f,
            viewportHeight = 26.2329f
        ) {
            addSfPath("M8.0783 9.58405L2.73566 12.0471C2.67707 12.0667 2.68683 12.1448 2.75519 12.1448L10.5993 12.1038L12.954 14.4572L12.9212 22.3206C12.9212 22.3792 12.9798 22.3889 13.0091 22.3303L15.4866 16.9886L16.7608 18.2622L14.0931 23.9807C13.3607 25.5432 11.2806 25.2502 11.2708 23.512L11.2415 14.0295C11.2415 13.8928 11.2025 13.844 11.0657 13.844L1.51496 13.8049C-0.154963 13.7952-0.58465 11.7639 0.938787 11.0608L6.81969 8.32608ZM23.6146 3.57056L18.7373 14.0254L17.4555 12.7435L21.7689 3.44361C21.8275 3.32642 21.7591 3.27759 21.6517 3.32642L12.334 7.62206L11.0643 6.35228L21.4564 1.51978C23.0775 0.758062 24.3763 1.91041 23.6146 3.57056Z", fillAlpha = 0.425f)
            addSfPath("M22.3841 21.9495C22.7064 22.2815 23.2435 22.2815 23.556 21.9495C23.8685 21.637 23.888 21.0999 23.556 20.7776L4.38605 1.60767C4.07355 1.29517 3.52668 1.27564 3.20441 1.60767C2.88215 1.92017 2.88215 2.46705 3.20441 2.77955Z", fillAlpha = 0.85f)
        }
        return _sFLocationSlash!!
    }

private var _sFLocationSlash: ImageVector? = null
