package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFTrapezoidAndLineVertical (dualtone)
 * Viewport: 25.6729 x 29.5215
 */
public val SfSymbols.Dualtone.SFTrapezoidAndLineVertical: ImageVector
    get() {
        if (_sFTrapezoidAndLineVertical != null) {
            return _sFTrapezoidAndLineVertical!!
        }
        _sFTrapezoidAndLineVertical = sfIcon(
            name = "Dualtone.SFTrapezoidAndLineVertical",
            viewportWidth = 25.6729f,
            viewportHeight = 29.5215f
        ) {
            addSfPath("M13.4859 28.5742C13.4859 29.1113 13.1636 29.4336 12.6753 29.4336C12.1578 29.4336 11.8453 29.1113 11.8453 28.5742L11.8453 24.8437L13.4859 24.8437ZM13.4859 23.2031L11.8453 23.2031L11.8453 6.23047L13.4859 6.23047ZM13.4859 0.869141L13.4859 4.59961L11.8453 4.59961L11.8453 0.869141C11.8453 0.332031 12.1578 0 12.6753 0C13.1636 0 13.4859 0.332031 13.4859 0.869141Z", fillAlpha = 0.425f)
            addSfPath("M0.116736 21.6797C-0.352014 23.6133 0.605017 24.8438 2.73392 24.8438L22.5777 24.8438C24.7066 24.8438 25.6636 23.6133 25.1949 21.6797L21.7085 7.36328C21.2593 5.55664 20.2339 4.59961 18.3003 4.59961L7.01127 4.59961C5.07767 4.59961 4.05228 5.55664 3.60306 7.36328ZM1.75736 21.6797L5.16556 7.58789C5.38041 6.66992 5.93705 6.23047 6.94291 6.23047L18.3687 6.23047C19.3745 6.23047 19.9312 6.66992 20.146 7.58789L23.5542 21.6797C23.7788 22.5977 23.3296 23.2031 22.4312 23.2031L2.88041 23.2031C1.98197 23.2031 1.53275 22.5977 1.75736 21.6797Z", fillAlpha = 0.85f)
        }
        return _sFTrapezoidAndLineVertical!!
    }

private var _sFTrapezoidAndLineVertical: ImageVector? = null
