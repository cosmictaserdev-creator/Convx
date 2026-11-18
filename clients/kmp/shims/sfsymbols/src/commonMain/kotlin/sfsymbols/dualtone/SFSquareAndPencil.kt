package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFSquareAndPencil (dualtone)
 * Viewport: 30.0684 x 29.5277
 */
public val SfSymbols.Dualtone.SFSquareAndPencil: ImageVector
    get() {
        if (_sFSquareAndPencil != null) {
            return _sFSquareAndPencil!!
        }
        _sFSquareAndPencil = sfIcon(
            name = "Dualtone.SFSquareAndPencil",
            viewportWidth = 30.0684f,
            viewportHeight = 29.5277f
        ) {
            addSfPath("M22.041 4.28789L20.4382 5.89177L9.0381 5.89177C6.82131 5.89177 5.57131 7.14177 5.57131 9.35856L5.57131 20.9894C5.57131 23.216 6.82131 24.466 9.0381 24.466L20.669 24.466C22.8858 24.466 24.1358 23.216 24.1358 20.9894L24.1358 9.66867L25.7512 8.05092C25.8276 8.4579 25.8643 8.89852 25.8643 9.36833L25.8643 20.9894C25.8643 24.3293 24.0088 26.1945 20.669 26.1945L9.0381 26.1945C5.69826 26.1945 3.84279 24.3293 3.84279 20.9894L3.84279 9.36833C3.84279 6.02848 5.69826 4.16325 9.0381 4.16325L20.669 4.16325C21.1602 4.16325 21.6193 4.2036 22.041 4.28789Z", fillAlpha = 0.425f)
            addSfPath("M12.4463 17.9133L14.7119 16.9074L26.46 5.15934L24.9072 3.61638L13.169 15.3644L12.1045 17.5617C12.0069 17.7472 12.2412 18.0011 12.4463 17.9133ZM27.3487 4.2902L28.1983 3.4113C28.6084 2.99138 28.6182 2.45427 28.2178 2.05388L27.9737 1.80973C27.6026 1.43864 27.0459 1.48747 26.6553 1.87809L25.7959 2.7277Z", fillAlpha = 0.85f)
        }
        return _sFSquareAndPencil!!
    }

private var _sFSquareAndPencil: ImageVector? = null
