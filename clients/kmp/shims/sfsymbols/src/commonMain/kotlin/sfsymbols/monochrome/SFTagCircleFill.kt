package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFTagCircleFill (monochrome)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Monochrome.SFTagCircleFill: ImageVector
    get() {
        if (_sFTagCircleFill != null) {
            return _sFTagCircleFill!!
        }
        _sFTagCircleFill = sfIcon(
            name = "Monochrome.SFTagCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M25.4395 12.7344C25.4395 19.7461 19.7266 25.459 12.7148 25.459C5.71289 25.459 0 19.7461 0 12.7344C0 5.73242 5.71289 0.0195312 12.7148 0.0195312C19.7266 0.0195312 25.4395 5.73242 25.4395 12.7344ZM14.0527 6.14258C13.3203 6.14258 12.9785 6.48438 12.5 6.96289L6.2207 13.3105C5.48828 14.043 5.49805 14.7461 6.20117 15.4492L10.2051 19.4629C10.918 20.166 11.6309 20.1855 12.3633 19.4531L18.7207 13.1543C19.1895 12.6855 19.5312 12.3535 19.5312 11.5918L19.5312 9.15039C19.5312 8.57422 19.3848 8.25195 19.0039 7.87109L17.8516 6.75781C17.4707 6.37695 17.1484 6.14258 16.5918 6.14258ZM16.3574 9.29688C16.709 9.62891 16.6992 10.1855 16.3574 10.5371C16.0156 10.8887 15.459 10.8789 15.127 10.5469C14.7754 10.2148 14.7754 9.6582 15.127 9.29688C15.459 8.95508 16.0156 8.95508 16.3574 9.29688Z", fillAlpha = 0.85f)
        }
        return _sFTagCircleFill!!
    }

private var _sFTagCircleFill: ImageVector? = null
