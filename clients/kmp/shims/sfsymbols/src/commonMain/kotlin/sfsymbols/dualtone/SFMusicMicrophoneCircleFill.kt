package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFMusicMicrophoneCircleFill (dualtone)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Dualtone.SFMusicMicrophoneCircleFill: ImageVector
    get() {
        if (_sFMusicMicrophoneCircleFill != null) {
            return _sFMusicMicrophoneCircleFill!!
        }
        _sFMusicMicrophoneCircleFill = sfIcon(
            name = "Dualtone.SFMusicMicrophoneCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.4395C19.7266 25.4395 25.4395 19.7266 25.4395 12.7246C25.4395 5.71289 19.7266 0 12.7148 0C5.71289 0 0 5.71289 0 12.7246C0 19.7266 5.71289 25.4395 12.7148 25.4395Z", fillAlpha = 0.2125f)
            addSfPath("M12.9492 9.56055C12.832 8.7207 13.1641 7.91016 13.9355 7.13867L17.8223 11.0449C17.0801 11.7969 16.2207 12.168 15.3906 12.0605L13.4082 13.8672L13.4082 18.8965C13.4082 19.2871 13.1152 19.5898 12.7246 19.5898C12.3145 19.5898 12.0215 19.2871 12.0215 18.8965L12.0215 15.1855L8.63281 18.3105C8.31055 18.6035 7.91016 18.6035 7.59766 18.3301L6.52344 19.1699C6.35742 19.2871 6.18164 19.2676 6.03516 19.1406L5.83008 18.9453C5.68359 18.8184 5.68359 18.6035 5.80078 18.4473L6.64062 17.3926C6.34766 17.0508 6.34766 16.6602 6.65039 16.3379ZM7.49023 16.9336L8.04688 17.4902L14.248 11.6211C14.0918 11.5039 13.916 11.3672 13.7695 11.2012C13.6035 11.0254 13.4766 10.8691 13.3789 10.6738ZM18.5547 6.41602C19.7461 7.59766 19.6973 9.16992 18.4473 10.4297L14.541 6.5332C15.791 5.2832 17.3535 5.22461 18.5547 6.41602Z", fillAlpha = 0.85f)
        }
        return _sFMusicMicrophoneCircleFill!!
    }

private var _sFMusicMicrophoneCircleFill: ImageVector? = null
