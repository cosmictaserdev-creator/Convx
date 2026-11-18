package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFMicrophoneCircleFill (dualtone)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Dualtone.SFMicrophoneCircleFill: ImageVector
    get() {
        if (_sFMicrophoneCircleFill != null) {
            return _sFMicrophoneCircleFill!!
        }
        _sFMicrophoneCircleFill = sfIcon(
            name = "Dualtone.SFMicrophoneCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.4395C19.7266 25.4395 25.4395 19.7266 25.4395 12.7246C25.4395 5.71289 19.7266 0 12.7148 0C5.71289 0 0 5.71289 0 12.7246C0 19.7266 5.71289 25.4395 12.7148 25.4395Z", fillAlpha = 0.2125f)
            addSfPath("M9.49219 20.5176C9.16016 20.5176 8.86719 20.2441 8.86719 19.9121C8.86719 19.5703 9.16016 19.2773 9.49219 19.2773L12.0996 19.2773L12.0996 17.7539C9.54102 17.4902 7.80273 15.5566 7.80273 12.9688L7.80273 11.3867C7.80273 11.0645 8.08594 10.7812 8.4082 10.7812C8.74023 10.7812 9.01367 11.0645 9.01367 11.3867L9.01367 12.9688C9.01367 15.127 10.498 16.6211 12.7148 16.6211C14.9316 16.6211 16.4258 15.127 16.4258 12.9688L16.4258 11.3867C16.4258 11.0645 16.6992 10.7812 17.0312 10.7812C17.3535 10.7812 17.6367 11.0645 17.6367 11.3867L17.6367 12.9688C17.6367 15.5566 15.9082 17.4805 13.3398 17.7539L13.3398 19.2773L15.957 19.2773C16.2891 19.2773 16.5723 19.5703 16.5723 19.9121C16.5723 20.2344 16.2793 20.5176 15.957 20.5176ZM12.7148 15.2148C11.4355 15.2148 10.5664 14.2188 10.5664 12.9102L10.5664 7.32422C10.5664 6.01562 11.4355 5.03906 12.7148 5.03906C13.9941 5.03906 14.8633 6.01562 14.8633 7.32422L14.8633 12.9102C14.8633 14.2188 13.9941 15.2148 12.7148 15.2148Z", fillAlpha = 0.85f)
        }
        return _sFMicrophoneCircleFill!!
    }

private var _sFMicrophoneCircleFill: ImageVector? = null
