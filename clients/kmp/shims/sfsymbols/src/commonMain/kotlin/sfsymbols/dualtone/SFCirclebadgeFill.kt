package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFCirclebadgeFill (dualtone)
 * Viewport: 19.6582 x 19.3262
 */
public val SfSymbols.Dualtone.SFCirclebadgeFill: ImageVector
    get() {
        if (_sFCirclebadgeFill != null) {
            return _sFCirclebadgeFill!!
        }
        _sFCirclebadgeFill = sfIcon(
            name = "Dualtone.SFCirclebadgeFill",
            viewportWidth = 19.6582f,
            viewportHeight = 19.3262f
        ) {
            addSfPath("M9.64844 19.3066C14.9707 19.3066 19.2969 14.9805 19.2969 9.6582C19.2969 4.33594 14.9707 0 9.64844 0C4.32617 0 0 4.33594 0 9.6582C0 14.9805 4.32617 19.3066 9.64844 19.3066Z", fillAlpha = 0.85f)
        }
        return _sFCirclebadgeFill!!
    }

private var _sFCirclebadgeFill: ImageVector? = null
