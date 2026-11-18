package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFFishFill (monochrome)
 * Viewport: 34.637 x 26.6396
 */
public val SfSymbols.Monochrome.SFFishFill: ImageVector
    get() {
        if (_sFFishFill != null) {
            return _sFFishFill!!
        }
        _sFFishFill = sfIcon(
            name = "Monochrome.SFFishFill",
            viewportWidth = 34.637f,
            viewportHeight = 26.6396f
        ) {
            addSfPath("M0.0333257 20.8735C0.0821538 21.3716 0.365357 21.5962 0.873169 21.5864C3.85169 21.5376 6.34192 20.1411 8.52942 17.5532C10.8634 19.9067 13.5587 21.3032 16.4103 21.9282L15.9708 25.4731C15.8732 26.313 16.3322 26.772 17.299 26.6059C20.3458 26.1372 23.6954 23.9887 25.5118 21.4595C30.0626 20.0923 34.2716 17.27 34.2716 13.8325C34.2716 10.5903 30.5021 7.88523 26.2345 6.42039C24.6134 1.19578 17.6603-1.1968 12.7384 1.53758C12.2501 1.79148 12.2013 2.2114 12.5919 2.72898C13.7247 4.23289 14.1251 5.13133 14.3497 6.34226C12.2599 7.11375 10.297 8.34422 8.52942 10.1216C6.34192 7.53367 3.85169 6.13718 0.873169 6.08836C0.365357 6.07859 0.0821538 6.31297 0.0333257 6.80125C-0.220581 9.40867 1.00012 12.2016 2.88489 13.8325C1.00012 15.4634-0.220581 18.2661 0.0333257 20.8735ZM27.7677 14.5454C27.1036 14.5454 26.5372 13.979 26.5372 13.3149C26.5372 12.6411 27.1036 12.0845 27.7677 12.0845C28.4415 12.0845 28.9982 12.6411 28.9982 13.3149C28.9982 13.979 28.4415 14.5454 27.7677 14.5454Z", fillAlpha = 0.85f)
        }
        return _sFFishFill!!
    }

private var _sFFishFill: ImageVector? = null
