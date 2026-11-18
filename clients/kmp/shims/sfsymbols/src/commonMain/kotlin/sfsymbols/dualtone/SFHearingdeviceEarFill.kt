package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFHearingdeviceEarFill (dualtone)
 * Viewport: 19.6777 x 27.1973
 */
public val SfSymbols.Dualtone.SFHearingdeviceEarFill: ImageVector
    get() {
        if (_sFHearingdeviceEarFill != null) {
            return _sFHearingdeviceEarFill!!
        }
        _sFHearingdeviceEarFill = sfIcon(
            name = "Dualtone.SFHearingdeviceEarFill",
            viewportWidth = 19.6777f,
            viewportHeight = 27.1973f
        ) {
            addSfPath("M0.595703 21.0156C0.595703 24.4043 2.72461 26.582 6.04492 26.582C8.54492 26.582 10.4395 25.4199 11.5918 23.252L12.6172 21.3867C15.293 16.3184 19.6777 15.8887 19.6777 9.70703C19.6777 3.87695 15.9277 0 10.2734 0C4.50195 0 0.595703 3.93555 0.595703 9.77539Z", fillAlpha = 0.2125f)
            addSfPath("M4.21875 17.5977C4.21875 15.2734 6.8457 15.6738 6.8457 14.1309C6.8457 12.8418 4.21875 11.9043 4.21875 9.07227C4.21875 5.89844 6.65039 3.59375 10.2441 3.59375C13.8574 3.59375 16.2598 5.99609 16.2598 9.60938C16.2598 10.0684 15.8789 10.4492 15.4199 10.4492C14.9609 10.4492 14.5703 10.0684 14.5703 9.60938C14.5703 6.99219 12.8809 5.2832 10.2441 5.2832C7.64648 5.2832 5.9082 6.89453 5.9082 9.07227C5.9082 11.7871 9.83398 12.3535 9.83398 16.084C9.83398 18.0859 8.51562 19.6777 6.62109 19.6875C5.18555 19.6875 4.21875 18.8477 4.21875 17.5977Z", fillAlpha = 0.85f)
        }
        return _sFHearingdeviceEarFill!!
    }

private var _sFHearingdeviceEarFill: ImageVector? = null
