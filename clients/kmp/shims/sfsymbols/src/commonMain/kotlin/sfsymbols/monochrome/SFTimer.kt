package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFTimer (monochrome)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Monochrome.SFTimer: ImageVector
    get() {
        if (_sFTimer != null) {
            return _sFTimer!!
        }
        _sFTimer = sfIcon(
            name = "Monochrome.SFTimer",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.4395C19.7363 25.4395 25.4395 19.7461 25.4395 12.7246C25.4395 5.70312 19.7363 0 12.7246 0C12.0898 0 11.8066 0.361328 11.8066 0.947266L11.8066 4.94141C11.8066 5.41992 12.1191 5.79102 12.6074 5.79102C13.0859 5.79102 13.3984 5.41992 13.3984 4.94141L13.3984 1.86523C18.8672 2.16797 23.6133 6.69922 23.6133 12.7246C23.6133 18.75 18.7402 23.623 12.7148 23.623C6.68945 23.623 1.81641 18.75 1.81641 12.7344C1.81641 9.94141 2.87109 7.42188 4.55078 5.51758C4.88281 5.11719 4.89258 4.57031 4.53125 4.19922C4.17969 3.81836 3.57422 3.85742 3.18359 4.31641C1.21094 6.5625 0 9.48242 0 12.7246C0 19.7461 5.69336 25.4395 12.7148 25.4395Z", fillAlpha = 0.85f)
            addSfPath("M12.7148 14.6484C13.7891 14.6484 14.6484 13.7891 14.6484 12.7246C14.6484 12.0703 14.3262 11.5039 13.8184 11.1426L7.5 6.63086C6.93359 6.23047 6.34766 6.80664 6.74805 7.39258L11.0352 13.6816C11.4258 14.2578 12.0117 14.6484 12.7148 14.6484Z", fillAlpha = 0.85f)
        }
        return _sFTimer!!
    }

private var _sFTimer: ImageVector? = null
