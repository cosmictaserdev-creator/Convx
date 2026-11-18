package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFQuestionmarkCircleFill (monochrome)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Monochrome.SFQuestionmarkCircleFill: ImageVector
    get() {
        if (_sFQuestionmarkCircleFill != null) {
            return _sFQuestionmarkCircleFill!!
        }
        _sFQuestionmarkCircleFill = sfIcon(
            name = "Monochrome.SFQuestionmarkCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M25.4395 12.7246C25.4395 19.7266 19.7266 25.4395 12.7148 25.4395C5.71289 25.4395 0 19.7266 0 12.7246C0 5.71289 5.71289 0 12.7148 0C19.7266 0 25.4395 5.71289 25.4395 12.7246ZM11.1621 17.8906C11.1621 18.5645 11.748 19.1211 12.4414 19.1211C13.125 19.1211 13.7207 18.5742 13.7207 17.8906C13.7207 17.1973 13.1348 16.6504 12.4414 16.6504C11.7383 16.6504 11.1621 17.207 11.1621 17.8906ZM8.94531 8.57422C8.88672 8.76953 8.84766 8.96484 8.84766 9.17969C8.84766 9.67773 9.23828 9.96094 9.61914 9.96094C10.0098 9.96094 10.2637 9.76562 10.4492 9.48242L10.6348 9.20898C10.9961 8.36914 11.7578 7.80273 12.7637 7.80273C14.0332 7.80273 14.8535 8.50586 14.8535 9.53125C14.8535 10.4102 14.3359 10.8691 13.1641 11.6797C12.207 12.3535 11.5723 13.0273 11.5723 14.248C11.5723 14.3262 11.5723 14.4043 11.5723 14.4824C11.5723 15.0684 11.8945 15.3711 12.4609 15.3711C13.0273 15.3711 13.3594 15.0195 13.3594 14.5605C13.3594 14.4922 13.3594 14.4141 13.3594 14.3457C13.3594 13.5938 13.7695 13.1348 14.6973 12.5098C15.9766 11.6602 16.7871 10.9082 16.7871 9.44336C16.7871 7.42188 14.9902 6.26953 12.832 6.26953C10.6641 6.26953 9.25781 7.35352 8.94531 8.57422Z", fillAlpha = 0.85f)
        }
        return _sFQuestionmarkCircleFill!!
    }

private var _sFQuestionmarkCircleFill: ImageVector? = null
