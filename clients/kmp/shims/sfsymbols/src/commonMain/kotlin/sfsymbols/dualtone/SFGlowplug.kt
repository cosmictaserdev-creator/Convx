package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFGlowplug (dualtone)
 * Viewport: 36.6158 x 22.5684
 */
public val SfSymbols.Dualtone.SFGlowplug: ImageVector
    get() {
        if (_sFGlowplug != null) {
            return _sFGlowplug!!
        }
        _sFGlowplug = sfIcon(
            name = "Dualtone.SFGlowplug",
            viewportWidth = 36.6158f,
            viewportHeight = 22.5684f
        ) {
            addSfPath("M32.7268 1.73828C33.5276 1.73828 34.3186 1.83594 35.0804 2.03125C36.3011 2.33398 36.7796 0.683594 35.4612 0.332031C34.7483 0.136719 33.7327 0 32.7268 0C25.7737 0 20.0413 6.36719 20.0413 14.0234C20.0413 19.1113 22.1702 22.5488 25.4221 22.5488C28.6741 22.5488 30.8128 19.1113 30.8128 14.0234C30.8128 6.36719 25.0999 0 18.1272 0C11.1546 0 5.44168 6.36719 5.44168 14.0234C5.44168 19.1113 7.58035 22.5488 10.8323 22.5488C14.0745 22.5488 16.2132 19.1113 16.2132 14.0234C16.2132 6.36719 10.471 0 3.51785 0C2.52176 0 1.50613 0.136719 0.793241 0.332031C-0.534884 0.683594-0.0466024 2.33398 1.16434 2.03125C1.92605 1.83594 2.71707 1.73828 3.51785 1.73828C9.50418 1.73828 14.4846 7.30469 14.4846 14.0234C14.4846 18.1152 13.0003 20.8203 10.8323 20.8203C8.66434 20.8203 7.17019 18.1055 7.17019 14.0234C7.17019 7.30469 12.1409 1.73828 18.1272 1.73828C24.1038 1.73828 29.0843 7.30469 29.0843 14.0234C29.0843 18.1055 27.5804 20.8203 25.4221 20.8203C23.2542 20.8203 21.7698 18.1152 21.7698 14.0234C21.7698 7.30469 26.7503 1.73828 32.7268 1.73828Z", fillAlpha = 0.85f)
        }
        return _sFGlowplug!!
    }

private var _sFGlowplug: ImageVector? = null
