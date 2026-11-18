package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFClipboardFill (dualtone)
 * Viewport: 21.3281 x 34.3262
 */
public val SfSymbols.Dualtone.SFClipboardFill: ImageVector
    get() {
        if (_sFClipboardFill != null) {
            return _sFClipboardFill!!
        }
        _sFClipboardFill = sfIcon(
            name = "Dualtone.SFClipboardFill",
            viewportWidth = 21.3281f,
            viewportHeight = 34.3262f
        ) {
            addSfPath("M20.9668 8.49609L20.9668 27.8125C20.9668 30.3418 19.707 31.6113 17.207 31.6113L3.75977 31.6113C1.25977 31.6113 0 30.3418 0 27.8125L0 8.49609C0 5.9668 1.25977 4.6875 3.75977 4.6875L4.17413 4.6875C4.16466 4.77973 4.16016 4.87429 4.16016 4.9707L4.16016 6.2793C4.16016 7.74414 5.18555 8.7793 6.62109 8.7793L14.3457 8.7793C15.7812 8.7793 16.8066 7.74414 16.8066 6.2793L16.8066 4.9707C16.8066 4.87429 16.8021 4.77973 16.7927 4.6875L17.207 4.6875C19.707 4.6875 20.9668 5.9668 20.9668 8.49609Z", fillAlpha = 0.85f)
            addSfPath("M6.62109 7.41211L14.3457 7.41211C15.0293 7.41211 15.4492 6.99219 15.4492 6.2793L15.4492 4.9707C15.4492 4.25781 15.0293 3.83789 14.3457 3.83789L13.0859 3.83789C13.0176 2.4707 11.875 1.35742 10.4883 1.35742C9.0918 1.35742 7.94922 2.4707 7.88086 3.83789L6.62109 3.83789C5.9375 3.83789 5.51758 4.25781 5.51758 4.9707L5.51758 6.2793C5.51758 6.99219 5.9375 7.41211 6.62109 7.41211ZM10.4883 5.0293C9.86328 5.0293 9.36523 4.51172 9.36523 3.91602C9.36523 3.29102 9.86328 2.79297 10.4883 2.79297C11.1035 2.79297 11.6016 3.29102 11.6016 3.91602C11.6016 4.51172 11.1035 5.0293 10.4883 5.0293Z", fillAlpha = 0.85f)
        }
        return _sFClipboardFill!!
    }

private var _sFClipboardFill: ImageVector? = null
