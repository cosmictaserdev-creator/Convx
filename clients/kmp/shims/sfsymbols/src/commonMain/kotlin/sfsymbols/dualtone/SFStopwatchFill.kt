package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFStopwatchFill (dualtone)
 * Viewport: 25.8008 x 30.7129
 */
public val SfSymbols.Dualtone.SFStopwatchFill: ImageVector
    get() {
        if (_sFStopwatchFill != null) {
            return _sFStopwatchFill!!
        }
        _sFStopwatchFill = sfIcon(
            name = "Dualtone.SFStopwatchFill",
            viewportWidth = 25.8008f,
            viewportHeight = 30.7129f
        ) {
            addSfPath("M12.7148 28.0664C19.6777 28.0664 25.4395 22.3047 25.4395 15.3516C25.4395 8.38867 19.668 2.62695 12.7051 2.62695C5.75195 2.62695 0 8.38867 0 15.3516C0 22.3047 5.76172 28.0664 12.7148 28.0664ZM11.5332 3.29102L13.8867 3.29102L13.8867 1.21094C13.8867 0.546875 13.3691 0 12.7051 0C12.0605 0 11.5332 0.546875 11.5332 1.21094ZM20.6152 6.20117L22.2363 7.89062L23.8086 6.29883C24.0625 6.04492 24.209 5.71289 24.209 5.41016C24.209 4.79492 23.7109 4.29688 23.0664 4.29688C22.7051 4.29688 22.4316 4.4043 22.168 4.6582Z", fillAlpha = 0.2125f)
            addSfPath("M12.7148 17.2363C11.6699 17.2363 10.8496 16.3965 10.8496 15.3613C10.8496 14.6289 11.2793 14.0039 11.9531 13.6621L11.9531 7.05078C11.9531 6.63086 12.2949 6.30859 12.7051 6.30859C13.1348 6.30859 13.4668 6.63086 13.4668 7.05078L13.4668 13.6719C14.1602 14.0039 14.5703 14.6289 14.5703 15.3613C14.5703 16.3965 13.75 17.2363 12.7148 17.2363Z", fillAlpha = 0.85f)
        }
        return _sFStopwatchFill!!
    }

private var _sFStopwatchFill: ImageVector? = null
