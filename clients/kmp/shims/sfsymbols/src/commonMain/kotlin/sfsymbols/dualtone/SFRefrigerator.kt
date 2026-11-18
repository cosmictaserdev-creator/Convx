package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFRefrigerator (dualtone)
 * Viewport: 18.1445 x 28.0176
 */
public val SfSymbols.Dualtone.SFRefrigerator: ImageVector
    get() {
        if (_sFRefrigerator != null) {
            return _sFRefrigerator!!
        }
        _sFRefrigerator = sfIcon(
            name = "Dualtone.SFRefrigerator",
            viewportWidth = 18.1445f,
            viewportHeight = 28.0176f
        ) {
            addSfPath("M2.75391 27.9883L15.0293 27.9883C16.6797 27.9883 17.7832 26.8359 17.7832 25.1074L17.7832 2.87109C17.7832 1.14258 16.6797 0 15.0293 0L2.75391 0C1.10352 0 0 1.14258 0 2.87109L0 25.1074C0 26.8359 1.10352 27.9883 2.75391 27.9883ZM2.82227 26.25C2.16797 26.25 1.72852 25.8008 1.72852 25.1074L1.72852 2.88086C1.72852 2.1875 2.16797 1.72852 2.82227 1.72852L14.9609 1.72852C15.6152 1.72852 16.0547 2.1875 16.0547 2.88086L16.0547 25.1074C16.0547 25.8008 15.6152 26.25 14.9609 26.25ZM4.14062 7.35352C4.47266 7.35352 4.70703 7.11914 4.70703 6.78711L4.70703 3.92578C4.70703 3.59375 4.47266 3.35938 4.14062 3.35938C3.79883 3.35938 3.56445 3.59375 3.56445 3.92578L3.56445 6.78711C3.56445 7.11914 3.79883 7.35352 4.14062 7.35352ZM1.25 10.8008L16.5527 10.8008L16.5527 9.36523L1.25 9.36523ZM4.14062 16.1133C4.47266 16.1133 4.70703 15.8789 4.70703 15.5469L4.70703 12.6855C4.70703 12.3535 4.47266 12.1191 4.14062 12.1191C3.79883 12.1191 3.56445 12.3535 3.56445 12.6855L3.56445 15.5469C3.56445 15.8789 3.79883 16.1133 4.14062 16.1133ZM1.25 24.2871L16.5527 24.2871L16.5527 22.8516L1.25 22.8516Z", fillAlpha = 0.85f)
        }
        return _sFRefrigerator!!
    }

private var _sFRefrigerator: ImageVector? = null
