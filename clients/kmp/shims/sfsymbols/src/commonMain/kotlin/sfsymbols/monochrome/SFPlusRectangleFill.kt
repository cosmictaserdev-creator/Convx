package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFPlusRectangleFill (monochrome)
 * Viewport: 29.9512 x 22.959
 */
public val SfSymbols.Monochrome.SFPlusRectangleFill: ImageVector
    get() {
        if (_sFPlusRectangleFill != null) {
            return _sFPlusRectangleFill!!
        }
        _sFPlusRectangleFill = sfIcon(
            name = "Monochrome.SFPlusRectangleFill",
            viewportWidth = 29.9512f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M29.5898 3.76953L29.5898 19.1992C29.5898 21.6797 28.3105 22.959 25.7812 22.959L3.79883 22.959C1.2793 22.959 0 21.6992 0 19.1992L0 3.76953C0 1.26953 1.2793 0 3.79883 0L25.7812 0C28.3105 0 29.5898 1.2793 29.5898 3.76953ZM13.8672 6.50391L13.8672 10.5371L9.83398 10.5371C9.27734 10.5371 8.88672 10.918 8.88672 11.4844C8.88672 12.0215 9.28711 12.3926 9.83398 12.3926L13.8672 12.3926L13.8672 16.4453C13.8672 16.9824 14.248 17.3828 14.7852 17.3828C15.3418 17.3828 15.7324 16.9922 15.7324 16.4453L15.7324 12.3926L19.7754 12.3926C20.3223 12.3926 20.7129 12.0215 20.7129 11.4844C20.7129 10.918 20.3223 10.5371 19.7754 10.5371L15.7324 10.5371L15.7324 6.50391C15.7324 5.9375 15.3418 5.54688 14.7852 5.54688C14.248 5.54688 13.8672 5.94727 13.8672 6.50391Z", fillAlpha = 0.85f)
        }
        return _sFPlusRectangleFill!!
    }

private var _sFPlusRectangleFill: ImageVector? = null
