package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFBeatsPill (dualtone)
 * Viewport: 34.209 x 14.502
 */
public val SfSymbols.Dualtone.SFBeatsPill: ImageVector
    get() {
        if (_sFBeatsPill != null) {
            return _sFBeatsPill!!
        }
        _sFBeatsPill = sfIcon(
            name = "Dualtone.SFBeatsPill",
            viewportWidth = 34.209f,
            viewportHeight = 14.502f
        ) {
            addSfPath("M7.23633 0C3.22266 0 0 3.23242 0 7.24609C0 11.2598 3.22266 14.4824 7.23633 14.4824L26.6016 14.4824C30.6152 14.4824 33.8477 11.2598 33.8477 7.24609C33.8477 3.23242 30.6152 0 26.6016 0ZM7.23633 1.72852L26.6016 1.72852C29.7559 1.72852 32.1191 4.0918 32.1191 7.24609C32.1191 10.3906 29.7559 12.7539 26.6016 12.7539L7.23633 12.7539C4.0918 12.7539 1.72852 10.3906 1.72852 7.24609C1.72852 4.0918 4.0918 1.72852 7.23633 1.72852ZM16.9238 10.4688C18.3105 10.4688 19.4629 9.33594 19.4629 7.98828C19.4629 6.57227 18.3008 5.41016 16.9238 5.41016C16.416 5.41016 15.957 5.54688 15.5469 5.85938L15.5469 3.84766L14.3945 4.3457L14.3945 7.98828C14.3945 9.36523 15.498 10.4688 16.9238 10.4688ZM16.9238 9.33594C16.1719 9.33594 15.5469 8.71094 15.5469 7.98828C15.5469 7.22656 16.1816 6.60156 16.9238 6.60156C17.6758 6.60156 18.3008 7.23633 18.3008 7.98828C18.3008 8.71094 17.6758 9.33594 16.9238 9.33594Z", fillAlpha = 0.85f)
        }
        return _sFBeatsPill!!
    }

private var _sFBeatsPill: ImageVector? = null
