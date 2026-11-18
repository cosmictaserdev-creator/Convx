package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFLockRectangleFill (monochrome)
 * Viewport: 29.9512 x 22.959
 */
public val SfSymbols.Monochrome.SFLockRectangleFill: ImageVector
    get() {
        if (_sFLockRectangleFill != null) {
            return _sFLockRectangleFill!!
        }
        _sFLockRectangleFill = sfIcon(
            name = "Monochrome.SFLockRectangleFill",
            viewportWidth = 29.9512f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M29.5898 3.76953L29.5898 19.1992C29.5898 21.6797 28.3105 22.959 25.7812 22.959L3.79883 22.959C1.2793 22.959 0 21.6992 0 19.1992L0 3.76953C0 1.26953 1.2793 0 3.79883 0L25.7812 0C28.3105 0 29.5898 1.2793 29.5898 3.76953ZM11.3184 8.38867L11.3184 9.99023C10.5176 10.0293 10.127 10.4785 10.127 11.4258L10.127 16.6309C10.127 17.6172 10.5566 18.0664 11.4746 18.0664L18.125 18.0664C19.043 18.0664 19.4727 17.6172 19.4727 16.6309L19.4727 11.4258C19.4727 10.4785 19.082 10.0293 18.2812 9.99023L18.2812 8.38867C18.2812 6.06445 16.875 4.52148 14.8047 4.52148C12.7246 4.52148 11.3184 6.06445 11.3184 8.38867ZM17.0703 8.27148L17.0703 9.98047L12.5293 9.98047L12.5293 8.27148C12.5293 6.71875 13.4375 5.68359 14.8047 5.68359C16.1621 5.68359 17.0703 6.71875 17.0703 8.27148Z", fillAlpha = 0.85f)
        }
        return _sFLockRectangleFill!!
    }

private var _sFLockRectangleFill: ImageVector? = null
