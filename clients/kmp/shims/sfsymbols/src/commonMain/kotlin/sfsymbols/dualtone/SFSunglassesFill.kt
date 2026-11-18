package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFSunglassesFill (dualtone)
 * Viewport: 38.6133 x 12.7832
 */
public val SfSymbols.Dualtone.SFSunglassesFill: ImageVector
    get() {
        if (_sFSunglassesFill != null) {
            return _sFSunglassesFill!!
        }
        _sFSunglassesFill = sfIcon(
            name = "Dualtone.SFSunglassesFill",
            viewportWidth = 38.6133f,
            viewportHeight = 12.7832f
        ) {
            addSfPath("M19.1309 4.41406C19.6289 4.41406 20.0293 4.69727 20.2539 5.20508L21.6113 8.19336C23.0859 11.4062 25.752 12.7637 28.7402 12.7637C33.0859 12.7637 36.1426 10.127 36.8164 5.40039L37.0508 3.75C37.2754 2.16797 38.252 1.60156 38.252 0.908203C38.252 0.351562 37.8516 0 37.0703 0C33.1543 0 29.6191 0.195312 27.5 0.517578C23.0664 1.17188 21.3086 2.31445 19.1309 2.31445C16.9434 2.31445 15.1855 1.17188 10.752 0.517578C8.64258 0.195312 5.10742 0 1.18164 0C0.410156 0 0 0.351562 0 0.908203C0 1.60156 0.976562 2.16797 1.21094 3.75L1.44531 5.40039C2.10938 10.127 5.16602 12.7637 9.52148 12.7637C12.5 12.7637 15.1758 11.4062 16.6406 8.19336L17.998 5.20508C18.2324 4.69727 18.6328 4.41406 19.1309 4.41406Z", fillAlpha = 0.85f)
        }
        return _sFSunglassesFill!!
    }

private var _sFSunglassesFill: ImageVector? = null
