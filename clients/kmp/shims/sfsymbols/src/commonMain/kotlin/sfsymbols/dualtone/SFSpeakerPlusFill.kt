package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFSpeakerPlusFill (dualtone)
 * Viewport: 30.5957 x 21.9434
 */
public val SfSymbols.Dualtone.SFSpeakerPlusFill: ImageVector
    get() {
        if (_sFSpeakerPlusFill != null) {
            return _sFSpeakerPlusFill!!
        }
        _sFSpeakerPlusFill = sfIcon(
            name = "Dualtone.SFSpeakerPlusFill",
            viewportWidth = 30.5957f,
            viewportHeight = 21.9434f
        ) {
            addSfPath("M25.3711 15.8008L25.3711 6.10352C25.3711 5.55664 24.9902 5.17578 24.4531 5.17578C23.9355 5.17578 23.5742 5.55664 23.5742 6.10352L23.5742 15.8008C23.5742 16.3379 23.9355 16.7188 24.4531 16.7188C24.9902 16.7188 25.3711 16.3477 25.3711 15.8008ZM19.6289 11.8457L29.3262 11.8457C29.8633 11.8457 30.2344 11.4844 30.2344 10.9668C30.2344 10.4199 29.8633 10.0488 29.3262 10.0488L19.6289 10.0488C19.0723 10.0488 18.7012 10.4199 18.7012 10.9668C18.7012 11.4844 19.082 11.8457 19.6289 11.8457Z", fillAlpha = 0.425f)
            addSfPath("M13.4668 21.9434C14.2285 21.9434 14.7461 21.3867 14.7461 20.6445L14.7461 1.35742C14.7461 0.615234 14.2285 0.00976562 13.4473 0.00976562C12.9102 0.00976562 12.5391 0.244141 11.9824 0.771484L6.52344 5.94727C6.43555 6.03516 6.31836 6.07422 6.17188 6.07422L2.4707 6.07422C0.908203 6.07422 0 6.98242 0 8.64258L0 13.3691C0 15.0195 0.908203 15.9277 2.4707 15.9277L6.17188 15.9277C6.31836 15.9277 6.43555 15.9766 6.52344 16.0645L11.9824 21.2305C12.4902 21.709 12.9297 21.9434 13.4668 21.9434Z", fillAlpha = 0.85f)
        }
        return _sFSpeakerPlusFill!!
    }

private var _sFSpeakerPlusFill: ImageVector? = null
