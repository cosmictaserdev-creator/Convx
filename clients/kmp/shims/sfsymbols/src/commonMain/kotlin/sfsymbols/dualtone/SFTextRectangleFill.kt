package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFTextRectangleFill (dualtone)
 * Viewport: 29.9512 x 22.959
 */
public val SfSymbols.Dualtone.SFTextRectangleFill: ImageVector
    get() {
        if (_sFTextRectangleFill != null) {
            return _sFTextRectangleFill!!
        }
        _sFTextRectangleFill = sfIcon(
            name = "Dualtone.SFTextRectangleFill",
            viewportWidth = 29.9512f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M3.79883 22.959L25.7812 22.959C28.3105 22.959 29.5898 21.6797 29.5898 19.1992L29.5898 3.76953C29.5898 1.2793 28.3105 0 25.7812 0L3.79883 0C1.2793 0 0 1.26953 0 3.76953L0 19.1992C0 21.6992 1.2793 22.959 3.79883 22.959Z", fillAlpha = 0.2125f)
            addSfPath("M5.43945 6.86523C5.0293 6.86523 4.72656 6.55273 4.72656 6.16211C4.72656 5.77148 5.0293 5.46875 5.43945 5.46875L24.1602 5.46875C24.5605 5.46875 24.8633 5.77148 24.8633 6.16211C24.8633 6.55273 24.5605 6.86523 24.1602 6.86523ZM5.43945 12.0605C5.0293 12.0605 4.72656 11.748 4.72656 11.3574C4.72656 10.9766 5.0293 10.6738 5.43945 10.6738L14.8828 10.6738C15.2832 10.6738 15.5859 10.9766 15.5859 11.3574C15.5859 11.748 15.2832 12.0605 14.8828 12.0605Z", fillAlpha = 0.85f)
        }
        return _sFTextRectangleFill!!
    }

private var _sFTextRectangleFill: ImageVector? = null
