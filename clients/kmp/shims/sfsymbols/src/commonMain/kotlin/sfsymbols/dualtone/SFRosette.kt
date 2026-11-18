package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFRosette (dualtone)
 * Viewport: 20.127 x 27.9395
 */
public val SfSymbols.Dualtone.SFRosette: ImageVector
    get() {
        if (_sFRosette != null) {
            return _sFRosette!!
        }
        _sFRosette = sfIcon(
            name = "Dualtone.SFRosette",
            viewportWidth = 20.127f,
            viewportHeight = 27.9395f
        ) {
            addSfPath("M0 9.91211C0 12.7734 1.24023 15.3809 3.21289 17.1875L3.22266 26.9727C3.22266 27.627 3.56445 27.9297 4.0332 27.9297C4.44336 27.9297 4.74609 27.6758 5.05859 27.373L9.25781 23.1934C9.53125 22.9297 9.7168 22.8418 9.89258 22.8418C10.0684 22.8418 10.2539 22.9297 10.5273 23.1934L14.7168 27.373C15.0488 27.7051 15.3613 27.9297 15.7422 27.9297C16.2305 27.9297 16.5723 27.627 16.5723 26.9727L16.5723 17.1484C18.5254 15.3516 19.7559 12.7637 19.7656 9.91211C19.7754 4.45312 15.3027 0 9.88281 0C4.45312 0 0 4.45312 0 9.91211ZM1.74805 9.91211C1.74805 5.36133 5.33203 1.70898 9.89258 1.70898C14.4434 1.70898 18.0176 5.36133 18.0273 9.91211C18.0371 14.4531 14.4434 18.125 9.89258 18.1152C5.33203 18.1055 1.74805 14.4531 1.74805 9.91211Z", fillAlpha = 0.85f)
        }
        return _sFRosette!!
    }

private var _sFRosette: ImageVector? = null
