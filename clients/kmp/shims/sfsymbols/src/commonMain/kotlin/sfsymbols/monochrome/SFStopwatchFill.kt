package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFStopwatchFill (monochrome)
 * Viewport: 25.8008 x 30.7129
 */
public val SfSymbols.Monochrome.SFStopwatchFill: ImageVector
    get() {
        if (_sFStopwatchFill != null) {
            return _sFStopwatchFill!!
        }
        _sFStopwatchFill = sfIcon(
            name = "Monochrome.SFStopwatchFill",
            viewportWidth = 25.8008f,
            viewportHeight = 30.7129f
        ) {
            addSfPath("M13.8867 1.21094L13.8867 2.68776C16.5985 2.93919 19.0788 4.0667 21.0325 5.78653L22.168 4.6582C22.4316 4.4043 22.7051 4.29688 23.0664 4.29688C23.7109 4.29688 24.209 4.79492 24.209 5.41016C24.209 5.71289 24.0625 6.04492 23.8086 6.29883L22.6498 7.47198C24.3943 9.64295 25.4395 12.3885 25.4395 15.3516C25.4395 22.3047 19.6777 28.0664 12.7148 28.0664C5.76172 28.0664 0 22.3047 0 15.3516C0 8.78446 5.11663 3.28585 11.5332 2.68735L11.5332 1.21094C11.5332 0.546875 12.0605 0 12.7051 0C13.3691 0 13.8867 0.546875 13.8867 1.21094ZM11.9531 7.05078L11.9531 13.6621C11.2793 14.0039 10.8496 14.6289 10.8496 15.3613C10.8496 16.3965 11.6699 17.2363 12.7148 17.2363C13.75 17.2363 14.5703 16.3965 14.5703 15.3613C14.5703 14.6289 14.1602 14.0039 13.4668 13.6719L13.4668 7.05078C13.4668 6.63086 13.1348 6.30859 12.7051 6.30859C12.2949 6.30859 11.9531 6.63086 11.9531 7.05078Z", fillAlpha = 0.85f)
        }
        return _sFStopwatchFill!!
    }

private var _sFStopwatchFill: ImageVector? = null
