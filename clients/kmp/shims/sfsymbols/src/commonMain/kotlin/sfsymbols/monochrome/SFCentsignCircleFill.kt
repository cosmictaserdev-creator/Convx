package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFCentsignCircleFill (monochrome)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Monochrome.SFCentsignCircleFill: ImageVector
    get() {
        if (_sFCentsignCircleFill != null) {
            return _sFCentsignCircleFill!!
        }
        _sFCentsignCircleFill = sfIcon(
            name = "Monochrome.SFCentsignCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M25.4395 12.7246C25.4395 19.7266 19.7266 25.4395 12.7148 25.4395C5.71289 25.4395 0 19.7266 0 12.7246C0 5.71289 5.71289 0 12.7148 0C19.7266 0 25.4395 5.71289 25.4395 12.7246ZM12.2656 7.10938L12.2656 8.22266C9.95117 8.42773 8.61328 10.2344 8.61328 12.666C8.61328 15.2344 10.0293 16.9043 12.2656 17.0996L12.2656 18.2227C12.2656 18.4375 12.4512 18.623 12.6758 18.623C12.9004 18.623 13.0762 18.4375 13.0762 18.2227L13.0762 17.1094C14.043 17.0312 14.9707 16.6699 15.5957 16.0645C15.9863 15.6641 16.3281 15.0586 16.3281 14.6289C16.3281 14.248 16.0352 14.043 15.7227 14.043C15.4004 14.043 15.2539 14.2578 15.127 14.4824C14.707 15.3711 14.043 15.8887 13.0762 15.9863L13.0762 9.3457C14.0234 9.43359 14.7363 9.93164 15.1172 10.8496C15.2344 11.123 15.4492 11.2891 15.7227 11.2891C16.0742 11.2891 16.3086 11.0645 16.3086 10.7031C16.3086 10.2246 15.957 9.66797 15.5664 9.26758C14.9512 8.66211 14.0723 8.30078 13.0762 8.22266L13.0762 7.10938C13.0762 6.89453 12.9004 6.70898 12.6758 6.70898C12.4512 6.70898 12.2656 6.89453 12.2656 7.10938ZM12.2656 15.9668C10.7031 15.7422 9.94141 14.4043 9.94141 12.666C9.94141 10.8984 10.7324 9.57031 12.2656 9.35547Z", fillAlpha = 0.85f)
        }
        return _sFCentsignCircleFill!!
    }

private var _sFCentsignCircleFill: ImageVector? = null
