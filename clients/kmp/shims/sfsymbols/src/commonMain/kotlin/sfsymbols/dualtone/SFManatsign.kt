package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFManatsign (dualtone)
 * Viewport: 17.0508 x 23.5449
 */
public val SfSymbols.Dualtone.SFManatsign: ImageVector
    get() {
        if (_sFManatsign != null) {
            return _sFManatsign!!
        }
        _sFManatsign = sfIcon(
            name = "Dualtone.SFManatsign",
            viewportWidth = 17.0508f,
            viewportHeight = 23.5449f
        ) {
            addSfPath("M1.06445 23.5156C1.68945 23.5156 2.11914 23.1348 2.11914 22.4023L2.11914 14.2773C2.11914 9.7168 3.83789 5.82031 8.33984 5.82031C12.8223 5.82031 14.5703 9.62891 14.5703 14.2773L14.5703 22.4023C14.5703 23.0859 14.9902 23.5156 15.6348 23.5156C16.2305 23.5156 16.6895 23.1445 16.6895 22.4023L16.6895 14.0918C16.6895 8.24219 14.0234 3.87695 8.33984 3.87695C2.71484 3.87695 0 8.25195 0 14.0918L0 22.4023C0 23.0859 0.419922 23.5156 1.06445 23.5156ZM8.31055 23.5156C8.75977 23.5156 9.13086 23.1836 9.13086 22.6758L9.13086 0.820312C9.13086 0.292969 8.76953 0 8.31055 0C7.85156 0 7.44141 0.253906 7.44141 0.820312L7.44141 22.6758C7.44141 23.1738 7.8125 23.5156 8.31055 23.5156Z", fillAlpha = 0.85f)
        }
        return _sFManatsign!!
    }

private var _sFManatsign: ImageVector? = null
