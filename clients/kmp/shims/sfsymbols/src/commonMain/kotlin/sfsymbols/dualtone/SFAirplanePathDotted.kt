package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFAirplanePathDotted (dualtone)
 * Viewport: 37.207 x 22.5098
 */
public val SfSymbols.Dualtone.SFAirplanePathDotted: ImageVector
    get() {
        if (_sFAirplanePathDotted != null) {
            return _sFAirplanePathDotted!!
        }
        _sFAirplanePathDotted = sfIcon(
            name = "Dualtone.SFAirplanePathDotted",
            viewportWidth = 37.207f,
            viewportHeight = 22.5098f
        ) {
            addSfPath("M7.5 12.9395C8.41797 12.9395 9.17969 12.1777 9.17969 11.2598C9.17969 10.332 8.41797 9.57031 7.5 9.57031C6.57227 9.57031 5.81055 10.332 5.81055 11.2598C5.81055 12.1777 6.57227 12.9395 7.5 12.9395ZM1.67969 12.9395C2.60742 12.9395 3.35938 12.1777 3.35938 11.2598C3.35938 10.332 2.60742 9.57031 1.67969 9.57031C0.761719 9.57031 0 10.332 0 11.2598C0 12.1777 0.761719 12.9395 1.67969 12.9395Z", fillAlpha = 0.425f)
            addSfPath("M36.8457 11.25C36.8359 9.96094 35.0781 9.02344 32.9688 9.02344L28.4473 9.02344C27.832 9.02344 27.5977 8.91602 27.2266 8.50586L19.8145 0.400391C19.5801 0.146484 19.2871 0 18.9648 0L17.6855 0C17.3926 0 17.2266 0.263672 17.373 0.566406L21.1914 9.02344L15.459 9.64844L13.457 5.98633C13.3008 5.71289 13.0859 5.5957 12.7246 5.5957L12.2266 5.5957C11.9434 5.5957 11.7578 5.77148 11.7578 6.06445L11.7578 16.4355C11.7578 16.7285 11.9434 16.9043 12.2266 16.9043L12.7246 16.9043C13.0859 16.9043 13.3008 16.7871 13.457 16.5137L15.459 12.8516L21.1914 13.4766L17.373 21.9336C17.2266 22.2363 17.3926 22.5 17.6855 22.5L18.9648 22.5C19.2871 22.5 19.5801 22.3535 19.8145 22.0996L27.2266 13.9941C27.5977 13.584 27.832 13.4766 28.4473 13.4766L32.9688 13.4766C35.0781 13.4766 36.8359 12.5391 36.8457 11.25Z", fillAlpha = 0.85f)
        }
        return _sFAirplanePathDotted!!
    }

private var _sFAirplanePathDotted: ImageVector? = null
