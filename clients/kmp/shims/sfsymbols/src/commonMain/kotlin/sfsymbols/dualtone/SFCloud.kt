package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFCloud (dualtone)
 * Viewport: 30.2734 x 20.127
 */
public val SfSymbols.Dualtone.SFCloud: ImageVector
    get() {
        if (_sFCloud != null) {
            return _sFCloud!!
        }
        _sFCloud = sfIcon(
            name = "Dualtone.SFCloud",
            viewportWidth = 30.2734f,
            viewportHeight = 20.127f
        ) {
            addSfPath("M6.34766 19.7949L22.8223 19.7949C27.0117 19.7949 30.2734 16.6211 30.2734 12.5781C30.2734 8.49609 26.9336 5.41016 22.4219 5.41016C20.752 2.07031 17.7344 0 13.9258 0C9.07227 0 5 3.81836 4.56055 8.81836C2.05078 9.52148 0.283203 11.5918 0.283203 14.2871C0.283203 17.3242 2.5 19.7949 6.34766 19.7949ZM6.32812 18.0664C3.61328 18.0664 2.01172 16.5332 2.01172 14.3359C2.01172 12.4219 3.26172 11.0156 5.38086 10.459C6.01562 10.293 6.24023 10.0098 6.28906 9.375C6.58203 5.03906 9.92188 1.72852 13.9258 1.72852C17.1289 1.72852 19.541 3.52539 20.957 6.44531C21.2305 7.00195 21.5527 7.19727 22.2559 7.19727C26.1621 7.19727 28.5352 9.64844 28.5352 12.627C28.5352 15.6543 26.084 18.0664 22.9297 18.0664Z", fillAlpha = 0.85f)
        }
        return _sFCloud!!
    }

private var _sFCloud: ImageVector? = null
