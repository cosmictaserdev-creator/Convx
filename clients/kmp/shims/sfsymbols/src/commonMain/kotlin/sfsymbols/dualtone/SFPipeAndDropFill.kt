package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFPipeAndDropFill (dualtone)
 * Viewport: 29.9512 x 26.7871
 */
public val SfSymbols.Dualtone.SFPipeAndDropFill: ImageVector
    get() {
        if (_sFPipeAndDropFill != null) {
            return _sFPipeAndDropFill!!
        }
        _sFPipeAndDropFill = sfIcon(
            name = "Dualtone.SFPipeAndDropFill",
            viewportWidth = 29.9512f,
            viewportHeight = 26.7871f
        ) {
            addSfPath("M6.36719 3.65234L6.36719 4.41406L23.2324 4.41406L23.2324 3.65234C23.2324 2.30469 23.9941 1.5625 25.3906 1.5625L27.4219 1.5625C28.8281 1.5625 29.5898 2.30469 29.5898 3.65234L29.5898 12.3535C29.5898 13.7012 28.8281 14.4434 27.4219 14.4434L25.3906 14.4434C23.9941 14.4434 23.2324 13.7012 23.2324 12.3535L23.2324 11.4648L18.7006 11.4648L17.3145 8.89648C16.748 7.83203 15.8887 7.27539 14.8145 7.27539C13.7305 7.27539 12.8809 7.8418 12.3047 8.87695L10.9248 11.4648L6.36719 11.4648L6.36719 12.3535C6.36719 13.7012 5.60547 14.4434 4.19922 14.4434L2.16797 14.4434C0.761719 14.4434 0 13.7012 0 12.3535L0 3.65234C0 2.30469 0.761719 1.5625 2.16797 1.5625L4.19922 1.5625C5.60547 1.5625 6.36719 2.30469 6.36719 3.65234Z", fillAlpha = 0.425f)
            addSfPath("M14.8145 25.2246C17.9395 25.2246 20.4297 22.793 20.4297 19.7461C20.4297 18.0566 19.5801 16.3965 18.8672 15.0391L15.9473 9.62891C15.6543 9.08203 15.3223 8.83789 14.8145 8.83789C14.3066 8.83789 13.9746 9.08203 13.6816 9.61914L10.7812 15.0391C10.0586 16.3965 9.20898 18.0566 9.20898 19.7461C9.20898 22.793 11.6992 25.2246 14.8145 25.2246Z", fillAlpha = 0.85f)
        }
        return _sFPipeAndDropFill!!
    }

private var _sFPipeAndDropFill: ImageVector? = null
