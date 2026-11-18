package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFPlusCapsule (monochrome)
 * Viewport: 32.3828 x 23.8184
 */
public val SfSymbols.Monochrome.SFPlusCapsule: ImageVector
    get() {
        if (_sFPlusCapsule != null) {
            return _sFPlusCapsule!!
        }
        _sFPlusCapsule = sfIcon(
            name = "Monochrome.SFPlusCapsule",
            viewportWidth = 32.3828f,
            viewportHeight = 23.8184f
        ) {
            addSfPath("M0 11.9043C0 19.1211 4.89258 23.8086 12.3047 23.8086L19.7168 23.8086C27.1289 23.8086 32.0215 19.1211 32.0215 11.9043C32.0215 4.67773 27.1289 0 19.7168 0L12.3047 0C4.89258 0 0 4.67773 0 11.9043ZM1.72852 11.9043C1.72852 5.75195 5.95703 1.72852 12.3047 1.72852L19.7168 1.72852C26.0645 1.72852 30.293 5.75195 30.293 11.9043C30.293 18.0469 26.0645 22.0703 19.7168 22.0703L12.3047 22.0703C5.95703 22.0703 1.72852 18.0469 1.72852 11.9043Z", fillAlpha = 0.85f)
            addSfPath("M16.9141 16.7383L16.9141 7.04102C16.9141 6.49414 16.5332 6.11328 15.9961 6.11328C15.4785 6.11328 15.1074 6.49414 15.1074 7.04102L15.1074 16.7383C15.1074 17.2754 15.4785 17.6562 15.9961 17.6562C16.5332 17.6562 16.9141 17.2852 16.9141 16.7383ZM11.1621 12.7832L20.8691 12.7832C21.3965 12.7832 21.7773 12.4219 21.7773 11.9043C21.7773 11.3574 21.4062 10.9863 20.8691 10.9863L11.1621 10.9863C10.6152 10.9863 10.2441 11.3574 10.2441 11.9043C10.2441 12.4219 10.625 12.7832 11.1621 12.7832Z", fillAlpha = 0.85f)
        }
        return _sFPlusCapsule!!
    }

private var _sFPlusCapsule: ImageVector? = null
