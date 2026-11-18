package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFTagCircleFill (dualtone)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Dualtone.SFTagCircleFill: ImageVector
    get() {
        if (_sFTagCircleFill != null) {
            return _sFTagCircleFill!!
        }
        _sFTagCircleFill = sfIcon(
            name = "Dualtone.SFTagCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.459C19.7266 25.459 25.4395 19.7461 25.4395 12.7344C25.4395 5.73242 19.7266 0.0195312 12.7148 0.0195312C5.71289 0.0195312 0 5.73242 0 12.7344C0 19.7461 5.71289 25.459 12.7148 25.459Z", fillAlpha = 0.2125f)
            addSfPath("M10.2051 19.4629L6.20117 15.4492C5.49805 14.7461 5.48828 14.043 6.2207 13.3105L12.5 6.96289C12.9785 6.48438 13.3203 6.14258 14.0527 6.14258L16.5918 6.14258C17.1484 6.14258 17.4707 6.37695 17.8516 6.75781L19.0039 7.87109C19.3848 8.25195 19.5312 8.57422 19.5312 9.15039L19.5312 11.5918C19.5312 12.3535 19.1895 12.6855 18.7207 13.1543L12.3633 19.4531C11.6309 20.1855 10.918 20.166 10.2051 19.4629ZM15.127 10.5469C15.459 10.8789 16.0156 10.8887 16.3574 10.5371C16.6992 10.1855 16.709 9.62891 16.3574 9.29688C16.0156 8.95508 15.459 8.95508 15.127 9.29688C14.7754 9.6582 14.7754 10.2148 15.127 10.5469Z", fillAlpha = 0.85f)
        }
        return _sFTagCircleFill!!
    }

private var _sFTagCircleFill: ImageVector? = null
