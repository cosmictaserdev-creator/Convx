package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFArrowDownRightTopleftRectangleFill (monochrome)
 * Viewport: 29.9512 x 22.959
 */
public val SfSymbols.Monochrome.SFArrowDownRightTopleftRectangleFill: ImageVector
    get() {
        if (_sFArrowDownRightTopleftRectangleFill != null) {
            return _sFArrowDownRightTopleftRectangleFill!!
        }
        _sFArrowDownRightTopleftRectangleFill = sfIcon(
            name = "Monochrome.SFArrowDownRightTopleftRectangleFill",
            viewportWidth = 29.9512f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M29.5898 3.76953L29.5898 19.1992C29.5898 21.6797 28.3105 22.959 25.7812 22.959L3.79883 22.959C1.2793 22.959 0 21.6992 0 19.1992L0 3.76953C0 1.26953 1.2793 0 3.79883 0L25.7812 0C28.3105 0 29.5898 1.2793 29.5898 3.76953ZM3.55469 4.39453C3.55469 4.60938 3.66211 4.83398 3.81836 4.98047L8.19336 9.35547L10.0511 11.115L8.16406 10.918L5.67383 10.918C5.12695 10.9277 4.79492 11.2598 4.79492 11.748C4.79492 12.2363 5.11719 12.5488 5.64453 12.5586L11.6699 12.5586C12.2656 12.5586 12.5781 12.2559 12.5781 11.6406L12.5781 5.625C12.5684 5.10742 12.2461 4.77539 11.7676 4.77539C11.2695 4.77539 10.9375 5.11719 10.9277 5.6543L10.9277 7.91992L11.1327 10.0322L9.36523 8.17383L5.01953 3.81836C4.85352 3.65234 4.64844 3.55469 4.36523 3.55469C3.87695 3.55469 3.55469 3.87695 3.55469 4.39453Z", fillAlpha = 0.85f)
        }
        return _sFArrowDownRightTopleftRectangleFill!!
    }

private var _sFArrowDownRightTopleftRectangleFill: ImageVector? = null
