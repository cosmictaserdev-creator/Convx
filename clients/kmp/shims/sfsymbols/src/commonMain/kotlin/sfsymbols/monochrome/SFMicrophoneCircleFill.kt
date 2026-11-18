package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFMicrophoneCircleFill (monochrome)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Monochrome.SFMicrophoneCircleFill: ImageVector
    get() {
        if (_sFMicrophoneCircleFill != null) {
            return _sFMicrophoneCircleFill!!
        }
        _sFMicrophoneCircleFill = sfIcon(
            name = "Monochrome.SFMicrophoneCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M25.4395 12.7246C25.4395 19.7266 19.7266 25.4395 12.7148 25.4395C5.71289 25.4395 0 19.7266 0 12.7246C0 5.71289 5.71289 0 12.7148 0C19.7266 0 25.4395 5.71289 25.4395 12.7246ZM16.4258 11.3867L16.4258 12.9688C16.4258 15.127 14.9316 16.6211 12.7148 16.6211C10.498 16.6211 9.01367 15.127 9.01367 12.9688L9.01367 11.3867C9.01367 11.0645 8.74023 10.7812 8.4082 10.7812C8.08594 10.7812 7.80273 11.0645 7.80273 11.3867L7.80273 12.9688C7.80273 15.5566 9.54102 17.4902 12.0996 17.7539L12.0996 19.2773L9.49219 19.2773C9.16016 19.2773 8.86719 19.5703 8.86719 19.9121C8.86719 20.2441 9.16016 20.5176 9.49219 20.5176L15.957 20.5176C16.2793 20.5176 16.5723 20.2344 16.5723 19.9121C16.5723 19.5703 16.2891 19.2773 15.957 19.2773L13.3398 19.2773L13.3398 17.7539C15.9082 17.4805 17.6367 15.5566 17.6367 12.9688L17.6367 11.3867C17.6367 11.0645 17.3535 10.7812 17.0312 10.7812C16.6992 10.7812 16.4258 11.0645 16.4258 11.3867ZM10.5664 7.32422L10.5664 12.9102C10.5664 14.2188 11.4355 15.2148 12.7148 15.2148C13.9941 15.2148 14.8633 14.2188 14.8633 12.9102L14.8633 7.32422C14.8633 6.01562 13.9941 5.03906 12.7148 5.03906C11.4355 5.03906 10.5664 6.01562 10.5664 7.32422Z", fillAlpha = 0.85f)
        }
        return _sFMicrophoneCircleFill!!
    }

private var _sFMicrophoneCircleFill: ImageVector? = null
