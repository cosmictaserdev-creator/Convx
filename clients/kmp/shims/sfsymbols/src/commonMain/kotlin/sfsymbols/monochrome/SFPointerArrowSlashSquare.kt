package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFPointerArrowSlashSquare (monochrome)
 * Viewport: 23.3203 x 22.959
 */
public val SfSymbols.Monochrome.SFPointerArrowSlashSquare: ImageVector
    get() {
        if (_sFPointerArrowSlashSquare != null) {
            return _sFPointerArrowSlashSquare!!
        }
        _sFPointerArrowSlashSquare = sfIcon(
            name = "Monochrome.SFPointerArrowSlashSquare",
            viewportWidth = 23.3203f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M22.959 3.76953L22.959 19.1992C22.959 21.6797 21.6797 22.959 19.1504 22.959L3.79883 22.959C1.2793 22.959 0 21.6992 0 19.1992L0 3.76953C0 1.26953 1.2793 0 3.79883 0L19.1504 0C21.6797 0 22.959 1.2793 22.959 3.76953ZM3.83789 1.72852C2.4707 1.72852 1.72852 2.46094 1.72852 3.85742L1.72852 19.1016C1.72852 20.5078 2.4707 21.2305 3.83789 21.2305L19.1211 21.2305C20.459 21.2305 21.2305 20.5078 21.2305 19.1016L21.2305 3.85742C21.2305 2.46094 20.459 1.72852 19.1211 1.72852Z", fillAlpha = 0.85f)
            addSfPath("M14.0477 15.9325L14.5703 17.1484C14.7852 17.666 14.541 18.252 14.0332 18.457C13.5059 18.6719 12.9102 18.4277 12.7051 17.9199L10.7715 13.1641L8.82812 15.1367C8.53516 15.4297 8.00781 15.2734 8.01758 14.834L8.0366 9.92137ZM8.81836 4.44336L15.7129 11.6602C16.0156 11.9922 15.8008 12.4219 15.3711 12.4316L14.3954 12.452L8.05137 6.10801L8.05664 4.74609C8.06641 4.32617 8.52539 4.15039 8.81836 4.44336Z", fillAlpha = 0.85f)
            addSfPath("M17.0605 17.7441C17.2559 17.9492 17.5879 17.9492 17.793 17.7441C17.998 17.5391 17.998 17.2266 17.793 17.0117L5.9668 5.18555C5.76172 4.98047 5.42969 4.98047 5.22461 5.18555C5.01953 5.39062 5.0293 5.72266 5.22461 5.91797Z", fillAlpha = 0.85f)
        }
        return _sFPointerArrowSlashSquare!!
    }

private var _sFPointerArrowSlashSquare: ImageVector? = null
