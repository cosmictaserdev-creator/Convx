package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFPerson (dualtone)
 * Viewport: 22.1875 x 23.1348
 */
public val SfSymbols.Dualtone.SFPerson: ImageVector
    get() {
        if (_sFPerson != null) {
            return _sFPerson!!
        }
        _sFPerson = sfIcon(
            name = "Dualtone.SFPerson",
            viewportWidth = 22.1875f,
            viewportHeight = 23.1348f
        ) {
            addSfPath("M2.86133 23.125L18.9648 23.125C20.9082 23.125 21.8262 22.5195 21.8262 21.2109C21.8262 17.9199 17.6758 13.1934 10.9082 13.1934C4.15039 13.1934 0 17.9199 0 21.2109C0 22.5195 0.917969 23.125 2.86133 23.125ZM2.38281 21.4941C1.9043 21.4941 1.72852 21.3672 1.72852 21.0156C1.72852 18.7402 5.03906 14.834 10.9082 14.834C16.7871 14.834 20.0977 18.7402 20.0977 21.0156C20.0977 21.3672 19.9219 21.4941 19.4434 21.4941ZM10.9277 11.5332C13.8867 11.5332 16.2695 8.92578 16.2695 5.69336C16.2695 2.51953 13.8867 0 10.9277 0C7.97852 0 5.57617 2.55859 5.57617 5.71289C5.57617 8.93555 7.96875 11.5332 10.9277 11.5332ZM10.9277 9.90234C8.94531 9.90234 7.30469 8.06641 7.30469 5.71289C7.30469 3.42773 8.93555 1.63086 10.9277 1.63086C12.9199 1.63086 14.541 3.39844 14.541 5.69336C14.541 8.04688 12.9102 9.90234 10.9277 9.90234Z", fillAlpha = 0.85f)
        }
        return _sFPerson!!
    }

private var _sFPerson: ImageVector? = null
