package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFTagSquare (monochrome)
 * Viewport: 23.3203 x 22.959
 */
public val SfSymbols.Monochrome.SFTagSquare: ImageVector
    get() {
        if (_sFTagSquare != null) {
            return _sFTagSquare!!
        }
        _sFTagSquare = sfIcon(
            name = "Monochrome.SFTagSquare",
            viewportWidth = 23.3203f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M3.79883 22.959L19.1504 22.959C21.6797 22.959 22.959 21.6797 22.959 19.1992L22.959 3.76953C22.959 1.2793 21.6797 0 19.1504 0L3.79883 0C1.2793 0 0 1.26953 0 3.76953L0 19.1992C0 21.6992 1.2793 22.959 3.79883 22.959ZM3.83789 21.2305C2.4707 21.2305 1.72852 20.5078 1.72852 19.1016L1.72852 3.85742C1.72852 2.46094 2.4707 1.72852 3.83789 1.72852L19.1211 1.72852C20.459 1.72852 21.2305 2.46094 21.2305 3.85742L21.2305 19.1016C21.2305 20.5078 20.459 21.2305 19.1211 21.2305Z", fillAlpha = 0.85f)
            addSfPath("M9.04297 18.0469C9.72656 18.7402 10.4199 18.75 11.1328 18.0371L17.3438 11.875C17.8027 11.4258 18.1348 11.1035 18.1348 10.3711L18.1348 7.96875C18.1348 7.42188 17.9883 7.10938 17.6172 6.73828L16.4941 5.63477C16.123 5.27344 15.8203 5.04883 15.2637 5.04883L12.8027 5.04883C12.0801 5.04883 11.7383 5.38086 11.2891 5.83984L5.13672 12.041C4.43359 12.7539 4.44336 13.4375 5.12695 14.1211ZM13.8379 9.33594C13.5059 9.01367 13.5059 8.4668 13.8379 8.11523C14.1602 7.7832 14.7168 7.7832 15.0488 8.11523C15.3809 8.44727 15.3809 8.98438 15.0391 9.32617C14.707 9.67773 14.1699 9.66797 13.8379 9.33594Z", fillAlpha = 0.85f)
        }
        return _sFTagSquare!!
    }

private var _sFTagSquare: ImageVector? = null
