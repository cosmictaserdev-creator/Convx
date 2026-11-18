package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFCirclebadge (monochrome)
 * Viewport: 19.6582 x 19.3262
 */
public val SfSymbols.Monochrome.SFCirclebadge: ImageVector
    get() {
        if (_sFCirclebadge != null) {
            return _sFCirclebadge!!
        }
        _sFCirclebadge = sfIcon(
            name = "Monochrome.SFCirclebadge",
            viewportWidth = 19.6582f,
            viewportHeight = 19.3262f
        ) {
            addSfPath("M9.64844 19.3066C14.9707 19.3066 19.2969 14.9805 19.2969 9.6582C19.2969 4.33594 14.9707 0 9.64844 0C4.32617 0 0 4.33594 0 9.6582C0 14.9805 4.32617 19.3066 9.64844 19.3066ZM9.64844 17.4902C5.32227 17.4902 1.81641 13.9844 1.81641 9.6582C1.81641 5.33203 5.32227 1.82617 9.64844 1.82617C13.9746 1.82617 17.4805 5.33203 17.4805 9.6582C17.4805 13.9844 13.9746 17.4902 9.64844 17.4902Z", fillAlpha = 0.85f)
        }
        return _sFCirclebadge!!
    }

private var _sFCirclebadge: ImageVector? = null
