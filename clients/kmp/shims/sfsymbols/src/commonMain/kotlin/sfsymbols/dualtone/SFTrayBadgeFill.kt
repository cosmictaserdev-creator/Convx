package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFTrayBadgeFill (dualtone)
 * Viewport: 29.9512 x 31.8262
 */
public val SfSymbols.Dualtone.SFTrayBadgeFill: ImageVector
    get() {
        if (_sFTrayBadgeFill != null) {
            return _sFTrayBadgeFill!!
        }
        _sFTrayBadgeFill = sfIcon(
            name = "Dualtone.SFTrayBadgeFill",
            viewportWidth = 29.9512f,
            viewportHeight = 31.8262f
        ) {
            addSfPath("M16.4355 5.80078C16.4355 6.16457 16.4697 6.52083 16.539 6.86523L9.19922 6.86523C8.24219 6.86523 7.51953 7.1582 6.93359 7.95898L2.05078 14.5508C1.875 14.8047 1.95312 15.1074 2.31445 15.1074L10.6738 15.1074C11.2891 15.1074 11.5918 15.5664 11.5918 16.0742L11.5918 16.1328C11.5918 17.793 12.8711 19.4043 14.7949 19.4043C16.7188 19.4043 17.998 17.793 17.998 16.1328L17.998 16.0742C17.998 15.5664 18.3008 15.1074 18.916 15.1074L27.2656 15.1074C27.627 15.1074 27.7051 14.8047 27.5293 14.5508L24.8647 10.9464C25.3539 10.6991 25.8017 10.3804 26.1941 10.003L28.9355 13.6719C29.4238 14.3262 29.5898 14.8047 29.5898 15.8301L29.5898 22.7441C29.5898 25.2344 28.3105 26.5137 25.7812 26.5137L3.79883 26.5137C1.2793 26.5137 0 25.2441 0 22.7441L0 15.8301C0 14.8047 0.166016 14.3262 0.644531 13.6719L5.24414 7.5293C6.52344 5.80078 7.24609 5.3418 9.3457 5.3418L16.459 5.3418Z", fillAlpha = 0.425f)
            addSfPath("M22.2363 10.0488C24.5508 10.0488 26.4746 8.13477 26.4746 5.81055C26.4746 3.48633 24.5508 1.57227 22.2363 1.57227C19.9023 1.57227 17.998 3.48633 17.998 5.81055C17.998 8.13477 19.9023 10.0488 22.2363 10.0488Z", fillAlpha = 0.85f)
        }
        return _sFTrayBadgeFill!!
    }

private var _sFTrayBadgeFill: ImageVector? = null
