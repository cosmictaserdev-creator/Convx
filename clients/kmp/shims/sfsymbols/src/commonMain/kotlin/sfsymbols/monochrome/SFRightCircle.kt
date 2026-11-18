package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFRightCircle (monochrome)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Monochrome.SFRightCircle: ImageVector
    get() {
        if (_sFRightCircle != null) {
            return _sFRightCircle!!
        }
        _sFRightCircle = sfIcon(
            name = "Monochrome.SFRightCircle",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.4395C19.7363 25.4395 25.4395 19.7461 25.4395 12.7246C25.4395 5.70312 19.7363 0 12.7148 0C5.69336 0 0 5.70312 0 12.7246C0 19.7461 5.69336 25.4395 12.7148 25.4395ZM12.7148 23.623C6.68945 23.623 1.81641 18.75 1.81641 12.7246C1.81641 6.69922 6.68945 1.82617 12.7148 1.82617C18.7402 1.82617 23.6133 6.69922 23.6133 12.7246C23.6133 18.75 18.7402 23.623 12.7148 23.623Z", fillAlpha = 0.85f)
            addSfPath("M9.50195 18.4668C10.0586 18.4668 10.3711 18.1055 10.3711 17.5195L10.3711 14.1016L12.8906 14.1016L15.166 17.8906C15.4199 18.3105 15.6445 18.4668 16.0254 18.4668C16.4941 18.4668 16.8066 18.1641 16.8066 17.7344C16.8066 17.4902 16.748 17.3145 16.6016 17.0898L14.5898 13.8672C16.0938 13.418 17.0703 12.1191 17.0703 10.5371C17.0703 8.34961 15.498 6.93359 13.0664 6.93359L9.48242 6.93359C8.93555 6.93359 8.64258 7.32422 8.64258 7.90039L8.64258 17.5195C8.64258 18.0859 8.94531 18.4668 9.50195 18.4668ZM10.3711 12.8125L10.3711 8.28125L12.8809 8.28125C14.3555 8.28125 15.3613 9.13086 15.3613 10.5762C15.3613 11.9824 14.375 12.8125 12.8418 12.8125Z", fillAlpha = 0.85f)
        }
        return _sFRightCircle!!
    }

private var _sFRightCircle: ImageVector? = null
