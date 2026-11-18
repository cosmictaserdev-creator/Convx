package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFBubbleLeftAndBubbleRightFill (dualtone)
 * Viewport: 37.9395 x 31.2793
 */
public val SfSymbols.Dualtone.SFBubbleLeftAndBubbleRightFill: ImageVector
    get() {
        if (_sFBubbleLeftAndBubbleRightFill != null) {
            return _sFBubbleLeftAndBubbleRightFill!!
        }
        _sFBubbleLeftAndBubbleRightFill = sfIcon(
            name = "Dualtone.SFBubbleLeftAndBubbleRightFill",
            viewportWidth = 37.9395f,
            viewportHeight = 31.2793f
        ) {
            addSfPath("M28.6773 6.45508L19.2871 6.45508C15.0781 6.45508 12.6465 8.95508 12.6465 13.0859L12.6465 20.3125C12.6465 21.5907 12.8793 22.7109 13.3268 23.6474L10.1562 26.4453C9.52148 27.002 9.15039 27.2852 8.61328 27.2852C7.87109 27.2852 7.43164 26.7578 7.43164 25.918L7.43164 22.5586L6.69922 22.5586C3.16406 22.5586 1.19141 20.5566 1.19141 17.0605L1.19141 7.09961C1.19141 3.60352 3.16406 1.5918 6.69922 1.5918L23.1934 1.5918C26.5137 1.5918 28.4461 3.36528 28.6773 6.45508Z", fillAlpha = 0.425f)
            addSfPath("M19.2871 25.3613L24.082 25.3613L28.2031 28.8867C28.8281 29.4336 29.1992 29.7168 29.707 29.7168C30.4492 29.7168 30.8691 29.1895 30.8691 28.3789L30.8691 25.3613L31.3086 25.3613C34.6289 25.3613 36.3867 23.5742 36.3867 20.293L36.3867 13.0859C36.3867 9.80469 34.6289 8.00781 31.3086 8.00781L19.2871 8.00781C15.957 8.00781 14.1992 9.80469 14.1992 13.0859L14.1992 20.3125C14.1992 23.584 15.957 25.3613 19.2871 25.3613Z", fillAlpha = 0.85f)
        }
        return _sFBubbleLeftAndBubbleRightFill!!
    }

private var _sFBubbleLeftAndBubbleRightFill: ImageVector? = null
