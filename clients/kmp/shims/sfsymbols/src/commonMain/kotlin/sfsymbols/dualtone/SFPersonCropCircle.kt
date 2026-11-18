package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFPersonCropCircle (dualtone)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Dualtone.SFPersonCropCircle: ImageVector
    get() {
        if (_sFPersonCropCircle != null) {
            return _sFPersonCropCircle!!
        }
        _sFPersonCropCircle = sfIcon(
            name = "Dualtone.SFPersonCropCircle",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.4395C19.7363 25.4395 25.4395 19.7461 25.4395 12.7246C25.4395 5.70312 19.7363 0 12.7148 0C5.69336 0 0 5.70312 0 12.7246C0 19.7461 5.69336 25.4395 12.7148 25.4395ZM12.7148 23.623C6.68945 23.623 1.81641 18.75 1.81641 12.7246C1.81641 6.69922 6.68945 1.82617 12.7148 1.82617C18.7402 1.82617 23.6133 6.69922 23.6133 12.7246C23.6133 18.75 18.7402 23.623 12.7148 23.623ZM21.2988 20.9668L21.2695 20.8496C20.5957 19.0332 17.2949 16.9629 12.7148 16.9629C8.1543 16.9629 4.85352 19.0234 4.16016 20.8301L4.13086 20.9668C6.38672 23.2227 10.0684 24.5703 12.7246 24.5703C15.3809 24.5703 19.0039 23.252 21.2988 20.9668ZM12.7148 14.8145C15.1367 14.834 17.0215 12.7637 17.0215 10.0684C17.0215 7.5293 15.1172 5.41992 12.7148 5.41992C10.3125 5.41992 8.39844 7.5293 8.4082 10.0684C8.41797 12.7637 10.293 14.7949 12.7148 14.8145Z", fillAlpha = 0.85f)
        }
        return _sFPersonCropCircle!!
    }

private var _sFPersonCropCircle: ImageVector? = null
