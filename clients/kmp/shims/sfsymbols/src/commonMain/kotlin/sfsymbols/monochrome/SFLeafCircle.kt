package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFLeafCircle (monochrome)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Monochrome.SFLeafCircle: ImageVector
    get() {
        if (_sFLeafCircle != null) {
            return _sFLeafCircle!!
        }
        _sFLeafCircle = sfIcon(
            name = "Monochrome.SFLeafCircle",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.4395C19.7363 25.4395 25.4395 19.7461 25.4395 12.7246C25.4395 5.70312 19.7363 0 12.7148 0C5.69336 0 0 5.70312 0 12.7246C0 19.7461 5.69336 25.4395 12.7148 25.4395ZM12.7148 23.623C6.68945 23.623 1.81641 18.75 1.81641 12.7246C1.81641 6.69922 6.68945 1.82617 12.7148 1.82617C18.7402 1.82617 23.6133 6.69922 23.6133 12.7246C23.6133 18.75 18.7402 23.623 12.7148 23.623Z", fillAlpha = 0.85f)
            addSfPath("M6.60156 7.85156C6.50391 8.45703 6.42578 9.21875 6.42578 9.7168C6.42578 14.0137 8.96484 16.8652 12.8125 16.8652C15.1367 16.8652 16.3965 15.5371 16.7188 14.8535L16.084 14.9023C16.8164 15.6738 17.1191 16.4453 17.5098 17.7051C17.5879 17.9688 17.7832 18.0762 17.9785 18.0762C18.3984 18.0762 18.7305 17.7051 18.7305 17.207C18.7305 16.3965 17.627 15.0195 17.0312 14.4629C14.541 12.1582 10.8203 13.5156 9.74609 10.9961C9.66797 10.8008 9.87305 10.6348 10.0684 10.8105C12.0898 12.627 14.502 11.1621 17.0312 13.4277C17.207 13.5938 17.4219 13.5156 17.4512 13.3203C17.4805 13.1445 17.5 12.8711 17.5 12.6074C17.5 9.82422 15.5762 8.50586 12.832 8.50586C11.9238 8.50586 10.8984 8.71094 10.0586 8.71094C9.0625 8.71094 8.00781 8.57422 7.20703 7.70508C6.98242 7.4707 6.66992 7.5 6.60156 7.85156Z", fillAlpha = 0.85f)
        }
        return _sFLeafCircle!!
    }

private var _sFLeafCircle: ImageVector? = null
