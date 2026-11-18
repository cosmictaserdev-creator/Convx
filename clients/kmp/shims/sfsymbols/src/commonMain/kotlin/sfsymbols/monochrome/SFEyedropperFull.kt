package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFEyedropperFull (monochrome)
 * Viewport: 26.4355 x 26.4941
 */
public val SfSymbols.Monochrome.SFEyedropperFull: ImageVector
    get() {
        if (_sFEyedropperFull != null) {
            return _sFEyedropperFull!!
        }
        _sFEyedropperFull = sfIcon(
            name = "Monochrome.SFEyedropperFull",
            viewportWidth = 26.4355f,
            viewportHeight = 26.4941f
        ) {
            addSfPath("M16.8652 11.3477L15.1367 13.0566C14.6484 13.5449 14.0234 13.7109 13.5254 13.1738C13.0078 12.6074 13.1152 12.0898 13.6621 11.5234L15.3516 9.80469L14.3945 8.85742L4.13086 19.1211C0.292969 22.959 2.08984 22.4219 0 25.3711L1.05469 26.4941C3.91602 24.4238 3.56445 26.4062 7.4707 22.5195L17.7832 12.2656ZM19.6289 13.6133L19.9512 13.2617C20.5371 12.627 20.5762 11.8945 19.9316 11.2695L19.5508 10.8887C21.4551 9.17969 23.5938 8.91602 24.9023 7.59766C26.748 5.72266 26.1523 3.11523 24.8535 1.80664C23.5547 0.498047 20.9668-0.0585938 19.0625 1.75781C17.7246 3.04688 17.4805 5.20508 15.7715 7.10938L15.3906 6.72852C14.7656 6.08398 14.0332 6.12305 13.3984 6.70898L13.0469 7.03125C12.2852 7.74414 12.4414 8.39844 13.0762 9.04297L17.6172 13.584C18.2617 14.2188 18.916 14.375 19.6289 13.6133Z", fillAlpha = 0.85f)
        }
        return _sFEyedropperFull!!
    }

private var _sFEyedropperFull: ImageVector? = null
