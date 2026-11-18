package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFCrossFill (dualtone)
 * Viewport: 26.4648 x 26.1035
 */
public val SfSymbols.Dualtone.SFCrossFill: ImageVector
    get() {
        if (_sFCrossFill != null) {
            return _sFCrossFill!!
        }
        _sFCrossFill = sfIcon(
            name = "Dualtone.SFCrossFill",
            viewportWidth = 26.4648f,
            viewportHeight = 26.1035f
        ) {
            addSfPath("M0 15.498C0 16.9531 0.986328 17.9199 2.45117 17.9199L8.16406 17.9199L8.16406 23.6426C8.16406 25.1074 9.13086 26.084 10.5859 26.084L15.5176 26.084C16.9727 26.084 17.9395 25.1074 17.9395 23.6426L17.9395 17.9199L23.6523 17.9199C25.1172 17.9199 26.1035 16.9531 26.1035 15.498L26.1035 10.5664C26.1035 9.11133 25.1172 8.14453 23.6523 8.14453L17.9395 8.14453L17.9395 2.44141C17.9395 0.976562 16.9727 0 15.5176 0L10.5859 0C9.13086 0 8.16406 0.976562 8.16406 2.44141L8.16406 8.14453L2.44141 8.14453C0.976562 8.14453 0 9.11133 0 10.5664Z", fillAlpha = 0.85f)
        }
        return _sFCrossFill!!
    }

private var _sFCrossFill: ImageVector? = null
