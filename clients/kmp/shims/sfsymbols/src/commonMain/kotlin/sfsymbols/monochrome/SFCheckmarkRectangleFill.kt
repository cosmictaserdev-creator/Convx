package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFCheckmarkRectangleFill (monochrome)
 * Viewport: 29.9512 x 22.959
 */
public val SfSymbols.Monochrome.SFCheckmarkRectangleFill: ImageVector
    get() {
        if (_sFCheckmarkRectangleFill != null) {
            return _sFCheckmarkRectangleFill!!
        }
        _sFCheckmarkRectangleFill = sfIcon(
            name = "Monochrome.SFCheckmarkRectangleFill",
            viewportWidth = 29.9512f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M29.5898 3.76953L29.5898 19.1992C29.5898 21.6797 28.3105 22.959 25.7812 22.959L3.79883 22.959C1.2793 22.959 0 21.6992 0 19.1992L0 3.76953C0 1.26953 1.2793 0 3.79883 0L25.7812 0C28.3105 0 29.5898 1.2793 29.5898 3.76953ZM19.0039 6.46484L13.3594 15.4395L10.5469 11.9238C10.293 11.582 10.0488 11.4746 9.73633 11.4746C9.24805 11.4746 8.87695 11.875 8.87695 12.3633C8.87695 12.6074 8.97461 12.8516 9.13086 13.0664L12.4609 17.0898C12.7441 17.4512 13.0371 17.6074 13.4082 17.6074C13.7695 17.6074 14.0723 17.4316 14.2969 17.0898L20.4199 7.49023C20.5469 7.28516 20.6836 7.03125 20.6836 6.79688C20.6836 6.29883 20.2441 5.9668 19.7754 5.9668C19.4824 5.9668 19.209 6.14258 19.0039 6.46484Z", fillAlpha = 0.85f)
        }
        return _sFCheckmarkRectangleFill!!
    }

private var _sFCheckmarkRectangleFill: ImageVector? = null
