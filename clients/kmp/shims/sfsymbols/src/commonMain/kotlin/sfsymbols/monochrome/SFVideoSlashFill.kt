package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFVideoSlashFill (monochrome)
 * Viewport: 36.9971 x 29.4776
 */
public val SfSymbols.Monochrome.SFVideoSlashFill: ImageVector
    get() {
        if (_sFVideoSlashFill != null) {
            return _sFVideoSlashFill!!
        }
        _sFVideoSlashFill = sfIcon(
            name = "Monochrome.SFVideoSlashFill",
            viewportWidth = 36.9971f,
            viewportHeight = 29.4776f
        ) {
            addSfPath("M21.5619 24.939L7.9126 24.939C5.34424 24.939 3.8501 23.4839 3.8501 20.9253L3.8501 8.53273C3.8501 8.1141 3.89277 7.72498 3.97913 7.37011ZM34.6606 8.19094L34.6606 21.2671C34.6606 22.273 34.0552 22.937 33.1372 22.937C32.6685 22.937 32.1997 22.7027 31.7603 22.3316L27.1899 18.4448L27.1899 11.0034L31.7603 7.13625C32.1997 6.76516 32.6685 6.53078 33.1372 6.53078C34.0552 6.53078 34.6606 7.19484 34.6606 8.19094ZM25.6372 8.53273L25.6372 20.9253C25.6372 21.3583 25.5944 21.7597 25.5074 22.1249L7.90464 4.52912C7.90717 4.52883 7.90988 4.52883 7.9126 4.52883L21.6724 4.52883C24.2407 4.52883 25.6372 5.98391 25.6372 8.53273Z", fillAlpha = 0.85f)
            addSfPath("M26.4966 27.6734C26.8286 27.9956 27.3657 27.9956 27.6782 27.6734C28.0005 27.3413 28.0005 26.8237 27.6782 26.4917L2.98096 1.81398C2.66846 1.49172 2.13135 1.48195 1.79932 1.81398C1.47705 2.12648 1.47705 2.67336 1.79932 2.98586Z", fillAlpha = 0.85f)
        }
        return _sFVideoSlashFill!!
    }

private var _sFVideoSlashFill: ImageVector? = null
