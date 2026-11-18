package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFWindshieldRearAndWiper (dualtone)
 * Viewport: 30.9961 x 25.4395
 */
public val SfSymbols.Dualtone.SFWindshieldRearAndWiper: ImageVector
    get() {
        if (_sFWindshieldRearAndWiper != null) {
            return _sFWindshieldRearAndWiper!!
        }
        _sFWindshieldRearAndWiper = sfIcon(
            name = "Dualtone.SFWindshieldRearAndWiper",
            viewportWidth = 30.9961f,
            viewportHeight = 25.4395f
        ) {
            addSfPath("M3.13441 3.7188C2.21806 3.93785 1.72852 4.62756 1.72852 5.76172L1.72852 14.9902C1.72852 16.3867 2.4707 17.1094 3.83789 17.1094L10.9787 17.1094L11.9912 18.8379L3.79883 18.8379C1.2793 18.8379 0 17.5781 0 15.0781L0 5.67383C0 3.7741 0.738708 2.58489 2.20094 2.1253ZM30.6348 5.67383L30.6348 15.0781C30.6348 17.5684 29.3652 18.8379 26.8359 18.8379L14.0049 18.8379L12.9923 17.1094L26.8066 17.1094C28.1348 17.1094 28.9062 16.3867 28.9062 14.9902L28.9062 5.76172C28.9062 4.36523 28.1348 3.64258 26.8066 3.64258L5.10292 3.64258L4.08457 1.9043L26.8359 1.9043C29.3652 1.9043 30.6348 3.18359 30.6348 5.67383Z", fillAlpha = 0.425f)
            addSfPath("M15.2832 25.4395C16.748 25.4395 17.9785 24.2188 17.9785 22.7441C17.9785 21.25 16.7578 20.0391 15.2832 20.0391C13.8086 20.0391 12.5879 21.25 12.5879 22.7441C12.5879 24.2188 13.7891 25.4395 15.2832 25.4395ZM15.2832 23.9844C14.5898 23.9844 14.0234 23.4277 14.0234 22.7441C14.0234 22.041 14.5898 21.4648 15.2832 21.4648C15.957 21.4648 16.543 22.0508 16.543 22.7441C16.543 23.4277 15.9668 23.9844 15.2832 23.9844ZM13.5645 21.5234L15.0977 20.7031L3.30078 0.566406C3.07617 0.166016 2.58789-0.00976562 2.09961 0.244141C1.65039 0.46875 1.5625 1.02539 1.79688 1.43555Z", fillAlpha = 0.85f)
        }
        return _sFWindshieldRearAndWiper!!
    }

private var _sFWindshieldRearAndWiper: ImageVector? = null
