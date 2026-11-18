package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFSCircleFill (dualtone)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Dualtone.SFSCircleFill: ImageVector
    get() {
        if (_sFSCircleFill != null) {
            return _sFSCircleFill!!
        }
        _sFSCircleFill = sfIcon(
            name = "Dualtone.SFSCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.4395C19.7266 25.4395 25.4395 19.7266 25.4395 12.7246C25.4395 5.71289 19.7266 0 12.7148 0C5.71289 0 0 5.71289 0 12.7246C0 19.7266 5.71289 25.4395 12.7148 25.4395Z", fillAlpha = 0.2125f)
            addSfPath("M12.5977 18.7402C10.3223 18.7402 8.74023 17.6172 8.31055 16.3379C8.21289 16.1133 8.18359 15.918 8.18359 15.7422C8.18359 15.2344 8.49609 14.9316 8.99414 14.9316C9.4043 14.9316 9.64844 15.1367 9.84375 15.5566C10.2344 16.6504 11.2793 17.2656 12.7246 17.2656C14.2188 17.2656 15.4102 16.5234 15.4102 15.3516C15.4102 14.3945 14.834 13.9648 13.2227 13.5156L11.3574 12.998C9.39453 12.4609 8.38867 11.5625 8.38867 9.98047C8.38867 7.95898 10.1758 6.5332 12.7441 6.5332C14.8828 6.5332 16.416 7.5293 16.8652 8.92578C16.9434 9.14062 16.9824 9.33594 16.9824 9.49219C16.9824 10.0195 16.6504 10.2734 16.1816 10.2734C15.752 10.2734 15.5078 10.0684 15.3516 9.67773C14.9512 8.55469 14.043 7.96875 12.6758 7.96875C11.2402 7.96875 10.2344 8.75977 10.2344 9.89258C10.2344 10.7227 10.7812 11.2109 12.0996 11.5723L13.8379 12.041C16.1035 12.6465 17.2656 13.5547 17.2656 15.1953C17.2656 17.3633 15.4199 18.7402 12.5977 18.7402Z", fillAlpha = 0.85f)
        }
        return _sFSCircleFill!!
    }

private var _sFSCircleFill: ImageVector? = null
