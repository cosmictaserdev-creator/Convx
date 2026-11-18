package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFSuitDiamond (dualtone)
 * Viewport: 19.5703 x 25.4004
 */
public val SfSymbols.Dualtone.SFSuitDiamond: ImageVector
    get() {
        if (_sFSuitDiamond != null) {
            return _sFSuitDiamond!!
        }
        _sFSuitDiamond = sfIcon(
            name = "Dualtone.SFSuitDiamond",
            viewportWidth = 19.5703f,
            viewportHeight = 25.4004f
        ) {
            addSfPath("M9.59961 25.3809C10.2344 25.3809 10.5469 24.9512 11.1621 24.1211L18.7207 13.9844C19.0332 13.5645 19.209 13.1445 19.209 12.6953C19.209 12.2363 19.0332 11.8262 18.7207 11.3965L11.1621 1.25977C10.5469 0.429688 10.2344 0 9.59961 0C8.97461 0 8.66211 0.429688 8.03711 1.25977L0.488281 11.3965C0.166016 11.8262 0 12.2363 0 12.6953C0 13.1445 0.166016 13.5645 0.488281 13.9844L8.03711 24.1211C8.66211 24.9512 8.97461 25.3809 9.59961 25.3809ZM9.59961 23.1348C9.52148 23.1348 9.48242 23.0762 9.43359 23.0078L2.05078 13.0469C1.94336 12.9004 1.92383 12.8027 1.92383 12.6953C1.92383 12.5879 1.94336 12.4805 2.05078 12.3438L9.43359 2.38281C9.48242 2.31445 9.52148 2.25586 9.59961 2.25586C9.6875 2.25586 9.72656 2.31445 9.77539 2.38281L17.1582 12.3438C17.2656 12.4805 17.2852 12.5879 17.2852 12.6953C17.2852 12.8027 17.2656 12.9004 17.1582 13.0469L9.77539 23.0078C9.72656 23.0762 9.6875 23.1348 9.59961 23.1348Z", fillAlpha = 0.85f)
        }
        return _sFSuitDiamond!!
    }

private var _sFSuitDiamond: ImageVector? = null
