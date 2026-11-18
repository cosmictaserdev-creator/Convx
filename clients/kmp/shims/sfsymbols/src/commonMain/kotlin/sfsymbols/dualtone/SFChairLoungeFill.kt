package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFChairLoungeFill (dualtone)
 * Viewport: 24.4531 x 21.3965
 */
public val SfSymbols.Dualtone.SFChairLoungeFill: ImageVector
    get() {
        if (_sFChairLoungeFill != null) {
            return _sFChairLoungeFill!!
        }
        _sFChairLoungeFill = sfIcon(
            name = "Dualtone.SFChairLoungeFill",
            viewportWidth = 24.4531f,
            viewportHeight = 21.3965f
        ) {
            addSfPath("M7.04102 11.5918L17.0508 11.5918L17.0508 8.68164C17.0508 6.60156 18.4766 5.05859 20.4883 5.05859L20.6738 5.05859L20.6738 2.65625C20.6738 1.05469 19.6484 0 18.125 0L5.9668 0C4.44336 0 3.41797 1.05469 3.41797 2.65625L3.41797 5.05859L3.60352 5.05859C5.61523 5.05859 7.04102 6.60156 7.04102 8.68164ZM0 16.3281C0 17.793 0.986328 18.7695 2.44141 18.7695L21.6602 18.7695C23.1055 18.7695 24.0918 17.793 24.0918 16.3281L24.0918 8.68164C24.0918 7.2168 23.1055 6.24023 21.6602 6.24023L20.8496 6.24023C19.4043 6.24023 18.4082 7.2168 18.4082 8.68164L18.4082 12.9492L5.68359 12.9492L5.68359 8.68164C5.68359 7.2168 4.69727 6.24023 3.24219 6.24023L2.44141 6.24023C0.986328 6.24023 0 7.2168 0 8.68164ZM4.20898 21.377L4.94141 21.377C5.57617 21.377 5.98633 20.9863 5.98633 20.3418L5.98633 17.8613L3.17383 17.8613L3.17383 20.3418C3.17383 20.9863 3.56445 21.377 4.20898 21.377ZM19.1504 21.377L19.8828 21.377C20.5273 21.377 20.918 20.9863 20.918 20.3418L20.918 17.8613L18.1152 17.8613L18.1152 20.3418C18.1152 20.9863 18.5156 21.377 19.1504 21.377Z", fillAlpha = 0.85f)
        }
        return _sFChairLoungeFill!!
    }

private var _sFChairLoungeFill: ImageVector? = null
