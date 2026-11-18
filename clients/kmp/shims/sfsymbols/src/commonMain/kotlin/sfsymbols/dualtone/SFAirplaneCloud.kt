package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFAirplaneCloud (dualtone)
 * Viewport: 32.6678 x 23.6426
 */
public val SfSymbols.Dualtone.SFAirplaneCloud: ImageVector
    get() {
        if (_sFAirplaneCloud != null) {
            return _sFAirplaneCloud!!
        }
        _sFAirplaneCloud = sfIcon(
            name = "Dualtone.SFAirplaneCloud",
            viewportWidth = 32.6678f,
            viewportHeight = 23.6426f
        ) {
            addSfPath("M20.3183 9.76562L28.3554 9.76562C30.4452 9.76562 32.0468 8.19336 32.0468 6.19141C32.0468 4.16016 30.3964 2.64648 28.1992 2.63672C27.3691 1.00586 25.8749 0 23.9999 0C21.6171 0 19.6054 1.875 19.3808 4.30664C18.1503 4.6582 17.2909 5.69336 17.2909 7.02148C17.2909 8.62305 18.4824 9.76562 20.3183 9.76562Z", fillAlpha = 0.425f)
            addSfPath("M5.12298 21.4355L27.3398 21.4844C29.8788 21.4941 31.9784 20.2734 31.8124 18.418C31.6464 16.7285 29.5175 15.7324 27.2031 15.8789L19.4492 16.4062C18.9316 16.4355 18.58 16.3477 18.1113 15.9277L9.10735 8.32031C8.84368 8.08594 8.52142 7.98828 8.19915 7.98828L7.34954 8.01758C7.03704 8.02734 6.85149 8.32031 7.03704 8.65234L12.0077 17.8125L12.4667 16.875L5.92376 17.3242L7.92571 17.9199L1.63665 13.6035C1.34368 13.3984 1.07024 13.3789 0.679619 13.5938L0.29876 13.7891C-0.0039746 13.9258-0.072334 14.1895 0.0741504 14.4629L3.0722 20.4199C3.43353 21.1328 4.12688 21.4355 5.12298 21.4355Z", fillAlpha = 0.85f)
        }
        return _sFAirplaneCloud!!
    }

private var _sFAirplaneCloud: ImageVector? = null
