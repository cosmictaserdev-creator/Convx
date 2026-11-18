package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFFibrechannel (dualtone)
 * Viewport: 31.9238 x 23.6816
 */
public val SfSymbols.Dualtone.SFFibrechannel: ImageVector
    get() {
        if (_sFFibrechannel != null) {
            return _sFFibrechannel!!
        }
        _sFFibrechannel = sfIcon(
            name = "Dualtone.SFFibrechannel",
            viewportWidth = 31.9238f,
            viewportHeight = 23.6816f
        ) {
            addSfPath("M0 0.859375C0 1.33789 0.390625 1.72852 0.869141 1.72852L30.7031 1.72852C31.1719 1.72852 31.5625 1.33789 31.5625 0.859375C31.5625 0.390625 31.1719 0 30.7031 0L0.869141 0C0.390625 0 0 0.390625 0 0.859375ZM3.75 12.7637L21.9434 12.7637L21.9434 17.3145C21.9434 18.0859 22.9199 18.4766 23.4863 17.8906L28.5547 12.7344C29.1992 12.0703 28.9941 10.918 27.832 10.918L9.63867 10.918L9.63867 6.36719C9.63867 5.5957 8.66211 5.20508 8.0957 5.79102L3.02734 10.9473C2.38281 11.6113 2.58789 12.7637 3.75 12.7637ZM0 22.8027C0 23.2715 0.390625 23.6719 0.869141 23.6719L30.7031 23.6719C31.1719 23.6719 31.5625 23.2715 31.5625 22.8027C31.5625 22.3242 31.1719 21.9336 30.7031 21.9336L0.869141 21.9336C0.390625 21.9336 0 22.3242 0 22.8027Z", fillAlpha = 0.85f)
        }
        return _sFFibrechannel!!
    }

private var _sFFibrechannel: ImageVector? = null
