package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFEarbudsCase (dualtone)
 * Viewport: 29.9512 x 19.7559
 */
public val SfSymbols.Dualtone.SFEarbudsCase: ImageVector
    get() {
        if (_sFEarbudsCase != null) {
            return _sFEarbudsCase!!
        }
        _sFEarbudsCase = sfIcon(
            name = "Dualtone.SFEarbudsCase",
            viewportWidth = 29.9512f,
            viewportHeight = 19.7559f
        ) {
            addSfPath("M5.73242 19.7559L23.8672 19.7559C27.5391 19.7559 29.5898 17.8516 29.5898 14.4434L29.5898 7.43164C29.5898 2.45117 27.1289 0.0195312 22.1191 0.0195312L7.48047 0.0195312C2.46094 0.0195312 0 2.45117 0 7.43164L0 14.4434C0 17.8516 2.05078 19.7559 5.73242 19.7559ZM5.73242 18.0273C3.01758 18.0273 1.72852 16.8457 1.72852 14.4434L1.73828 7.43164C1.73828 3.55469 3.55469 1.74805 7.48047 1.74805L22.1191 1.74805C26.0449 1.74805 27.8711 3.55469 27.8711 7.43164L27.8711 14.4434C27.8711 16.8457 26.582 18.0273 23.8672 18.0273ZM0.878906 8.59375L28.7207 8.59375L28.7207 7.25586L0.878906 7.25586ZM12.4512 12.3242L17.1387 12.3242C17.7051 12.3242 18.1055 11.9238 18.1055 11.3574C18.1055 10.791 17.7051 10.3906 17.1387 10.3906L12.4512 10.3906C11.9141 10.3906 11.4844 10.791 11.4844 11.3574C11.4844 11.9238 11.9141 12.3242 12.4512 12.3242Z", fillAlpha = 0.85f)
        }
        return _sFEarbudsCase!!
    }

private var _sFEarbudsCase: ImageVector? = null
