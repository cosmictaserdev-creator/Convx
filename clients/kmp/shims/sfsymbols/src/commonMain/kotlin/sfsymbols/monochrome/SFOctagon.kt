package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFOctagon (monochrome)
 * Viewport: 25.8691 x 24.6582
 */
public val SfSymbols.Monochrome.SFOctagon: ImageVector
    get() {
        if (_sFOctagon != null) {
            return _sFOctagon!!
        }
        _sFOctagon = sfIcon(
            name = "Monochrome.SFOctagon",
            viewportWidth = 25.8691f,
            viewportHeight = 24.6582f
        ) {
            addSfPath("M9.08203 24.6484L16.4258 24.6484C18.1641 24.6484 18.8867 24.082 19.7559 23.1445L24.502 17.959C25.3027 17.0898 25.5078 16.5039 25.5078 15.2637L25.5078 9.38477C25.5078 8.14453 25.3027 7.56836 24.502 6.68945L19.7559 1.50391C18.8867 0.576172 18.1641 0 16.4258 0L9.08203 0C7.34375 0 6.62109 0.576172 5.76172 1.50391L1.00586 6.68945C0.205078 7.56836 0 8.14453 0 9.38477L0 15.2637C0 16.5039 0.205078 17.0898 1.00586 17.959L5.76172 23.1445C6.62109 24.082 7.34375 24.6484 9.08203 24.6484ZM9.61914 22.9395C8.22266 22.9395 7.85156 22.6172 7.07031 21.7676L2.58789 16.9043C2.08008 16.3477 1.96289 16.0059 1.96289 15.0781L1.96289 9.57031C1.96289 8.64258 2.08008 8.31055 2.58789 7.75391L7.07031 2.88086C7.85156 2.04102 8.22266 1.71875 9.61914 1.71875L15.8984 1.71875C17.2852 1.71875 17.6562 2.04102 18.4375 2.88086L22.9199 7.75391C23.4277 8.31055 23.5449 8.64258 23.5449 9.57031L23.5449 15.0781C23.5449 16.0059 23.4277 16.3477 22.9199 16.9043L18.4375 21.7676C17.6562 22.6172 17.2852 22.9395 15.8984 22.9395Z", fillAlpha = 0.85f)
        }
        return _sFOctagon!!
    }

private var _sFOctagon: ImageVector? = null
