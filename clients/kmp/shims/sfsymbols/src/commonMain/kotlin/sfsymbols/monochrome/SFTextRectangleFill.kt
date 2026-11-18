package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFTextRectangleFill (monochrome)
 * Viewport: 29.9512 x 22.959
 */
public val SfSymbols.Monochrome.SFTextRectangleFill: ImageVector
    get() {
        if (_sFTextRectangleFill != null) {
            return _sFTextRectangleFill!!
        }
        _sFTextRectangleFill = sfIcon(
            name = "Monochrome.SFTextRectangleFill",
            viewportWidth = 29.9512f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M29.5898 3.76953L29.5898 19.1992C29.5898 21.6797 28.3105 22.959 25.7812 22.959L3.79883 22.959C1.2793 22.959 0 21.6992 0 19.1992L0 3.76953C0 1.26953 1.2793 0 3.79883 0L25.7812 0C28.3105 0 29.5898 1.2793 29.5898 3.76953ZM5.43945 10.6738C5.0293 10.6738 4.72656 10.9766 4.72656 11.3574C4.72656 11.748 5.0293 12.0605 5.43945 12.0605L14.8828 12.0605C15.2832 12.0605 15.5859 11.748 15.5859 11.3574C15.5859 10.9766 15.2832 10.6738 14.8828 10.6738ZM5.43945 5.46875C5.0293 5.46875 4.72656 5.77148 4.72656 6.16211C4.72656 6.55273 5.0293 6.86523 5.43945 6.86523L24.1602 6.86523C24.5605 6.86523 24.8633 6.55273 24.8633 6.16211C24.8633 5.77148 24.5605 5.46875 24.1602 5.46875Z", fillAlpha = 0.85f)
        }
        return _sFTextRectangleFill!!
    }

private var _sFTextRectangleFill: ImageVector? = null
