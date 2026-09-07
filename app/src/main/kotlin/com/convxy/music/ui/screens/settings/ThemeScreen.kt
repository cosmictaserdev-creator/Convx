package com.convxy.music.ui.screens.settings

import com.convxy.music.ui.utils.appTopBarWindowInsets
import com.convxy.music.ui.utils.appTopBarWindowInsets
import android.content.res.Configuration
import com.convxy.music.ui.utils.appTopBarWindowInsets
import android.os.Build
import com.convxy.music.ui.utils.appTopBarWindowInsets
import androidx.compose.animation.AnimatedVisibility
import com.convxy.music.ui.utils.appTopBarWindowInsets
import androidx.compose.animation.core.Spring
import com.convxy.music.ui.utils.appTopBarWindowInsets
import androidx.compose.animation.core.animateDpAsState
import com.convxy.music.ui.utils.appTopBarWindowInsets
import androidx.compose.animation.core.animateFloatAsState
import com.convxy.music.ui.utils.appTopBarWindowInsets
import androidx.compose.animation.core.spring
import com.convxy.music.ui.utils.appTopBarWindowInsets
import androidx.compose.animation.core.tween
import com.convxy.music.ui.utils.appTopBarWindowInsets
import androidx.compose.animation.fadeIn
import com.convxy.music.ui.utils.appTopBarWindowInsets
import androidx.compose.animation.fadeOut
import com.convxy.music.ui.utils.appTopBarWindowInsets
import androidx.compose.animation.scaleIn
import com.convxy.music.ui.utils.appTopBarWindowInsets
import androidx.compose.animation.scaleOut
import com.convxy.music.ui.utils.appTopBarWindowInsets
import androidx.compose.foundation.BorderStroke
import com.convxy.music.ui.utils.appTopBarWindowInsets
import androidx.compose.foundation.Canvas
import com.convxy.music.ui.utils.appTopBarWindowInsets
import androidx.compose.foundation.background
import com.convxy.music.ui.utils.appTopBarWindowInsets
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import com.convxy.music.ui.utils.appTopBarWindowInsets
import com.convxy.music.ui.utils.bounceClick
import com.convxy.music.ui.utils.appTopBarWindowInsets
import com.convxy.music.ui.utils.combinedBounceClick
import com.convxy.music.ui.utils.appTopBarWindowInsets
import androidx.compose.foundation.interaction.MutableInteractionSource
import com.convxy.music.ui.utils.appTopBarWindowInsets
import androidx.compose.foundation.isSystemInDarkTheme
import com.convxy.music.ui.utils.appTopBarWindowInsets
import androidx.compose.foundation.layout.Arrangement
import com.convxy.music.ui.utils.appTopBarWindowInsets
import androidx.compose.foundation.layout.Box
import com.convxy.music.ui.utils.appTopBarWindowInsets
import androidx.compose.foundation.layout.Column
import com.convxy.music.ui.utils.appTopBarWindowInsets
import androidx.compose.foundation.layout.PaddingValues
import com.convxy.music.ui.utils.appTopBarWindowInsets
import androidx.compose.foundation.layout.Row
import com.convxy.music.ui.utils.appTopBarWindowInsets
import androidx.compose.foundation.layout.Spacer
import com.convxy.music.ui.utils.appTopBarWindowInsets
import androidx.compose.foundation.layout.aspectRatio
import com.convxy.music.ui.utils.appTopBarWindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import com.convxy.music.ui.utils.appTopBarWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import com.convxy.music.ui.utils.appTopBarWindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import com.convxy.music.ui.utils.appTopBarWindowInsets
import androidx.compose.foundation.layout.height
import com.convxy.music.ui.utils.appTopBarWindowInsets
import androidx.compose.foundation.layout.heightIn
import com.convxy.music.ui.utils.appTopBarWindowInsets
import androidx.compose.foundation.layout.padding
import com.convxy.music.ui.utils.appTopBarWindowInsets
import androidx.compose.foundation.layout.size
import com.convxy.music.ui.utils.appTopBarWindowInsets
import androidx.compose.foundation.layout.width
import com.convxy.music.ui.utils.appTopBarWindowInsets
import androidx.compose.foundation.lazy.LazyRow
import com.convxy.music.ui.utils.appTopBarWindowInsets
import androidx.compose.foundation.lazy.items
import com.convxy.music.ui.utils.appTopBarWindowInsets
import androidx.compose.foundation.rememberScrollState
import com.convxy.music.ui.utils.appTopBarWindowInsets
import androidx.compose.foundation.shape.CircleShape
import com.convxy.music.ui.utils.appTopBarWindowInsets
import androidx.compose.foundation.shape.RoundedCornerShape
import com.convxy.music.ui.utils.appTopBarWindowInsets
import androidx.compose.foundation.verticalScroll
import com.convxy.music.ui.utils.appTopBarWindowInsets
import androidx.compose.material3.Card
import com.convxy.music.ui.utils.appTopBarWindowInsets
import androidx.compose.material3.CardDefaults
import com.convxy.music.ui.utils.appTopBarWindowInsets
import androidx.compose.material3.ExperimentalMaterial3Api
import com.convxy.music.ui.utils.appTopBarWindowInsets
import androidx.compose.material3.Icon
import com.convxy.music.ui.utils.appTopBarWindowInsets
import androidx.compose.material3.IconButton
import com.convxy.music.ui.utils.appTopBarWindowInsets
import androidx.compose.material3.MaterialTheme
import com.convxy.music.ui.utils.appTopBarWindowInsets
import androidx.compose.material3.Text
import com.convxy.music.ui.utils.appTopBarWindowInsets
import androidx.compose.material3.TextButton
import com.convxy.music.ui.utils.appTopBarWindowInsets
import androidx.compose.material3.TopAppBar
import com.convxy.music.ui.utils.appTopBarWindowInsets
import androidx.compose.material3.dynamicDarkColorScheme
import com.convxy.music.ui.utils.appTopBarWindowInsets
import androidx.compose.material3.dynamicLightColorScheme
import com.convxy.music.ui.utils.appTopBarWindowInsets
import androidx.compose.material3.ripple
import com.convxy.music.ui.utils.appTopBarWindowInsets
import androidx.compose.runtime.Composable
import com.convxy.music.ui.utils.appTopBarWindowInsets
import androidx.compose.runtime.getValue
import com.convxy.music.ui.utils.appTopBarWindowInsets
import androidx.compose.runtime.mutableStateOf
import com.convxy.music.ui.utils.appTopBarWindowInsets
import androidx.compose.runtime.remember
import com.convxy.music.ui.utils.appTopBarWindowInsets
import androidx.compose.runtime.setValue
import com.convxy.music.ui.utils.appTopBarWindowInsets
import com.convxy.music.ui.component.ColorPickerDialog
import com.convxy.music.ui.utils.appTopBarWindowInsets
import androidx.compose.ui.Alignment
import com.convxy.music.ui.utils.appTopBarWindowInsets
import androidx.compose.ui.Modifier
import com.convxy.music.ui.utils.appTopBarWindowInsets
import androidx.compose.ui.draw.clip
import com.convxy.music.ui.utils.appTopBarWindowInsets
import androidx.compose.ui.geometry.Offset
import com.convxy.music.ui.utils.appTopBarWindowInsets
import androidx.compose.ui.geometry.Size
import com.convxy.music.ui.utils.appTopBarWindowInsets
import androidx.compose.ui.graphics.Color
import com.convxy.music.ui.utils.appTopBarWindowInsets
import androidx.compose.ui.graphics.graphicsLayer
import com.convxy.music.ui.utils.appTopBarWindowInsets
import androidx.compose.ui.graphics.toArgb
import com.convxy.music.ui.utils.appTopBarWindowInsets
import androidx.compose.ui.platform.LocalConfiguration
import com.convxy.music.ui.utils.appTopBarWindowInsets
import androidx.compose.ui.platform.LocalContext
import com.convxy.music.ui.utils.appTopBarWindowInsets
import androidx.compose.ui.res.painterResource
import com.convxy.music.ui.utils.appTopBarWindowInsets
import androidx.compose.ui.res.stringResource
import com.convxy.music.ui.utils.appTopBarWindowInsets
import androidx.compose.ui.semantics.contentDescription
import com.convxy.music.ui.utils.appTopBarWindowInsets
import androidx.compose.ui.semantics.semantics
import com.convxy.music.ui.utils.appTopBarWindowInsets
import androidx.compose.ui.unit.dp
import com.convxy.music.ui.utils.appTopBarWindowInsets
import androidx.navigation.NavController
import com.convxy.music.ui.utils.appTopBarWindowInsets
import com.materialkolor.PaletteStyle
import com.convxy.music.ui.utils.appTopBarWindowInsets
import com.materialkolor.rememberDynamicColorScheme
import com.convxy.music.ui.utils.appTopBarWindowInsets
import com.convxy.music.R
import com.convxy.music.ui.utils.appTopBarWindowInsets
import com.convxy.music.constants.DarkModeKey
import com.convxy.music.ui.utils.appTopBarWindowInsets
import com.convxy.music.constants.DynamicThemeKey
import com.convxy.music.ui.utils.appTopBarWindowInsets
import com.convxy.music.constants.PureBlackKey
import com.convxy.music.ui.utils.appTopBarWindowInsets
import com.convxy.music.constants.PureBlackMiniPlayerKey
import com.convxy.music.ui.utils.appTopBarWindowInsets
import com.convxy.music.constants.SelectedThemeColorKey
import com.convxy.music.constants.AppBackgroundColorKey
import com.convxy.music.constants.AppTextColorKey
import com.convxy.music.ui.utils.appTopBarWindowInsets
import com.convxy.music.ui.theme.AppleTokens
import com.convxy.music.ui.utils.appTopBarWindowInsets
import com.convxy.music.ui.theme.DefaultThemeColor
import com.convxy.music.ui.utils.appTopBarWindowInsets
import com.convxy.music.ui.theme.vivimusicTheme
import com.convxy.music.ui.utils.appTopBarWindowInsets
import com.convxy.music.utils.rememberEnumPreference
import com.convxy.music.ui.utils.appTopBarWindowInsets
import com.convxy.music.utils.rememberPreference
import androidx.compose.foundation.layout.windowInsetsPadding
import com.convxy.music.LocalPlayerAwareWindowInsets

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThemeScreen(
    navController: NavController,
) {
    val (darkMode, onDarkModeChange) = rememberEnumPreference(DarkModeKey, DarkMode.AUTO)
    val (pureBlack, onPureBlackChangeRaw) = rememberPreference(PureBlackKey, defaultValue = false)
    val (_, onPureBlackMiniPlayerChange) = rememberPreference(
        PureBlackMiniPlayerKey,
        defaultValue = false
    )

    val onPureBlackChange: (Boolean) -> Unit = { enabled ->
        onPureBlackChangeRaw(enabled)
        onPureBlackMiniPlayerChange(enabled)
    }
    val (selectedThemeColorInt, onSelectedThemeColorChange) = rememberPreference(
        SelectedThemeColorKey,
        DefaultThemeColor.toArgb()
    )
    val (_, onDynamicThemeChange) = rememberPreference(DynamicThemeKey, defaultValue = true)

    val selectedThemeColor = Color(selectedThemeColorInt)
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    // Helper function to handle color selection with dynamic theme toggle
    val handleColorSelection: (Color) -> Unit = { color ->
        onSelectedThemeColorChange(color.toArgb())
        // Enable dynamic theme only when selecting the default/dynamic color
        // Disable it when selecting any other color
        val isDynamicColor = color == DefaultThemeColor
        onDynamicThemeChange(isDynamicColor)
    }

    // Mode + color back to their defaults. Doesn't touch the home background
    // image below — that already has its own "Remove image" action, and
    // clearing it here would silently delete a file the user picked.
    val onReset: () -> Unit = {
        onDarkModeChange(DarkMode.AUTO)
        onPureBlackChange(false)
        onSelectedThemeColorChange(DefaultThemeColor.toArgb())
        onDynamicThemeChange(true)
    }

    if (isLandscape) {
        LandscapeThemeLayout(
            darkMode = darkMode,
            onDarkModeChange = onDarkModeChange,
            pureBlack = pureBlack,
            onPureBlackChange = onPureBlackChange,
            selectedThemeColor = selectedThemeColor,
            onSelectedThemeColorChange = handleColorSelection,
            onReset = onReset,
        )
    } else {
        PortraitThemeLayout(
            darkMode = darkMode,
            onDarkModeChange = onDarkModeChange,
            pureBlack = pureBlack,
            onPureBlackChange = onPureBlackChange,
            selectedThemeColor = selectedThemeColor,
            onSelectedThemeColorChange = handleColorSelection,
            onReset = onReset,
        )
    }

    TopAppBar(
            windowInsets = appTopBarWindowInsets(),
        title = { Text(stringResource(R.string.theme_colors)) },
        navigationIcon = {
            IconButton(onClick = { navController.navigateUp() }) {
                Icon(
                    painter = painterResource(R.drawable.arrow_back),
                    contentDescription = stringResource(R.string.cd_back)
                )
            }
        }
    )
}

@Composable
fun PortraitThemeLayout(
    darkMode: DarkMode,
    onDarkModeChange: (DarkMode) -> Unit,
    pureBlack: Boolean,
    onPureBlackChange: (Boolean) -> Unit,
    selectedThemeColor: Color,
    onSelectedThemeColorChange: (Color) -> Unit,
    onReset: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            // Was a hardcoded PaddingValues(0.dp) from the caller, so content
            // started at y=0 under the opaque TopAppBar below — the phone
            // mockup's top edge rendered hidden behind the bar. This is the
            // same top-bar-aware inset every other settings screen already
            // uses (see GlassEffectSettings.kt).
            .windowInsetsPadding(LocalPlayerAwareWindowInsets.current)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        Box(
            modifier = Modifier
                .width(120.dp)
                .height(240.dp),
            contentAlignment = Alignment.Center
        ) {
            ThemeMockupPortrait(
                darkMode = darkMode,
                pureBlack = pureBlack,
                themeColor = selectedThemeColor
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        ThemeControls(
            darkMode = darkMode,
            onDarkModeChange = onDarkModeChange,
            pureBlack = pureBlack,
            onPureBlackChange = onPureBlackChange,
            onReset = onReset,
        )

        Spacer(modifier = Modifier.height(16.dp))

        HomeBackgroundControls()


        Spacer(modifier = Modifier.height(120.dp))
    }
}

@Composable
fun LandscapeThemeLayout(
    darkMode: DarkMode,
    onDarkModeChange: (DarkMode) -> Unit,
    pureBlack: Boolean,
    onPureBlackChange: (Boolean) -> Unit,
    selectedThemeColor: Color,
    onSelectedThemeColorChange: (Color) -> Unit,
    onReset: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxSize()
            // Same top-bar-hidden-mockup fix as PortraitThemeLayout above.
            .windowInsetsPadding(LocalPlayerAwareWindowInsets.current)
    ) {
        Column(
            modifier = Modifier
                .weight(0.4f)
                .fillMaxHeight()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .heightIn(max = 300.dp),
                contentAlignment = Alignment.Center
            ) {
                ThemeMockup(
                    darkMode = darkMode,
                    pureBlack = pureBlack,
                    themeColor = selectedThemeColor
                )
            }
        }

        Column(
            modifier = Modifier
                .weight(0.6f)
                .fillMaxHeight()
                .verticalScroll(rememberScrollState())
                .padding(end = 16.dp, top = 16.dp, bottom = 16.dp)
        ) {
            ThemeControls(
                darkMode = darkMode,
                onDarkModeChange = onDarkModeChange,
                pureBlack = pureBlack,
                onPureBlackChange = onPureBlackChange,
                onReset = onReset,
            )

            Spacer(modifier = Modifier.height(16.dp))

            HomeBackgroundControls()


            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
fun ThemeControls(
    darkMode: DarkMode,
    onDarkModeChange: (DarkMode) -> Unit,
    pureBlack: Boolean,
    onPureBlackChange: (Boolean) -> Unit,
    onReset: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = stringResource(R.string.theme_mode),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // System mode (AUTO)
                    ModeCircle(
                        darkMode = darkMode,
                        pureBlack = pureBlack,
                        targetMode = DarkMode.AUTO,
                        targetPureBlack = pureBlack,
                        onClick = {
                            onDarkModeChange(DarkMode.AUTO)
                        },
                        showIcon = true
                    )
                    
                    // Vertical divider to separate System from manual modes
                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .height(32.dp)
                            .background(MaterialTheme.colorScheme.outlineVariant)
                    )
                    
                    // Manual modes (Light, Dark, Pure Black)
                    ModeCircle(
                        darkMode = darkMode,
                        pureBlack = pureBlack,
                        targetMode = DarkMode.OFF,
                        targetPureBlack = false,
                        onClick = {
                            onDarkModeChange(DarkMode.OFF)
                            onPureBlackChange(false)
                        },
                        showIcon = false
                    )
                    
                    ModeCircle(
                        darkMode = darkMode,
                        pureBlack = pureBlack,
                        targetMode = DarkMode.ON,
                        targetPureBlack = false,
                        onClick = {
                            onDarkModeChange(DarkMode.ON)
                            onPureBlackChange(false)
                        },
                        showIcon = false
                    )
                    
                    ModeCircle(
                        darkMode = darkMode,
                        pureBlack = pureBlack,
                        targetMode = DarkMode.ON,
                        targetPureBlack = true,
                        onClick = {
                            onDarkModeChange(DarkMode.ON)
                            onPureBlackChange(true)
                        },
                        showIcon = false
                    )
                }
            }

            AppBackgroundTextColorSection()

            TextButton(
                onClick = onReset,
                modifier = Modifier.align(Alignment.End),
            ) {
                Text(stringResource(R.string.reset))
            }
        }
    }
}

/**
 * App-wide background/text color, independent of Liquid Glass. Replaces the
 * old accent-color palette picker in this screen — the seed-color mechanism
 * it wrote to ([SelectedThemeColorKey]/[DynamicThemeKey]) is untouched and
 * still drives the app's Material color scheme, this just removes its picker
 * UI here in favor of direct background/text color control.
 *
 * Both preferences default to 0 ("unset"): every call site that reads them
 * (Home's plain background, the shared nav chrome's non-glass fallback)
 * falls back to its exact pre-existing hardcoded color, so a user who never
 * opens this section sees no visual change at all.
 */
@Composable
private fun AppBackgroundTextColorSection() {
    val (backgroundColorInt, onBackgroundColorChange) = rememberPreference(AppBackgroundColorKey, defaultValue = 0)
    val (textColorInt, onTextColorChange) = rememberPreference(AppTextColorKey, defaultValue = 0)

    // "Unset" swatch shown here matches what every consuming call site's own
    // fallback actually resolves to (surfaceContainerHigh / onSurface), so
    // the picker never shows a color that doesn't match reality.
    val backgroundColor = if (backgroundColorInt == 0) MaterialTheme.colorScheme.surfaceContainerHigh else Color(backgroundColorInt)
    val textColor = if (textColorInt == 0) MaterialTheme.colorScheme.onSurface else Color(textColorInt)

    var showBackgroundPicker by remember { mutableStateOf(false) }
    var showTextPicker by remember { mutableStateOf(false) }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = stringResource(R.string.app_background_text_color),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = stringResource(R.string.app_background_text_color_desc),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            AppColorSwatchButton(
                label = stringResource(R.string.background_color),
                color = backgroundColor,
                onClick = { showBackgroundPicker = true },
                modifier = Modifier.weight(1f),
            )
            AppColorSwatchButton(
                label = stringResource(R.string.text_color),
                color = textColor,
                onClick = { showTextPicker = true },
                modifier = Modifier.weight(1f),
            )
        }
    }

    if (showBackgroundPicker) {
        ColorPickerDialog(
            initialColor = backgroundColor,
            title = stringResource(R.string.background_color),
            onDismiss = { showBackgroundPicker = false },
            onConfirm = {
                onBackgroundColorChange(it.toArgb())
                showBackgroundPicker = false
            },
        )
    }

    if (showTextPicker) {
        ColorPickerDialog(
            initialColor = textColor,
            title = stringResource(R.string.text_color),
            onDismiss = { showTextPicker = false },
            onConfirm = {
                onTextColorChange(it.toArgb())
                showTextPicker = false
            },
        )
    }
}

@Composable
private fun AppColorSwatchButton(
    label: String,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(color)
                .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f), CircleShape)
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}

@Composable
fun ModeCircle(
    darkMode: DarkMode,
    pureBlack: Boolean,
    targetMode: DarkMode,
    targetPureBlack: Boolean,
    showIcon: Boolean,
    onClick: () -> Unit
) {
    val context = LocalContext.current
    val isSystemDark = isSystemInDarkTheme()
    val isSelected = darkMode == targetMode && pureBlack == targetPureBlack
    
    val effectiveDark = when (targetMode) {
        DarkMode.AUTO -> isSystemDark
        DarkMode.ON -> true
        DarkMode.OFF -> false
    }
    
    // Use actual system colors for AUTO mode on Android 12+
    val modeColorScheme = if (targetMode == DarkMode.AUTO && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        if (effectiveDark) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
    } else {
        rememberDynamicColorScheme(
            seedColor = DefaultThemeColor,
            isDark = effectiveDark,
            style = PaletteStyle.TonalSpot
        )
    }
    
    val fillColor = when {
        targetPureBlack -> Color.Black
        effectiveDark -> modeColorScheme.surface
        else -> modeColorScheme.surface
    }
    
    // Animated border width
    val borderWidth by animateDpAsState(
        targetValue = if (isSelected) 3.dp else 0.dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "borderWidth"
    )
    
    // Animated scale for the entire circle
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.05f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "scale"
    )
    
    val interactionSource = remember { MutableInteractionSource() }
    
    val contentDesc = when {
        targetPureBlack -> stringResource(R.string.cd_pure_black_mode)
        targetMode == DarkMode.OFF -> stringResource(R.string.cd_light_mode)
        targetMode == DarkMode.ON -> stringResource(R.string.cd_dark_mode)
        else -> stringResource(R.string.cd_system_mode)
    }
    
    Box(
        modifier = Modifier
            .size(48.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clip(CircleShape)
            .background(fillColor)
            .then(
                if (borderWidth > 0.dp) {
                    Modifier.border(
                        width = borderWidth,
                        color = MaterialTheme.colorScheme.inversePrimary,
                        shape = CircleShape
                    )
                } else {
                    Modifier
                }
            )
            .bounceClick(
                interactionSource = interactionSource,
                indication = ripple(),
                onClick = onClick
            )
            .semantics {
                contentDescription = contentDesc
            },
        contentAlignment = Alignment.Center
    ) {
        when {
            showIcon -> {
                Icon(
                    painter = painterResource(R.drawable.sync),
                    contentDescription = null,
                    tint = modeColorScheme.onSurface,
                    modifier = Modifier.size(20.dp)
                )
            }
            isSelected -> {
                AnimatedVisibility(
                    visible = isSelected,
                    enter = fadeIn(animationSpec = tween(300)) + scaleIn(
                        initialScale = 0.3f,
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessMedium
                        )
                    ),
                    exit = fadeOut(animationSpec = tween(150)) + scaleOut(
                        targetScale = 0.3f,
                        animationSpec = tween(150)
                    )
                ) {
                    Icon(
                        painter = painterResource(R.drawable.check),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.inversePrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun ThemeMockup(
    darkMode: DarkMode,
    pureBlack: Boolean,
    themeColor: Color
) {
    val isSystemDark = isSystemInDarkTheme()
    val useDark = when (darkMode) {
        DarkMode.AUTO -> isSystemDark
        DarkMode.ON -> true
        DarkMode.OFF -> false
    }

    vivimusicTheme(
        darkTheme = useDark,
        pureBlack = pureBlack,
        themeColor = themeColor
    ) {
        Card(
            modifier = Modifier
                .fillMaxSize()
                .aspectRatio(9f / 18f),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp)
                        .background(MaterialTheme.colorScheme.surfaceContainer)
                        .padding(10.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(18.dp)
                                .background(MaterialTheme.colorScheme.primary, CircleShape)
                        )
                        Box(
                            modifier = Modifier
                                .size(18.dp)
                                .background(MaterialTheme.colorScheme.secondary, CircleShape)
                        )
                    }
                }

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(10.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(32.dp)
                            .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(6.dp))
                    )
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(40.dp)
                                .background(MaterialTheme.colorScheme.secondary, RoundedCornerShape(6.dp))
                        )
                        
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(40.dp)
                                .background(MaterialTheme.colorScheme.tertiary, RoundedCornerShape(6.dp))
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    contentAlignment = Alignment.BottomEnd
                ) {
                    Box(
                        modifier = Modifier
                            .size(30.dp)
                            .background(MaterialTheme.colorScheme.primaryContainer, CircleShape)
                    )
                }
            }
        }
    }
}

@Composable
fun ThemeMockupPortrait(
    darkMode: DarkMode,
    pureBlack: Boolean,
    themeColor: Color
) {
    val isSystemDark = isSystemInDarkTheme()
    val useDark = when (darkMode) {
        DarkMode.AUTO -> isSystemDark
        DarkMode.ON -> true
        DarkMode.OFF -> false
    }

    vivimusicTheme(
        darkTheme = useDark,
        pureBlack = pureBlack,
        themeColor = themeColor
    ) {
        Card(
            modifier = Modifier
                .fillMaxSize(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // Header (20% of height)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(0.2f)
                        .background(MaterialTheme.colorScheme.surfaceContainer)
                        .padding(6.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .background(MaterialTheme.colorScheme.primary, CircleShape)
                        )
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .background(MaterialTheme.colorScheme.secondary, CircleShape)
                        )
                    }
                }

                // Main Content (60% of height)
                Column(
                    modifier = Modifier
                        .weight(0.6f)
                        .padding(6.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(4.dp))
                    )
                    
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1.2f),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .background(MaterialTheme.colorScheme.secondary, RoundedCornerShape(4.dp))
                        )
                        
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .background(MaterialTheme.colorScheme.tertiary, RoundedCornerShape(4.dp))
                        )
                    }
                }

                // FAB Area (20% of height)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(0.2f)
                        .padding(6.dp),
                    contentAlignment = Alignment.BottomEnd
                ) {
                    Box(
                        modifier = Modifier
                            .size(18.dp)
                            .background(MaterialTheme.colorScheme.primaryContainer, CircleShape)
                    )
                }
            }
        }
    }
}
