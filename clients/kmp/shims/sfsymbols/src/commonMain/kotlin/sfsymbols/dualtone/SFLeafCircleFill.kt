package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFLeafCircleFill (dualtone)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Dualtone.SFLeafCircleFill: ImageVector
    get() {
        if (_sFLeafCircleFill != null) {
            return _sFLeafCircleFill!!
        }
        _sFLeafCircleFill = sfIcon(
            name = "Dualtone.SFLeafCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.4395C19.7266 25.4395 25.4395 19.7266 25.4395 12.7246C25.4395 5.71289 19.7266 0 12.7148 0C5.71289 0 0 5.71289 0 12.7246C0 19.7266 5.71289 25.4395 12.7148 25.4395Z", fillAlpha = 0.2125f)
            addSfPath("M6.46484 7.73438C6.5332 7.37305 6.85547 7.34375 7.08984 7.58789C7.91016 8.47656 8.99414 8.61328 10.0098 8.61328C10.8691 8.61328 11.9141 8.39844 12.8516 8.39844C15.6543 8.39844 17.6367 9.74609 17.6367 12.5977C17.6367 12.8711 17.6074 13.1543 17.5879 13.3301C17.5488 13.5254 17.3242 13.6133 17.1484 13.4375C14.5605 11.123 12.0898 12.6172 10.0195 10.7617C9.81445 10.5859 9.59961 10.752 9.6875 10.9473C10.7812 13.5352 14.5996 12.1387 17.1484 14.502C17.7539 15.0684 18.8867 16.4844 18.8867 17.3145C18.8867 17.832 18.5449 18.2031 18.1152 18.2031C17.9199 18.2031 17.7246 18.1055 17.6367 17.832C17.3047 16.7383 17.041 16.0352 16.5137 15.3516C15.9375 16.1523 14.6777 16.9629 12.832 16.9629C8.88672 16.9629 6.28906 14.043 6.28906 9.64844C6.28906 9.13086 6.36719 8.34961 6.46484 7.73438Z", fillAlpha = 0.85f)
        }
        return _sFLeafCircleFill!!
    }

private var _sFLeafCircleFill: ImageVector? = null
