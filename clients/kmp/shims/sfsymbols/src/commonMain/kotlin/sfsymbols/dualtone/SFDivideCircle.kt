package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFDivideCircle (dualtone)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Dualtone.SFDivideCircle: ImageVector
    get() {
        if (_sFDivideCircle != null) {
            return _sFDivideCircle!!
        }
        _sFDivideCircle = sfIcon(
            name = "Dualtone.SFDivideCircle",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.4395C19.7363 25.4395 25.4395 19.7461 25.4395 12.7246C25.4395 5.70312 19.7363 0 12.7148 0C5.69336 0 0 5.70312 0 12.7246C0 19.7461 5.69336 25.4395 12.7148 25.4395ZM12.7148 23.623C6.68945 23.623 1.81641 18.75 1.81641 12.7246C1.81641 6.69922 6.68945 1.82617 12.7148 1.82617C18.7402 1.82617 23.6133 6.69922 23.6133 12.7246C23.6133 18.75 18.7402 23.623 12.7148 23.623Z", fillAlpha = 0.425f)
            addSfPath("M12.6855 9.42383C13.4766 9.42383 14.0625 8.88672 14.0625 8.18359C14.0625 7.40234 13.4961 6.8457 12.6855 6.8457C11.9434 6.8457 11.3574 7.41211 11.3574 8.19336C11.3574 8.89648 11.9434 9.42383 12.6855 9.42383ZM7.61719 13.6328L17.8027 13.6328C18.3984 13.6328 18.7988 13.3203 18.7988 12.7441C18.7988 12.168 18.418 11.8457 17.8027 11.8457L7.61719 11.8457C7.00195 11.8457 6.61133 12.168 6.61133 12.7441C6.61133 13.3203 7.02148 13.6328 7.61719 13.6328ZM12.6855 18.6328C13.4766 18.6328 14.0625 18.0762 14.0625 17.373C14.0625 16.5918 13.4961 16.0449 12.6855 16.0449C11.9434 16.0449 11.3574 16.6016 11.3574 17.3828C11.3574 18.0859 11.9434 18.6328 12.6855 18.6328Z", fillAlpha = 0.85f)
        }
        return _sFDivideCircle!!
    }

private var _sFDivideCircle: ImageVector? = null
