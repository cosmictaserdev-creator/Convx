package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFPlusMinusCapsule (dualtone)
 * Viewport: 32.3828 x 23.8184
 */
public val SfSymbols.Dualtone.SFPlusMinusCapsule: ImageVector
    get() {
        if (_sFPlusMinusCapsule != null) {
            return _sFPlusMinusCapsule!!
        }
        _sFPlusMinusCapsule = sfIcon(
            name = "Dualtone.SFPlusMinusCapsule",
            viewportWidth = 32.3828f,
            viewportHeight = 23.8184f
        ) {
            addSfPath("M0 11.9043C0 19.1211 4.89258 23.8086 12.3047 23.8086L19.7168 23.8086C27.1289 23.8086 32.0215 19.1211 32.0215 11.9043C32.0215 4.67773 27.1289 0 19.7168 0L12.3047 0C4.89258 0 0 4.67773 0 11.9043ZM1.72852 11.9043C1.72852 5.75195 5.95703 1.72852 12.3047 1.72852L19.7168 1.72852C26.0645 1.72852 30.293 5.75195 30.293 11.9043C30.293 18.0469 26.0645 22.0703 19.7168 22.0703L12.3047 22.0703C5.95703 22.0703 1.72852 18.0469 1.72852 11.9043Z", fillAlpha = 0.425f)
            addSfPath("M24.6777 12.7539C25.1562 12.7539 25.5371 12.373 25.5371 11.9043C25.5371 11.4258 25.1562 11.0449 24.6777 11.0449L18.7891 11.0449C18.3203 11.0449 17.9395 11.4258 17.9395 11.9043C17.9395 12.373 18.3203 12.7539 18.7891 12.7539ZM7.26562 12.7539L13.584 12.7539C14.0625 12.7539 14.4336 12.373 14.4336 11.9043C14.4336 11.4258 14.0625 11.0449 13.584 11.0449L7.26562 11.0449C6.79688 11.0449 6.41602 11.4258 6.41602 11.9043C6.41602 12.373 6.79688 12.7539 7.26562 12.7539ZM11.2793 15.0586L11.2793 8.73047C11.2793 8.26172 10.8887 7.89062 10.4199 7.89062C9.95117 7.89062 9.57031 8.26172 9.57031 8.73047L9.57031 15.0586C9.57031 15.5371 9.95117 15.9082 10.4199 15.9082C10.8887 15.9082 11.2793 15.5371 11.2793 15.0586Z", fillAlpha = 0.85f)
        }
        return _sFPlusMinusCapsule!!
    }

private var _sFPlusMinusCapsule: ImageVector? = null
