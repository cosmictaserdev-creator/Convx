package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFHearingdeviceEarFill (monochrome)
 * Viewport: 19.6777 x 27.1973
 */
public val SfSymbols.Monochrome.SFHearingdeviceEarFill: ImageVector
    get() {
        if (_sFHearingdeviceEarFill != null) {
            return _sFHearingdeviceEarFill!!
        }
        _sFHearingdeviceEarFill = sfIcon(
            name = "Monochrome.SFHearingdeviceEarFill",
            viewportWidth = 19.6777f,
            viewportHeight = 27.1973f
        ) {
            addSfPath("M19.6777 9.70703C19.6777 15.8887 15.293 16.3184 12.6172 21.3867L11.5918 23.252C10.4395 25.4199 8.54492 26.582 6.04492 26.582C2.72461 26.582 0.595703 24.4043 0.595703 21.0156L0.595703 9.77539C0.595703 3.93555 4.50195 0 10.2734 0C15.9277 0 19.6777 3.87695 19.6777 9.70703ZM4.21875 9.07227C4.21875 11.9043 6.8457 12.8418 6.8457 14.1309C6.8457 15.6738 4.21875 15.2734 4.21875 17.5977C4.21875 18.8477 5.18555 19.6875 6.62109 19.6875C8.51562 19.6777 9.83398 18.0859 9.83398 16.084C9.83398 12.3535 5.9082 11.7871 5.9082 9.07227C5.9082 6.89453 7.64648 5.2832 10.2441 5.2832C12.8809 5.2832 14.5703 6.99219 14.5703 9.60938C14.5703 10.0684 14.9609 10.4492 15.4199 10.4492C15.8789 10.4492 16.2598 10.0684 16.2598 9.60938C16.2598 5.99609 13.8574 3.59375 10.2441 3.59375C6.65039 3.59375 4.21875 5.89844 4.21875 9.07227Z", fillAlpha = 0.85f)
        }
        return _sFHearingdeviceEarFill!!
    }

private var _sFHearingdeviceEarFill: ImageVector? = null
