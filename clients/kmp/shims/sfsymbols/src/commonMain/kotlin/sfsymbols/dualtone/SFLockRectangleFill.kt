package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFLockRectangleFill (dualtone)
 * Viewport: 29.9512 x 22.959
 */
public val SfSymbols.Dualtone.SFLockRectangleFill: ImageVector
    get() {
        if (_sFLockRectangleFill != null) {
            return _sFLockRectangleFill!!
        }
        _sFLockRectangleFill = sfIcon(
            name = "Dualtone.SFLockRectangleFill",
            viewportWidth = 29.9512f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M3.79883 22.959L25.7812 22.959C28.3105 22.959 29.5898 21.6797 29.5898 19.1992L29.5898 3.76953C29.5898 1.2793 28.3105 0 25.7812 0L3.79883 0C1.2793 0 0 1.26953 0 3.76953L0 19.1992C0 21.6992 1.2793 22.959 3.79883 22.959Z", fillAlpha = 0.2125f)
            addSfPath("M10.127 16.6309L10.127 11.4258C10.127 10.4785 10.5176 10.0293 11.3184 9.99023L11.3184 8.38867C11.3184 6.06445 12.7246 4.52148 14.8047 4.52148C16.875 4.52148 18.2812 6.06445 18.2812 8.38867L18.2812 9.99023C19.082 10.0293 19.4727 10.4785 19.4727 11.4258L19.4727 16.6309C19.4727 17.6172 19.043 18.0664 18.125 18.0664L11.4746 18.0664C10.5566 18.0664 10.127 17.6172 10.127 16.6309ZM12.5293 9.98047L17.0703 9.98047L17.0703 8.27148C17.0703 6.71875 16.1621 5.68359 14.8047 5.68359C13.4375 5.68359 12.5293 6.71875 12.5293 8.27148Z", fillAlpha = 0.85f)
        }
        return _sFLockRectangleFill!!
    }

private var _sFLockRectangleFill: ImageVector? = null
