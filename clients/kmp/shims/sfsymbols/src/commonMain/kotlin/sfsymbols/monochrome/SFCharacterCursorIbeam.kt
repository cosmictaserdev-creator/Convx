package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFCharacterCursorIbeam (monochrome)
 * Viewport: 25.3125 x 26.1035
 */
public val SfSymbols.Monochrome.SFCharacterCursorIbeam: ImageVector
    get() {
        if (_sFCharacterCursorIbeam != null) {
            return _sFCharacterCursorIbeam!!
        }
        _sFCharacterCursorIbeam = sfIcon(
            name = "Monochrome.SFCharacterCursorIbeam",
            viewportWidth = 25.3125f,
            viewportHeight = 26.1035f
        ) {
            addSfPath("M16.8262 25.9375L24.1992 25.9375C24.6973 25.9375 24.9512 25.6934 24.9512 25.293C24.9512 24.873 24.6973 24.6582 24.1992 24.6582L21.25 24.6582L21.25 1.26953L24.1992 1.26953C24.6973 1.26953 24.9512 1.04492 24.9512 0.625C24.9512 0.224609 24.6973 0 24.1992 0L16.8262 0C16.3281 0 16.0742 0.224609 16.0742 0.625C16.0742 1.04492 16.3281 1.26953 16.8262 1.26953L19.7754 1.26953L19.7754 24.6582L16.8262 24.6582C16.3281 24.6582 16.0742 24.873 16.0742 25.293C16.0742 25.6934 16.3281 25.9375 16.8262 25.9375Z", fillAlpha = 0.85f)
            addSfPath("M0.908203 22.168C1.45508 22.168 1.73828 21.9434 1.94336 21.3574L3.74023 16.5039L11.4258 16.5039L13.2227 21.3574C13.4277 21.9434 13.7109 22.168 14.2578 22.168C14.8242 22.168 15.1855 21.8262 15.1855 21.2891C15.1855 21.1035 15.1465 20.9375 15.0684 20.7031L8.93555 4.54102C8.69141 3.89648 8.26172 3.57422 7.58789 3.57422C6.92383 3.57422 6.48438 3.88672 6.25977 4.53125L0.117188 20.7129C0.0292969 20.9473 0 21.1133 0 21.2988C0 21.8359 0.341797 22.168 0.908203 22.168ZM4.28711 14.8633L7.56836 5.9668L7.63672 5.9668L10.9082 14.8633Z", fillAlpha = 0.85f)
        }
        return _sFCharacterCursorIbeam!!
    }

private var _sFCharacterCursorIbeam: ImageVector? = null
