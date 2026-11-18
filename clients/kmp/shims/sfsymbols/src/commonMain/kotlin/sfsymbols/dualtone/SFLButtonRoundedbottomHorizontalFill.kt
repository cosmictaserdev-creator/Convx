package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFLButtonRoundedbottomHorizontalFill (dualtone)
 * Viewport: 28.9941 x 21.6504
 */
public val SfSymbols.Dualtone.SFLButtonRoundedbottomHorizontalFill: ImageVector
    get() {
        if (_sFLButtonRoundedbottomHorizontalFill != null) {
            return _sFLButtonRoundedbottomHorizontalFill!!
        }
        _sFLButtonRoundedbottomHorizontalFill = sfIcon(
            name = "Dualtone.SFLButtonRoundedbottomHorizontalFill",
            viewportWidth = 28.9941f,
            viewportHeight = 21.6504f
        ) {
            addSfPath("M10.2441 21.6309L18.3887 21.6309C25.5371 21.6309 28.6328 18.3203 28.6328 11.6406L28.6328 3.67188C28.6328 1.25977 27.373 0 24.9316 0L3.71094 0C1.25977 0 0 1.25 0 3.67188L0 11.6406C0 18.3203 3.10547 21.6309 10.2441 21.6309Z", fillAlpha = 0.2125f)
            addSfPath("M12.2168 16.0645C11.6602 16.0645 11.3281 15.6641 11.3281 15.0879L11.3281 6.41602C11.3281 5.83984 11.6504 5.41992 12.2168 5.41992C12.7734 5.41992 13.1445 5.83008 13.1445 6.41602L13.1445 14.5312L17.3242 14.5312C17.7441 14.5312 18.1152 14.8633 18.1152 15.3027C18.1152 15.752 17.7539 16.0645 17.3242 16.0645Z", fillAlpha = 0.85f)
        }
        return _sFLButtonRoundedbottomHorizontalFill!!
    }

private var _sFLButtonRoundedbottomHorizontalFill: ImageVector? = null
