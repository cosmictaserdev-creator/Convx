package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFHatCapFill (dualtone)
 * Viewport: 34.5844 x 19.6973
 */
public val SfSymbols.Dualtone.SFHatCapFill: ImageVector
    get() {
        if (_sFHatCapFill != null) {
            return _sFHatCapFill!!
        }
        _sFHatCapFill = sfIcon(
            name = "Dualtone.SFHatCapFill",
            viewportWidth = 34.5844f,
            viewportHeight = 19.6973f
        ) {
            addSfPath("M1.3889 17.5586C7.57054 17.1875 21.8381 14.209 25.6077 10.957C22.2287 3.07617 18.2151 0.585938 10.1194 1.96289C2.31664 3.27148-1.19899 9.6582 0.36351 16.8066C0.480698 17.334 0.822495 17.5879 1.3889 17.5586ZM8.35179 2.64648L11.7893 2.02148L11.6526 1.2207C11.5452 0.595703 10.8713 0.253906 10.1194 0.410156L9.24046 0.585938C8.47874 0.732422 8.10765 1.36719 8.22484 2.01172ZM28.176 19.6973C31.0862 19.6973 33.2541 17.2754 33.9768 15.5664C34.4846 14.3164 34.1721 13.7891 33.469 13.584L27.1409 11.7188C25.1487 13.5547 21.3401 15.0684 16.9846 16.0938C20.4905 17.8809 24.6311 19.6973 28.176 19.6973Z", fillAlpha = 0.85f)
        }
        return _sFHatCapFill!!
    }

private var _sFHatCapFill: ImageVector? = null
