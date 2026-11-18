package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFDistributeHorizontal (dualtone)
 * Viewport: 23.418 x 28.6426
 */
public val SfSymbols.Dualtone.SFDistributeHorizontal: ImageVector
    get() {
        if (_sFDistributeHorizontal != null) {
            return _sFDistributeHorizontal!!
        }
        _sFDistributeHorizontal = sfIcon(
            name = "Dualtone.SFDistributeHorizontal",
            viewportWidth = 23.418f,
            viewportHeight = 28.6426f
        ) {
            addSfPath("M6.98242 5.11719L6.98242 23.5254C6.98242 25.2051 7.91016 26.1426 9.57031 26.1426L13.4863 26.1426C15.1367 26.1426 16.0645 25.2051 16.0645 23.5254L16.0645 5.11719C16.0645 3.4375 15.1367 2.5 13.4863 2.5L9.57031 2.5C7.91016 2.5 6.98242 3.4375 6.98242 5.11719ZM8.62305 5.14648C8.62305 4.51172 8.99414 4.13086 9.6582 4.13086L13.3984 4.13086C14.0527 4.13086 14.4336 4.51172 14.4336 5.14648L14.4336 23.4863C14.4336 24.1309 14.0527 24.5117 13.3984 24.5117L9.6582 24.5117C8.99414 24.5117 8.62305 24.1309 8.62305 23.4863Z", fillAlpha = 0.425f)
            addSfPath("M21.4648 0.751953L21.4648 27.8906C21.4648 28.3105 21.8359 28.6426 22.2559 28.6426C22.6855 28.6426 23.0566 28.3105 23.0566 27.8906L23.0566 0.751953C23.0566 0.332031 22.6855 0 22.2559 0C21.8359 0 21.4648 0.332031 21.4648 0.751953ZM1.5918 0.751953C1.5918 0.332031 1.2207 0 0.800781 0C0.371094 0 0 0.332031 0 0.751953L0 27.8906C0 28.3105 0.371094 28.6426 0.800781 28.6426C1.2207 28.6426 1.5918 28.3105 1.5918 27.8906Z", fillAlpha = 0.85f)
        }
        return _sFDistributeHorizontal!!
    }

private var _sFDistributeHorizontal: ImageVector? = null
