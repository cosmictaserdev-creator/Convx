package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFMetronomeFill (monochrome)
 * Viewport: 27.1094 x 24.0723
 */
public val SfSymbols.Monochrome.SFMetronomeFill: ImageVector
    get() {
        if (_sFMetronomeFill != null) {
            return _sFMetronomeFill!!
        }
        _sFMetronomeFill = sfIcon(
            name = "Monochrome.SFMetronomeFill",
            viewportWidth = 27.1094f,
            viewportHeight = 24.0723f
        ) {
            addSfPath("M26.0352 18.8867C26.3574 19.4434 26.5234 20.0488 26.5234 20.6348C26.5234 22.4414 25.293 23.8672 23.252 23.8672L3.49609 23.8672C1.46484 23.8672 0.234375 22.4414 0.234375 20.6348C0.234375 20.0488 0.390625 19.4531 0.722656 18.8867L1.242 17.9785L25.5158 17.9785ZM9.52987 16.543L2.06289 16.543L4.77772 11.7954ZM16.1621 1.62109L24.6949 16.543L16.3944 16.543L14.2187 14.3704L14.2188 5.50781C14.2188 4.98047 13.9062 4.62891 13.3691 4.62891C12.832 4.62891 12.5098 4.98047 12.5098 5.50781L12.5098 12.6639L7.27194 7.43357L10.5957 1.62109C11.2109 0.537109 12.2852 0 13.3789 0C14.4629 0 15.5469 0.537109 16.1621 1.62109Z", fillAlpha = 0.85f)
            addSfPath("M11.9336 16.7383L14.3555 16.7188L3.10547 5.47852C2.74414 5.10742 2.26562 5.10742 1.88477 5.46875C1.51367 5.84961 1.5332 6.34766 1.89453 6.70898Z", fillAlpha = 0.85f)
        }
        return _sFMetronomeFill!!
    }

private var _sFMetronomeFill: ImageVector? = null
