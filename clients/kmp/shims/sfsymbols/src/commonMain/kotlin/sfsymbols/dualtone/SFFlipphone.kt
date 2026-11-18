package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFFlipphone (dualtone)
 * Viewport: 17.3633 x 25.2051
 */
public val SfSymbols.Dualtone.SFFlipphone: ImageVector
    get() {
        if (_sFFlipphone != null) {
            return _sFFlipphone!!
        }
        _sFFlipphone = sfIcon(
            name = "Dualtone.SFFlipphone",
            viewportWidth = 17.3633f,
            viewportHeight = 25.2051f
        ) {
            addSfPath("M8.50586 25.2051C13.8086 25.2051 17.002 22.207 17.002 16.9043L17.002 6.83594C17.002 4.84375 15.752 3.62305 13.6719 3.62305L1.72852 3.62305L1.72852 0.869141C1.72852 0.390625 1.33789 0 0.869141 0C0.390625 0 0 0.390625 0 0.869141L0 16.9043C0 22.207 3.19336 25.2051 8.50586 25.2051ZM8.50586 23.5156C4.29688 23.5156 1.72852 21.1816 1.72852 17.041L1.72852 5.35156L13.4668 5.35156C14.6387 5.35156 15.2734 5.95703 15.2734 7.09961L15.2734 17.041C15.2734 21.1816 12.7051 23.5156 8.50586 23.5156ZM4.29688 15.1074L12.7051 15.1074C13.457 15.1074 13.8965 14.6777 13.8965 13.9453L13.8965 7.90039C13.8965 7.1582 13.457 6.72852 12.7051 6.72852L4.29688 6.72852C3.54492 6.72852 3.10547 7.1582 3.10547 7.90039L3.10547 13.9453C3.10547 14.6777 3.54492 15.1074 4.29688 15.1074ZM6.67969 21.377L10.2832 21.377C10.7129 21.377 11.0645 21.0254 11.0645 20.5957C11.0645 20.166 10.7129 19.8145 10.2832 19.8145L6.67969 19.8145C6.24023 19.8145 5.89844 20.166 5.89844 20.5957C5.89844 21.0254 6.24023 21.377 6.67969 21.377Z", fillAlpha = 0.85f)
        }
        return _sFFlipphone!!
    }

private var _sFFlipphone: ImageVector? = null
