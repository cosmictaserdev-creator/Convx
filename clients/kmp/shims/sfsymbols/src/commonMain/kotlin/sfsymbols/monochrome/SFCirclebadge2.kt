package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFCirclebadge2 (monochrome)
 * Viewport: 26.2402 x 19.3262
 */
public val SfSymbols.Monochrome.SFCirclebadge2: ImageVector
    get() {
        if (_sFCirclebadge2 != null) {
            return _sFCirclebadge2!!
        }
        _sFCirclebadge2 = sfIcon(
            name = "Monochrome.SFCirclebadge2",
            viewportWidth = 26.2402f,
            viewportHeight = 19.3262f
        ) {
            addSfPath("M25.8789 9.6582C25.8789 14.9805 21.5527 19.3066 16.2305 19.3066C15.0733 19.3066 13.9632 19.1021 12.9395 18.7168C13.808 18.4128 14.6142 17.9782 15.3363 17.4356C15.6289 17.4731 15.9277 17.4902 16.2305 17.4902C20.5566 17.4902 24.0625 13.9844 24.0625 9.6582C24.0625 5.33203 20.5566 1.82617 16.2305 1.82617C15.9301 1.82617 15.6337 1.84307 15.3433 1.87976C14.6194 1.33382 13.8108 0.896691 12.9395 0.591028C13.9632 0.204971 15.0733 0 16.2305 0C21.5527 0 25.8789 4.33594 25.8789 9.6582Z", fillAlpha = 0.85f)
            addSfPath("M9.64844 19.3066C14.9707 19.3066 19.2969 14.9805 19.2969 9.6582C19.2969 4.33594 14.9707 0 9.64844 0C4.32617 0 0 4.33594 0 9.6582C0 14.9805 4.32617 19.3066 9.64844 19.3066ZM9.64844 17.4902C5.32227 17.4902 1.81641 13.9844 1.81641 9.6582C1.81641 5.33203 5.32227 1.82617 9.64844 1.82617C13.9746 1.82617 17.4805 5.33203 17.4805 9.6582C17.4805 13.9844 13.9746 17.4902 9.64844 17.4902Z", fillAlpha = 0.85f)
        }
        return _sFCirclebadge2!!
    }

private var _sFCirclebadge2: ImageVector? = null
