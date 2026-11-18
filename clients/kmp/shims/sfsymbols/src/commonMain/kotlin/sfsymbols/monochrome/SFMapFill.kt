package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFMapFill (monochrome)
 * Viewport: 26.0352 x 24.4434
 */
public val SfSymbols.Monochrome.SFMapFill: ImageVector
    get() {
        if (_sFMapFill != null) {
            return _sFMapFill!!
        }
        _sFMapFill = sfIcon(
            name = "Monochrome.SFMapFill",
            viewportWidth = 26.0352f,
            viewportHeight = 24.4434f
        ) {
            addSfPath("M0 22.6562C0 23.4961 0.488281 23.9746 1.30859 23.9746C1.60156 23.9746 1.9043 23.8867 2.26562 23.6816L7.74414 20.7031L7.74414 0.15625C7.50977 0.253906 7.25586 0.380859 7.01172 0.517578L1.01562 4.01367C0.332031 4.39453 0 4.92188 0 5.70312ZM9.29688 20.5371L15.6152 24.1602C15.8105 24.2676 16.0059 24.3555 16.1914 24.4043L16.1914 4.07227L10.0293 0.302734C9.80469 0.166016 9.55078 0.0585938 9.29688 0ZM17.7539 24.3652C17.8906 24.3164 18.0371 24.2578 18.1738 24.1699L24.668 20.4492C25.3418 20.0586 25.6738 19.541 25.6738 18.75L25.6738 1.78711C25.6738 0.9375 25.1758 0.46875 24.375 0.46875C24.082 0.46875 23.7793 0.556641 23.418 0.761719L17.7539 3.88672Z", fillAlpha = 0.85f)
        }
        return _sFMapFill!!
    }

private var _sFMapFill: ImageVector? = null
