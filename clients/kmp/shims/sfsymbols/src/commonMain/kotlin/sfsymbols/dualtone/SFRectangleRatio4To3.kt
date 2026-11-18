package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFRectangleRatio4To3 (dualtone)
 * Viewport: 23.3203 x 17.3535
 */
public val SfSymbols.Dualtone.SFRectangleRatio4To3: ImageVector
    get() {
        if (_sFRectangleRatio4To3 != null) {
            return _sFRectangleRatio4To3!!
        }
        _sFRectangleRatio4To3 = sfIcon(
            name = "Dualtone.SFRectangleRatio4To3",
            viewportWidth = 23.3203f,
            viewportHeight = 17.3535f
        ) {
            addSfPath("M0 3.81836L0 13.5449C0 16.0742 1.2793 17.3535 3.75977 17.3535L19.1895 17.3535C21.6797 17.3535 22.959 16.0742 22.959 13.5449L22.959 3.81836C22.959 1.29883 21.6895 0.0195312 19.1895 0.0195312L3.75977 0.0195312C1.25977 0.0195312 0 1.29883 0 3.81836ZM1.72852 3.85742C1.72852 2.49023 2.45117 1.74805 3.85742 1.74805L19.1016 1.74805C20.5078 1.74805 21.2305 2.49023 21.2305 3.85742L21.2305 13.5156C21.2305 14.8535 20.5078 15.6152 19.1016 15.6152L3.85742 15.6152C2.45117 15.6152 1.72852 14.8535 1.72852 13.5156Z", fillAlpha = 0.85f)
        }
        return _sFRectangleRatio4To3!!
    }

private var _sFRectangleRatio4To3: ImageVector? = null
