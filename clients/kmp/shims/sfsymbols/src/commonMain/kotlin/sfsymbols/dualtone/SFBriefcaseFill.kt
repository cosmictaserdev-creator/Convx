package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFBriefcaseFill (dualtone)
 * Viewport: 29.1504 x 24.1699
 */
public val SfSymbols.Dualtone.SFBriefcaseFill: ImageVector
    get() {
        if (_sFBriefcaseFill != null) {
            return _sFBriefcaseFill!!
        }
        _sFBriefcaseFill = sfIcon(
            name = "Dualtone.SFBriefcaseFill",
            viewportWidth = 29.1504f,
            viewportHeight = 24.1699f
        ) {
            addSfPath("M12.4609 15.7422C11.6113 15.7422 11.123 15.2637 11.123 14.4141L11.123 13.4082C7.43164 13.1641 3.76953 12.4219 0 10.9961L0 9.21875C4.58984 11.2207 9.24805 11.9238 14.3945 11.9238C19.5508 11.9238 24.1992 11.2207 28.7891 9.21875L28.7891 10.9961C25.0293 12.4219 21.3672 13.1641 17.666 13.4082L17.666 14.4141C17.666 15.2637 17.1777 15.7422 16.3281 15.7422ZM3.70117 24.1602L25.0879 24.1602C27.5684 24.1602 28.7891 22.959 28.7891 20.5078L28.7891 8.13477C28.7891 5.67383 27.5684 4.47266 25.0879 4.47266L3.70117 4.47266C1.23047 4.47266 0 5.67383 0 8.13477L0 20.5078C0 22.959 1.23047 24.1602 3.70117 24.1602ZM8.125 5.42969L9.79492 5.42969L9.79492 3.10547C9.79492 2.13867 10.3809 1.58203 11.3965 1.58203L17.3828 1.58203C18.4082 1.58203 18.9941 2.13867 18.9941 3.10547L18.9941 5.41016L20.6641 5.41016L20.6641 3.22266C20.6641 1.05469 19.4824 0 17.3535 0L11.4355 0C9.4043 0 8.125 1.05469 8.125 3.22266Z", fillAlpha = 0.85f)
        }
        return _sFBriefcaseFill!!
    }

private var _sFBriefcaseFill: ImageVector? = null
