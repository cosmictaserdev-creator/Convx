package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFXCircleFill (dualtone)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Dualtone.SFXCircleFill: ImageVector
    get() {
        if (_sFXCircleFill != null) {
            return _sFXCircleFill!!
        }
        _sFXCircleFill = sfIcon(
            name = "Dualtone.SFXCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.4395C19.7266 25.4395 25.4395 19.7266 25.4395 12.7246C25.4395 5.71289 19.7266 0 12.7148 0C5.71289 0 0 5.71289 0 12.7246C0 19.7266 5.71289 25.4395 12.7148 25.4395Z", fillAlpha = 0.2125f)
            addSfPath("M8.91602 18.6133C8.44727 18.6133 8.08594 18.2812 8.08594 17.8418C8.08594 17.6074 8.14453 17.4707 8.29102 17.2559L11.6113 12.5781L8.44727 8.11523C8.27148 7.88086 8.20312 7.72461 8.20312 7.48047C8.20312 7.02148 8.60352 6.65039 9.07227 6.65039C9.45312 6.65039 9.67773 6.78711 9.92188 7.13867L12.7832 11.3379L12.8418 11.3379L15.6641 7.14844C15.9082 6.78711 16.123 6.65039 16.4746 6.65039C16.9531 6.65039 17.3242 6.99219 17.3242 7.44141C17.3242 7.63672 17.2559 7.82227 17.0996 8.02734L13.7988 12.627L17.0703 17.207C17.2168 17.4023 17.2852 17.5684 17.2852 17.8027C17.2852 18.2812 16.9141 18.6133 16.416 18.6133C16.0547 18.6133 15.8594 18.4766 15.5762 18.0957L12.6953 13.916L12.6465 13.916L9.74609 18.0957C9.46289 18.4766 9.27734 18.6133 8.91602 18.6133Z", fillAlpha = 0.85f)
        }
        return _sFXCircleFill!!
    }

private var _sFXCircleFill: ImageVector? = null
