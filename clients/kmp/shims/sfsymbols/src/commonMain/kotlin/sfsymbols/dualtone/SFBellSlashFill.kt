package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFBellSlashFill (dualtone)
 * Viewport: 27.7198 x 27.3
 */
public val SfSymbols.Dualtone.SFBellSlashFill: ImageVector
    get() {
        if (_sFBellSlashFill != null) {
            return _sFBellSlashFill!!
        }
        _sFBellSlashFill = sfIcon(
            name = "Dualtone.SFBellSlashFill",
            viewportWidth = 27.7198f,
            viewportHeight = 27.3f
        ) {
            addSfPath("M13.6792 26.7213C11.5991 26.7213 10.105 25.2174 9.90969 23.4986L17.4488 23.4986C17.2437 25.2174 15.7495 26.7213 13.6792 26.7213ZM19.3784 21.9459L3.52297 21.9459C2.51711 21.9459 1.90188 21.4088 1.90188 20.608C1.90188 19.3385 3.14211 18.2252 4.21633 17.1119C5.17336 16.0865 5.31985 13.9967 5.48586 12.2096C5.53612 10.8614 5.68841 9.63323 5.96868 8.54764ZM17.1655 3.50838C20.4273 4.6119 21.6968 7.75643 21.8726 12.2096C22.0386 13.9967 22.1851 16.0865 23.1421 17.1119C24.2066 18.2252 25.4566 19.3385 25.4566 20.608C25.4566 20.9583 25.3388 21.2581 25.1214 21.4846L8.23134 4.60177C8.78001 4.13049 9.42845 3.76448 10.1831 3.50838C10.603 1.83846 11.8823 0.578697 13.6792 0.578697C15.4663 0.578697 16.7456 1.83846 17.1655 3.50838Z", fillAlpha = 0.425f)
            addSfPath("M2.12649 2.78573L24.939 25.5787C25.2613 25.901 25.7984 25.901 26.1109 25.5787C26.4331 25.2467 26.4429 24.7291 26.1109 24.3971L3.30813 1.60409C2.98586 1.28182 2.45852 1.27206 2.12649 1.60409C1.80422 1.92635 1.80422 2.46346 2.12649 2.78573Z", fillAlpha = 0.85f)
        }
        return _sFBellSlashFill!!
    }

private var _sFBellSlashFill: ImageVector? = null
