package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFCheckmarkRectangleFill (dualtone)
 * Viewport: 29.9512 x 22.959
 */
public val SfSymbols.Dualtone.SFCheckmarkRectangleFill: ImageVector
    get() {
        if (_sFCheckmarkRectangleFill != null) {
            return _sFCheckmarkRectangleFill!!
        }
        _sFCheckmarkRectangleFill = sfIcon(
            name = "Dualtone.SFCheckmarkRectangleFill",
            viewportWidth = 29.9512f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M3.79883 22.959L25.7812 22.959C28.3105 22.959 29.5898 21.6797 29.5898 19.1992L29.5898 3.76953C29.5898 1.2793 28.3105 0 25.7812 0L3.79883 0C1.2793 0 0 1.26953 0 3.76953L0 19.1992C0 21.6992 1.2793 22.959 3.79883 22.959Z", fillAlpha = 0.2125f)
            addSfPath("M13.4082 17.6074C13.0371 17.6074 12.7441 17.4512 12.4609 17.0898L9.13086 13.0664C8.97461 12.8516 8.87695 12.6074 8.87695 12.3633C8.87695 11.875 9.24805 11.4746 9.73633 11.4746C10.0488 11.4746 10.293 11.582 10.5469 11.9238L13.3594 15.4395L19.0039 6.46484C19.209 6.14258 19.4824 5.9668 19.7754 5.9668C20.2441 5.9668 20.6836 6.29883 20.6836 6.79688C20.6836 7.03125 20.5469 7.28516 20.4199 7.49023L14.2969 17.0898C14.0723 17.4316 13.7695 17.6074 13.4082 17.6074Z", fillAlpha = 0.85f)
        }
        return _sFCheckmarkRectangleFill!!
    }

private var _sFCheckmarkRectangleFill: ImageVector? = null
