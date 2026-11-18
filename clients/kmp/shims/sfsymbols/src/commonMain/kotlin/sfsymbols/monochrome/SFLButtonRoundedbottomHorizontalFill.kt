package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFLButtonRoundedbottomHorizontalFill (monochrome)
 * Viewport: 28.9941 x 21.6504
 */
public val SfSymbols.Monochrome.SFLButtonRoundedbottomHorizontalFill: ImageVector
    get() {
        if (_sFLButtonRoundedbottomHorizontalFill != null) {
            return _sFLButtonRoundedbottomHorizontalFill!!
        }
        _sFLButtonRoundedbottomHorizontalFill = sfIcon(
            name = "Monochrome.SFLButtonRoundedbottomHorizontalFill",
            viewportWidth = 28.9941f,
            viewportHeight = 21.6504f
        ) {
            addSfPath("M28.6328 3.67188L28.6328 11.6406C28.6328 18.3203 25.5371 21.6309 18.3887 21.6309L10.2441 21.6309C3.10547 21.6309 0 18.3203 0 11.6406L0 3.67188C0 1.25 1.25977 0 3.71094 0L24.9316 0C27.373 0 28.6328 1.25977 28.6328 3.67188ZM11.3281 6.41602L11.3281 15.0879C11.3281 15.6641 11.6602 16.0645 12.2168 16.0645L17.3242 16.0645C17.7539 16.0645 18.1152 15.752 18.1152 15.3027C18.1152 14.8633 17.7441 14.5312 17.3242 14.5312L13.1445 14.5312L13.1445 6.41602C13.1445 5.83008 12.7734 5.41992 12.2168 5.41992C11.6504 5.41992 11.3281 5.83984 11.3281 6.41602Z", fillAlpha = 0.85f)
        }
        return _sFLButtonRoundedbottomHorizontalFill!!
    }

private var _sFLButtonRoundedbottomHorizontalFill: ImageVector? = null
