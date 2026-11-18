package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFCapslockFill (monochrome)
 * Viewport: 26.6895 x 26.0645
 */
public val SfSymbols.Monochrome.SFCapslockFill: ImageVector
    get() {
        if (_sFCapslockFill != null) {
            return _sFCapslockFill!!
        }
        _sFCapslockFill = sfIcon(
            name = "Monochrome.SFCapslockFill",
            viewportWidth = 26.6895f,
            viewportHeight = 26.0645f
        ) {
            addSfPath("M1.66992 13.4277L24.6582 13.4277C25.5957 13.4277 26.3281 12.8711 26.3281 12.0117C26.3281 11.4746 26.0645 11.0547 25.5566 10.6055L14.6582 0.761719C14.1113 0.263672 13.6816 0 13.1641 0C12.6465 0 12.2168 0.263672 11.6699 0.761719L0.771484 10.6152C0.244141 11.084 0 11.4746 0 12.0117C0 12.8711 0.732422 13.4277 1.66992 13.4277ZM19.707 15.9375L19.707 10.2539C19.707 8.7207 18.8086 7.75391 17.207 7.75391L9.12109 7.75391C7.50977 7.75391 6.62109 8.7207 6.62109 10.2539L6.62109 15.9375C6.62109 17.4609 7.50977 18.4277 9.12109 18.4277L17.207 18.4277C18.8086 18.4277 19.707 17.4609 19.707 15.9375ZM8.50586 26.0645L17.8223 26.0645C19.0039 26.0645 19.707 25.3711 19.707 24.1895L19.707 22.3535C19.707 21.1816 19.0039 20.4785 17.8223 20.4785L8.50586 20.4785C7.32422 20.4785 6.62109 21.1816 6.62109 22.3535L6.62109 24.1895C6.62109 25.3711 7.32422 26.0645 8.50586 26.0645Z", fillAlpha = 0.85f)
        }
        return _sFCapslockFill!!
    }

private var _sFCapslockFill: ImageVector? = null
