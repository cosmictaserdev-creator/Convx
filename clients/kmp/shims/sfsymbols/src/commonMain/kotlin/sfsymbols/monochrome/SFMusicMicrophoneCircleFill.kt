package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFMusicMicrophoneCircleFill (monochrome)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Monochrome.SFMusicMicrophoneCircleFill: ImageVector
    get() {
        if (_sFMusicMicrophoneCircleFill != null) {
            return _sFMusicMicrophoneCircleFill!!
        }
        _sFMusicMicrophoneCircleFill = sfIcon(
            name = "Monochrome.SFMusicMicrophoneCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M25.4395 12.7246C25.4395 19.7266 19.7266 25.4395 12.7148 25.4395C5.71289 25.4395 0 19.7266 0 12.7246C0 5.71289 5.71289 0 12.7148 0C19.7266 0 25.4395 5.71289 25.4395 12.7246ZM12.9492 9.56055L6.65039 16.3379C6.34766 16.6602 6.34766 17.0508 6.64062 17.3926L5.80078 18.4473C5.68359 18.6035 5.68359 18.8184 5.83008 18.9453L6.03516 19.1406C6.18164 19.2676 6.35742 19.2871 6.52344 19.1699L7.59766 18.3301C7.91016 18.6035 8.31055 18.6035 8.63281 18.3105L12.0215 15.1855L12.0215 18.8965C12.0215 19.2871 12.3145 19.5898 12.7246 19.5898C13.1152 19.5898 13.4082 19.2871 13.4082 18.8965L13.4082 13.8672L15.3906 12.0605C16.2207 12.168 17.0801 11.7969 17.8223 11.0449L13.9355 7.13867C13.1641 7.91016 12.832 8.7207 12.9492 9.56055ZM13.7695 11.2012C13.916 11.3672 14.0918 11.5039 14.248 11.6211L8.04688 17.4902L7.49023 16.9336L13.3789 10.6738C13.4766 10.8691 13.6035 11.0254 13.7695 11.2012ZM14.541 6.5332L18.4473 10.4297C19.6973 9.16992 19.7461 7.59766 18.5547 6.41602C17.3535 5.22461 15.791 5.2832 14.541 6.5332Z", fillAlpha = 0.85f)
        }
        return _sFMusicMicrophoneCircleFill!!
    }

private var _sFMusicMicrophoneCircleFill: ImageVector? = null
