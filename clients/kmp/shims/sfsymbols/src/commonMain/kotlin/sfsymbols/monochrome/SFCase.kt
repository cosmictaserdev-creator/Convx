package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFCase (monochrome)
 * Viewport: 29.1504 x 24.1699
 */
public val SfSymbols.Monochrome.SFCase: ImageVector
    get() {
        if (_sFCase != null) {
            return _sFCase!!
        }
        _sFCase = sfIcon(
            name = "Monochrome.SFCase",
            viewportWidth = 29.1504f,
            viewportHeight = 24.1699f
        ) {
            addSfPath("M3.70117 24.1602L25.0879 24.1602C27.5684 24.1602 28.7891 22.959 28.7891 20.5078L28.7891 8.13477C28.7891 5.67383 27.5684 4.47266 25.0879 4.47266L3.70117 4.47266C1.23047 4.47266 0 5.67383 0 8.13477L0 20.5078C0 22.959 1.23047 24.1602 3.70117 24.1602ZM3.7207 22.4414C2.40234 22.4414 1.72852 21.7871 1.72852 20.4297L1.72852 8.21289C1.72852 6.8457 2.40234 6.19141 3.7207 6.19141L25.0684 6.19141C26.3965 6.19141 27.0605 6.8457 27.0605 8.21289L27.0605 20.4297C27.0605 21.7871 26.3965 22.4414 25.0684 22.4414ZM8.125 5.42969L9.79492 5.42969L9.79492 3.10547C9.79492 2.13867 10.3809 1.58203 11.3965 1.58203L17.3828 1.58203C18.4082 1.58203 18.9941 2.13867 18.9941 3.10547L18.9941 5.41016L20.6641 5.41016L20.6641 3.22266C20.6641 1.05469 19.4824 0 17.3535 0L11.4355 0C9.4043 0 8.125 1.05469 8.125 3.22266Z", fillAlpha = 0.85f)
        }
        return _sFCase!!
    }

private var _sFCase: ImageVector? = null
