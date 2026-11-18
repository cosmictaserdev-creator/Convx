package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFHeartSlashFill (dualtone)
 * Viewport: 30.0244 x 28.3496
 */
public val SfSymbols.Dualtone.SFHeartSlashFill: ImageVector
    get() {
        if (_sFHeartSlashFill != null) {
            return _sFHeartSlashFill!!
        }
        _sFHeartSlashFill = sfIcon(
            name = "Dualtone.SFHeartSlashFill",
            viewportWidth = 30.0244f,
            viewportHeight = 28.3496f
        ) {
            addSfPath("M19.6366 22.733C18.4302 23.7141 17.0951 24.6696 15.6567 25.5957C15.4126 25.752 15.0708 25.9082 14.8364 25.9082C14.5923 25.9082 14.2505 25.752 14.0063 25.5957C7.02393 21.0938 2.46338 15.8984 2.46338 10.5957C2.46338 9.07949 2.82788 7.71476 3.47487 6.58522ZM27.1997 10.5957C27.1997 13.7256 25.6143 16.8181 22.8676 19.7514L6.75827 3.64906C7.5616 3.31328 8.4483 3.13477 9.38721 3.13477C11.7798 3.13477 13.7427 4.49219 14.8364 6.55273C15.9399 4.48242 17.8833 3.13477 20.2856 3.13477C24.2017 3.13477 27.1997 6.24023 27.1997 10.5957Z", fillAlpha = 0.425f)
            addSfPath("M24.2212 25.3906C24.5532 25.7129 25.0903 25.7129 25.4028 25.3906C25.7251 25.0586 25.7349 24.541 25.4028 24.209L2.78564 1.61133C2.47314 1.28906 1.93604 1.2793 1.604 1.61133C1.28174 1.92383 1.28174 2.4707 1.604 2.7832Z", fillAlpha = 0.85f)
        }
        return _sFHeartSlashFill!!
    }

private var _sFHeartSlashFill: ImageVector? = null
