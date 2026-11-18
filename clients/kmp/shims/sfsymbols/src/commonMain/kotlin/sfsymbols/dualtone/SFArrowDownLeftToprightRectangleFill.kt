package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFArrowDownLeftToprightRectangleFill (dualtone)
 * Viewport: 29.9512 x 22.959
 */
public val SfSymbols.Dualtone.SFArrowDownLeftToprightRectangleFill: ImageVector
    get() {
        if (_sFArrowDownLeftToprightRectangleFill != null) {
            return _sFArrowDownLeftToprightRectangleFill!!
        }
        _sFArrowDownLeftToprightRectangleFill = sfIcon(
            name = "Dualtone.SFArrowDownLeftToprightRectangleFill",
            viewportWidth = 29.9512f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M3.79883 22.959L25.7812 22.959C28.3105 22.959 29.5898 21.6797 29.5898 19.1992L29.5898 3.76953C29.5898 1.2793 28.3105 0 25.7812 0L3.79883 0C1.2793 0 0 1.26953 0 3.76953L0 19.1992C0 21.6992 1.2793 22.959 3.79883 22.959Z", fillAlpha = 0.2125f)
            addSfPath("M25.7617 4.98047C25.9277 4.83398 26.0352 4.60938 26.0352 4.39453C26.0352 3.87695 25.7129 3.55469 25.2148 3.55469C24.9414 3.55469 24.7363 3.65234 24.5703 3.81836L20.2148 8.17383L18.125 10.3711C17.3926 11.1621 18.3594 12.2168 19.1797 11.4551L21.3965 9.35547ZM17.9199 12.5586L23.9355 12.5586C24.4629 12.5488 24.7949 12.2363 24.7949 11.748C24.7949 11.2598 24.4531 10.9277 23.9062 10.918L21.4258 10.918L18.3398 11.2402L18.6523 7.91992L18.6523 5.6543C18.6426 5.11719 18.3203 4.77539 17.8223 4.77539C17.334 4.77539 17.0215 5.10742 17.0117 5.625L17.0117 11.6406C17.0117 12.2559 17.3145 12.5586 17.9199 12.5586Z", fillAlpha = 0.85f)
        }
        return _sFArrowDownLeftToprightRectangleFill!!
    }

private var _sFArrowDownLeftToprightRectangleFill: ImageVector? = null
