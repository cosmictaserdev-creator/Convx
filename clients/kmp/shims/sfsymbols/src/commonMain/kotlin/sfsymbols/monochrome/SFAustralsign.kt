package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFAustralsign (monochrome)
 * Viewport: 19.082 x 23.1348
 */
public val SfSymbols.Monochrome.SFAustralsign: ImageVector
    get() {
        if (_sFAustralsign != null) {
            return _sFAustralsign!!
        }
        _sFAustralsign = sfIcon(
            name = "Monochrome.SFAustralsign",
            viewportWidth = 19.082f,
            viewportHeight = 23.1348f
        ) {
            addSfPath("M1.88477 23.1152C2.44141 23.1152 2.74414 22.8223 2.90039 22.3047L9.29688 2.74414L9.4043 2.74414L15.8203 22.3145C15.9863 22.8613 16.2793 23.1152 16.8359 23.1152C17.4121 23.1152 17.8906 22.7734 17.8906 22.1777C17.8906 22.041 17.8418 21.8066 17.7734 21.582L10.7129 1.03516C10.4883 0.341797 10.0488 0 9.35547 0C8.66211 0 8.23242 0.341797 7.99805 1.03516L0.947266 21.5918C0.878906 21.8066 0.839844 21.9727 0.839844 22.1875C0.839844 22.7734 1.31836 23.1152 1.88477 23.1152ZM0 11.9727C0 12.3535 0.322266 12.6758 0.712891 12.6758L18.0078 12.6758C18.3984 12.6758 18.7207 12.3535 18.7207 11.9727C18.7207 11.582 18.3984 11.2598 18.0078 11.2598L0.712891 11.2598C0.322266 11.2598 0 11.582 0 11.9727ZM0 16.4258C0 16.8164 0.322266 17.1387 0.712891 17.1387L18.0078 17.1387C18.3984 17.1387 18.7207 16.8164 18.7207 16.4258C18.7207 16.0449 18.3984 15.7227 18.0078 15.7227L0.712891 15.7227C0.322266 15.7227 0 16.0449 0 16.4258Z", fillAlpha = 0.85f)
        }
        return _sFAustralsign!!
    }

private var _sFAustralsign: ImageVector? = null
