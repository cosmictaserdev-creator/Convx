package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFCabinetFill (dualtone)
 * Viewport: 20.1758 x 28.5645
 */
public val SfSymbols.Dualtone.SFCabinetFill: ImageVector
    get() {
        if (_sFCabinetFill != null) {
            return _sFCabinetFill!!
        }
        _sFCabinetFill = sfIcon(
            name = "Dualtone.SFCabinetFill",
            viewportWidth = 20.1758f,
            viewportHeight = 28.5645f
        ) {
            addSfPath("M2.75391 26.2402L9.13086 26.2402L9.13086 0L2.75391 0C1.10352 0 0 1.14258 0 2.87109L0 23.3691C0 25.0977 1.10352 26.2402 2.75391 26.2402ZM6.14258 14.8438C5.43945 14.8438 4.87305 14.2773 4.87305 13.584C4.87305 12.8711 5.43945 12.3145 6.14258 12.3145C6.83594 12.3145 7.41211 12.8711 7.41211 13.584C7.41211 14.2773 6.83594 14.8438 6.14258 14.8438ZM17.0605 26.2402C18.7109 26.2402 19.8145 25.0977 19.8145 23.3691L19.8145 2.87109C19.8145 1.14258 18.7109 0 17.0605 0L10.6836 0L10.6836 26.2402ZM13.6621 14.8438C12.959 14.8438 12.3828 14.2773 12.3828 13.584C12.3828 12.8711 12.959 12.3145 13.6621 12.3145C14.3555 12.3145 14.9316 12.8711 14.9316 13.584C14.9316 14.2773 14.3555 14.8438 13.6621 14.8438ZM3.53516 28.5254C4.08203 28.5254 4.46289 28.1543 4.46289 27.5977L4.46289 25.1855L2.60742 25.1855L2.60742 27.5977C2.60742 28.1543 2.97852 28.5254 3.53516 28.5254ZM16.2793 28.5254C16.8359 28.5254 17.207 28.1543 17.207 27.5977L17.207 25.1855L15.3516 25.1855L15.3516 27.5977C15.3516 28.1543 15.7324 28.5254 16.2793 28.5254Z", fillAlpha = 0.85f)
        }
        return _sFCabinetFill!!
    }

private var _sFCabinetFill: ImageVector? = null
