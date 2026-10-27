package com.convx.music.ui.screens.equalizer.axion

import com.convx.music.ui.component.sf
import com.convx.music.ui.component.SymbolIcon
import com.convx.music.ui.ios.compat.AppTheme
import com.convx.music.ui.ios.compat.SegmentedButton
import com.convx.music.ui.ios.compat.SingleChoiceSegmentedButtonRow
import com.convx.music.ui.ios.compat.OutlinedTextField as IosOutlinedTextField

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Replay
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import com.convx.music.ui.utils.bounceClick
import com.convx.music.ui.utils.combinedBounceClick
import androidx.compose.material3.*
import com.convx.music.ui.component.GlassSwitchCompat as Switch
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.layout
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel
import com.convx.music.R
import com.convx.music.eq.data.SavedEQProfile
import com.convx.music.ui.component.IosSettingsGroup
import com.convx.music.ui.component.IosSettingsItem
import com.convx.music.ui.utils.backToMain
import kotlin.math.abs
import racra.compose.smooth_corner_rect_library.AbsoluteSmoothCornerShape
import com.convx.music.ui.component.ScreenPage
import com.convx.music.constants.BitPerfectKey
import com.convx.music.constants.SpatialAudioKey
import com.convx.music.utils.rememberPreference
import com.convx.music.eq.data.ParametricEQBand
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import com.convx.music.ui.component.DefaultDialog
import com.convx.music.ui.ios.compat.Text as Text
import com.convx.music.ui.ios.compat.TextButton as TextButton

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun AxionEqScreen(
    onBackClick: () -> Unit,
    viewModel: AxionEqViewModel = koinViewModel()
) {
    val enabled by viewModel.enabled.collectAsStateWithLifecycle()
    val bandGains by viewModel.bandGains.collectAsStateWithLifecycle()
    val mode by viewModel.mode.collectAsStateWithLifecycle()
    val bands by viewModel.bands.collectAsStateWithLifecycle()
    val preamp by viewModel.preamp.collectAsStateWithLifecycle()
    val (spatialAudio, onSpatialAudioChange) = rememberPreference(SpatialAudioKey, false)
    val (bitPerfect) = rememberPreference(BitPerfectKey, false)
    val isImporting by viewModel.isImporting.collectAsStateWithLifecycle()
    val importSucceeded by viewModel.importSucceeded.collectAsStateWithLifecycle()
    val importLauncher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        uri?.let(viewModel::importProfile)
    }
    if (importSucceeded != null) {
        DefaultDialog(
            onDismiss = viewModel::dismissImportResult,
            title = { Text(stringResource(if (importSucceeded == true) R.string.import_profile else R.string.import_error_title)) },
            buttons = {
                TextButton(onClick = viewModel::dismissImportResult) { Text(stringResource(android.R.string.ok)) }
            },
        ) {
            Text(stringResource(if (importSucceeded == true) R.string.eq_import_success else R.string.eq_import_failure))
        }
    }

    ScreenPage(
        title = stringResource(R.string.convx_equalizer),
        onBack = onBackClick,
        horizontalPadding = 16.dp,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
            IosSettingsGroup(items = listOf(IosSettingsItem(
                icon = painterResource(R.drawable.equalizer),
                title = { Text(stringResource(R.string.import_profile)) },
                description = { Text(stringResource(if (isImporting) R.string.eq_import_progress else R.string.eq_import_description)) },
                onClick = { if (!isImporting) importLauncher.launch(arrayOf("text/*", "application/octet-stream")) },
            )))
            IosSettingsGroup(
                items = listOf(
                    IosSettingsItem(
                        icon = androidx.compose.ui.res.painterResource(R.drawable.equalizer),
                        title = { Text(stringResource(R.string.eq_enable_title)) },
                        description = { Text(stringResource(R.string.eq_enable_summary)) },
                        trailingContent = {
                            Switch(
                                checked = enabled,
                                onCheckedChange = { viewModel.setEnabled(it) },
                                thumbContent = {
                                    SymbolIcon(symbol = if (enabled) sf("checkmark") else sf("xmark")
                                        , contentDescription = null, modifier = Modifier.size(SwitchDefaults.IconSize))
                                }
                            )
                        },
                        onClick = { viewModel.setEnabled(!enabled) }
                    ),
                    IosSettingsItem(
                        icon = painterResource(R.drawable.equalizer),
                        title = { Text(stringResource(R.string.spatial_audio)) },
                        description = { Text(stringResource(if (bitPerfect) R.string.spatial_audio_disabled_by_bit_perfect else R.string.spatial_audio_description)) },
                        trailingContent = {
                            Switch(checked = spatialAudio && !bitPerfect, onCheckedChange = onSpatialAudioChange, enabled = !bitPerfect)
                        },
                        onClick = { if (!bitPerfect) onSpatialAudioChange(!spatialAudio) },
                    )
                )
            )

            SingleChoiceSegmentedButtonRow(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
            ) {
                SegmentedButton(
                    selected = mode == 0,
                    onClick = { viewModel.setMode(0) },
                    label = { Text(stringResource(R.string.eq_simple)) },
                )
                SegmentedButton(
                    selected = mode == 1,
                    onClick = { viewModel.setMode(1) },
                    label = { Text(stringResource(R.string.eq_advanced)) },
                )
            }

            AnimatedContent(
                targetState = mode,
                transitionSpec = {
                    fadeIn().togetherWith(fadeOut()).using(SizeTransform(clip = false))
                },
                label = "eqMode",
            ) { currentMode ->
                val isDirty by viewModel.isDirty.collectAsStateWithLifecycle()
                var showSaveDialog by remember { mutableStateOf(false) }

                if (showSaveDialog) {
                    SavePresetDialog(
                        onDismiss = { showSaveDialog = false },
                        onSave = { name ->
                            viewModel.saveCustomProfile(name)
                            showSaveDialog = false
                        }
                    )
                }

                when (currentMode) {
                    0 -> SimpleEqMode(
                        bandGains = bandGains,
                        enabled = enabled,
                        viewModel = viewModel,
                        isDirty = isDirty,
                        onSaveClick = { showSaveDialog = true }
                    )
                    else -> AdvancedEqMode(
                        bands = bands,
                        preamp = preamp,
                        onPreampChange = { viewModel.setPreamp(it) },
                        onParametersChange = viewModel::setBandParameters,
                        onSaveClick = { showSaveDialog = true },
                        enabled = enabled,
                        onBandChange = { band, value ->
                            viewModel.setBandGain(band, value)
                        },
                        onReset = {
                            viewModel.reset()
                        }
                    )
                }
            }

            if (mode == 1) {
                val profiles by viewModel.customProfiles.collectAsStateWithLifecycle()
                profiles.forEach { profile ->
                    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        TextButton(onClick = { viewModel.selectProfile(profile) }, modifier = Modifier.weight(1f)) {
                            Text(profile.name)
                        }
                        TextButton(onClick = { viewModel.deleteProfiles(listOf(profile.id)) }) {
                            Text(stringResource(R.string.delete))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(60.dp))
    }
}

@Composable
private fun SimpleEqMode(
    bandGains: FloatArray,
    enabled: Boolean,
    viewModel: AxionEqViewModel,
    isDirty: Boolean,
    onSaveClick: () -> Unit
) {
    // Ported logic from AxionFx SimpleEqMode
    var bass by remember { mutableFloatStateOf(0f) }
    var mid by remember { mutableFloatStateOf(0f) }
    var treble by remember { mutableFloatStateOf(0f) }

    fun syncFromBands() {
        // Use more stable inverse mapping to avoid cross-talk drift
        bass = bandGains[1] / 50f
        mid = (bandGains[4] + bandGains[5]) / 2f / 50f
        treble = bandGains[8] / 50f
    }

    fun applyTriangle() {
        val bv = (bass * 50f).coerceIn(-600f, 600f)
        val mv = (mid * 50f).coerceIn(-600f, 600f)
        val tv = (treble * 50f).coerceIn(-600f, 600f)
        val newGains = FloatArray(10)
        
        // Smoother, more organic transition across the spectrum
        newGains[0] = bv * 1.1f
        newGains[1] = bv * 1.0f
        newGains[2] = bv * 0.7f + mv * 0.3f
        newGains[3] = bv * 0.2f + mv * 0.8f
        newGains[4] = mv * 1.0f
        newGains[5] = mv * 1.0f
        newGains[6] = mv * 0.8f + tv * 0.2f
        newGains[7] = mv * 0.3f + tv * 0.7f
        newGains[8] = tv * 1.0f
        newGains[9] = tv * 1.15f // Extended high-end sparkle
        
        viewModel.setBandsGains(newGains, fromUser = true)
    }

    LaunchedEffect(bandGains) {
        syncFromBands()
    }

    val convxPresets = listOf(
        R.string.eq_preset_flat to floatArrayOf(0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f),
        R.string.eq_preset_convx_signature to floatArrayOf(150f, 100f, 50f, 0f, -20f, 0f, 80f, 150f, 200f, 150f),
        R.string.eq_preset_acoustic to floatArrayOf(150f, 150f, 50f, 75f, 100f, 75f, 125f, 175f, 150f, 75f),
        R.string.eq_preset_spatial to floatArrayOf(75f, 50f, 25f, -50f, -25f, 0f, 50f, 75f, 100f, 75f),
        R.string.eq_preset_bass_boost to floatArrayOf(500f, 400f, 250f, 100f, 0f, -50f, 0f, 100f, 200f, 300f),
        R.string.eq_preset_pure_clarity to floatArrayOf(-100f, -50f, 0f, 50f, 150f, 250f, 300f, 250f, 150f, 100f),
        R.string.eq_preset_soft_bass to floatArrayOf(200f, 180f, 140f, 80f, 30f, 20f, 60f, 90f, 110f, 130f),
        R.string.eq_preset_electronic to floatArrayOf(350f, 280f, 120f, -50f, -150f, 50f, 180f, 300f, 400f, 500f),
        R.string.eq_preset_rock to floatArrayOf(300f, 220f, 150f, 50f, -100f, 120f, 200f, 250f, 320f, 380f),
        R.string.eq_preset_pop to floatArrayOf(-150f, 0f, 100f, 180f, 250f, 220f, 150f, 80f, -50f, -120f),
        R.string.eq_preset_jazz to floatArrayOf(150f, 100f, 60f, 140f, 200f, 180f, 120f, 180f, 220f, 200f),
        R.string.eq_preset_voice to floatArrayOf(-250f, -150f, 0f, 200f, 400f, 380f, 200f, 120f, 0f, -120f),
    )

    val dolbyPresets = listOf(
        R.string.eq_preset_dolby_open to floatArrayOf(150f, 180f, 220f, 180f, 160f, 210f, 250f, 280f, 180f, 80f),
        R.string.eq_preset_dolby_rich to floatArrayOf(100f, 160f, 200f, 220f, 280f, 260f, 240f, 200f, 150f, 50f),
        R.string.eq_preset_dolby_focused to floatArrayOf(-300f, -50f, 130f, 180f, 220f, 120f, 140f, 100f, -50f, -300f),
    )

    val diracPresets = listOf(
        R.string.eq_preset_dirac_music to floatArrayOf(200f, 140f, 80f, 0f, 30f, 80f, 140f, 200f, 280f, 350f),
        R.string.eq_preset_dirac_movie to floatArrayOf(300f, 250f, 150f, 0f, 70f, 120f, 180f, 250f, 320f, 400f),
        R.string.eq_preset_dirac_game to floatArrayOf(150f, 250f, 200f, 0f, 80f, 150f, 300f, 450f, 400f, 280f),
    )

    val customProfiles by viewModel.customProfiles.collectAsStateWithLifecycle()
    var showManageDialog by remember { mutableStateOf(false) }

    if (showManageDialog) {
        ManagePresetsDialog(
            customProfiles = customProfiles,
            onDismiss = { showManageDialog = false },
            onDeleteSelected = { ids ->
                viewModel.deleteProfiles(ids)
                showManageDialog = false
            }
        )
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp), 
    ) {
        CircularEqControl(
            bass = bass, mid = mid, treble = treble,
            enabled = enabled,
            onBassChange = { bass = it; applyTriangle() },
            onMidChange = { mid = it; applyTriangle() },
            onTrebleChange = { treble = it; applyTriangle() },
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .padding(horizontal = 8.dp)
                .aspectRatio(1f),
        )

        AnimatedVisibility(
            visible = isDirty && enabled,
            modifier = Modifier.align(Alignment.CenterHorizontally),
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            OutlinedButton(
                onClick = onSaveClick,
                modifier = Modifier.padding(bottom = 8.dp),
                shape = AppTheme.shapes.medium,
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp)
            ) {
                SymbolIcon(symbol = sf("checkmark"), contentDescription = null, size = 18.dp)
                Spacer(Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.eq_save),
                    style = AppTheme.typography.labelLarge
                )
            }
        }

        if (customProfiles.isNotEmpty()) {
            PresetSection(
                title = stringResource(R.string.eq_label_custom),
                presets = customProfiles.map { -1 to it.bands.map { it.gain.toFloat() * 50f }.toFloatArray() },
                presetNames = customProfiles.map { it.name },
                enabled = enabled,
                viewModel = viewModel,
                bandGains = bandGains,
                onEditClick = { showManageDialog = true },
                profiles = customProfiles,
            )
        }

        convxPresets.chunked(4).forEach { chunk ->
            PresetSection(
                title = if (convxPresets.first() in chunk) stringResource(R.string.eq_label_convx) else "",
                presets = chunk,
                enabled = enabled,
                viewModel = viewModel,
                bandGains = bandGains
            )
        }
        PresetSection(stringResource(R.string.eq_label_dolby), dolbyPresets, null, enabled, viewModel, bandGains)
        PresetSection(stringResource(R.string.eq_label_dirac), diracPresets, null, enabled, viewModel, bandGains)
    }
}

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun PresetSection(
    title: String,
    presets: List<Pair<Int, FloatArray>>,
    presetNames: List<String>? = null,
    enabled: Boolean,
    viewModel: AxionEqViewModel,
    bandGains: FloatArray,
    onEditClick: (() -> Unit)? = null,
    profiles: List<SavedEQProfile>? = null,
) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (title.isNotEmpty()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = title,
                    style = AppTheme.typography.titleSmall,
                    color = AppTheme.colorScheme.primary,
                    modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
                )
                
                if (onEditClick != null && enabled) {
                    androidx.compose.material3.IconButton(
                        onClick = onEditClick,
                        modifier = Modifier.size(24.dp)
                    ) {
                        SymbolIcon(symbol = sf("pencil"), contentDescription = null, size = 16.dp, tint = AppTheme.colorScheme.primary)
                    }
                }
            }
        }

        SingleChoiceSegmentedButtonRow(
            modifier = Modifier.fillMaxWidth(),
        ) {
            presets.forEachIndexed { index, (nameRes, bands) ->
                val name = presetNames?.getOrNull(index) ?: stringResource(nameRes)
                val isSelected = remember(bandGains) {
                    bandGains.size == bands.size &&
                    bandGains.zip(bands).all { (g, b) -> abs(g - b) < 10f }
                }

                SegmentedButton(
                    selected = isSelected,
                    onClick = {
                        if (enabled) {
                            val profile = profiles?.getOrNull(index)
                            if (profile != null) viewModel.selectProfile(profile) else viewModel.setBandsGains(bands)
                        }
                    },
                    enabled = enabled,
                    label = {
                        Text(
                            text = name,
                            style = AppTheme.typography.labelSmall,
                            maxLines = 1
                        )
                    },
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SavePresetDialog(
    onDismiss: () -> Unit,
    onSave: (String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    
    val cardShape = AbsoluteSmoothCornerShape(30.dp, 60)
    val blockShape = AbsoluteSmoothCornerShape(22.dp, 60)
    val actionShape = AbsoluteSmoothCornerShape(18.dp, 60)

    BasicAlertDialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .widthIn(max = 320.dp),
            shape = cardShape,
            color = AppTheme.colorScheme.surfaceContainerHigh,
            tonalElevation = 8.dp,
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                Surface(
                    shape = blockShape,
                    color = AppTheme.colorScheme.surfaceContainer,
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        Text(
                            text = stringResource(R.string.eq_save_dialog_title),
                            style = AppTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                        
                        IosOutlinedTextField(
                            value = name,
                            onValueChange = { name = it },
                            placeholder = { Text(stringResource(R.string.eq_save_name_hint)) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            shape = AppTheme.shapes.medium,
                            textStyle = AppTheme.typography.bodyMedium,
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End),
                ) {
                    TextButton(
                        onClick = onDismiss,
                        shape = actionShape,
                    ) {
                        Text(text = stringResource(R.string.cancel))
                    }
                    
                    OutlinedButton(
                        onClick = { if (name.isNotBlank()) onSave(name) },
                        enabled = name.isNotBlank(),
                        shape = actionShape,
                    ) {
                        Text(text = stringResource(R.string.eq_save))
                    }
                }
            }
        }
    }
}

@Composable
private fun AdvancedEqMode(
    bands: List<ParametricEQBand>,
    preamp: Double,
    onPreampChange: (Double) -> Unit,
    onParametersChange: (Int, Double, Double) -> Unit,
    onSaveClick: () -> Unit,
    enabled: Boolean,
    onBandChange: (Int, Float) -> Unit,
    onReset: () -> Unit,
) {
    var editingBand by remember { mutableStateOf<Int?>(null) }
    editingBand?.let { index ->
        BandParametersDialog(
            band = bands[index],
            onDismiss = { editingBand = null },
            onApply = { frequency, q ->
                onParametersChange(index, frequency, q)
                editingBand = null
            },
        )
    }

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(AppTheme.shapes.extraLarge)
                .background(AppTheme.colorScheme.surfaceContainerLow)
                .padding(vertical = 16.dp)
        ) {
            Row(
                modifier = Modifier
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                bands.forEachIndexed { band, parameters ->
                    EqBandSlider(
                        label = stringResource(R.string.eq_band_frequency, parameters.frequency),
                        value = (parameters.gain * 50).toFloat(),
                        enabled = enabled,
                        onValueChange = { onBandChange(band, it) },
                        onEdit = { editingBand = band },
                        q = parameters.q,
                    )
                }
            }
        }

        Text(stringResource(R.string.eq_preamp_value, preamp), style = AppTheme.typography.bodyMedium)
        Slider(value = preamp.toFloat().coerceIn(-24f, 0f), onValueChange = { onPreampChange(it.toDouble()) }, valueRange = -24f..0f, enabled = enabled)

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
        ) {
            OutlinedButton(onClick = onReset) {
                SymbolIcon(symbol = sf("arrow.counterclockwise"), contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(stringResource(R.string.eq_reset))
            }
            Spacer(Modifier.width(8.dp))
            OutlinedButton(onClick = onSaveClick) { Text(stringResource(R.string.eq_save)) }
        }
    }
}

@Composable
private fun EqBandSlider(
    label: String,
    value: Float,
    enabled: Boolean,
    onValueChange: (Float) -> Unit,
    onEdit: () -> Unit,
    q: Double,
) {
    Column(
        modifier = Modifier.width(56.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "%.1f".format(value / 50f),
            style = AppTheme.typography.labelSmall,
            color = if (enabled) AppTheme.colorScheme.primary
            else AppTheme.colorScheme.outline,
            textAlign = TextAlign.Center,
        )

        Spacer(modifier = Modifier.height(4.dp))

        Box(
            modifier = Modifier.height(200.dp),
            contentAlignment = Alignment.Center,
        ) {
            Slider(
                value = value,
                onValueChange = onValueChange,
                valueRange = -600f..600f,
                enabled = enabled,
                modifier = Modifier
                    .width(200.dp)
                    .layout { measurable, constraints ->
                        val placeable = measurable.measure(
                            constraints.copy(
                                minWidth = constraints.minHeight,
                                maxWidth = constraints.maxHeight,
                            )
                        )
                        layout(placeable.height, placeable.width) {
                            placeable.place(
                                -placeable.width / 2 + placeable.height / 2,
                                placeable.width / 2 - placeable.height / 2
                            )
                        }
                    }
                    .graphicsLayer { rotationZ = -90f },
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        TextButton(onClick = onEdit, enabled = enabled, contentPadding = PaddingValues(0.dp)) {
            Text(label, style = AppTheme.typography.labelSmall, textAlign = TextAlign.Center)
        }
        Text(stringResource(R.string.eq_band_q, q), style = AppTheme.typography.labelSmall)
    }
}

@Composable
private fun BandParametersDialog(band: ParametricEQBand, onDismiss: () -> Unit, onApply: (Double, Double) -> Unit) {
    var frequency by remember(band) { mutableStateOf(band.frequency.toString()) }
    var q by remember(band) { mutableStateOf(band.q.toString()) }
    val frequencyValue = frequency.toDoubleOrNull()
    val qValue = q.toDoubleOrNull()
    val valid = frequencyValue != null && frequencyValue.isFinite() && frequencyValue in 20.0..20000.0 &&
        qValue != null && qValue.isFinite() && qValue in 0.1..20.0
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.eq_edit_band)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                IosOutlinedTextField(value = frequency, onValueChange = { frequency = it }, label = { Text(stringResource(R.string.eq_frequency_hint)) }, singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal))
                IosOutlinedTextField(value = q, onValueChange = { q = it }, label = { Text(stringResource(R.string.eq_q_hint)) }, singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal))
            }
        },
        confirmButton = { TextButton(onClick = { if (valid) onApply(frequencyValue, qValue) }, enabled = valid) { Text(stringResource(R.string.eq_save)) } },
        dismissButton = { TextButton(onClick = onDismiss) { Text(stringResource(R.string.cancel)) } },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ManagePresetsDialog(
    customProfiles: List<SavedEQProfile>,
    onDismiss: () -> Unit,
    onDeleteSelected: (List<String>) -> Unit
) {
    val selectedIds = remember { mutableStateListOf<String>() }
    
    val cardShape = AbsoluteSmoothCornerShape(30.dp, 60)
    val blockShape = AbsoluteSmoothCornerShape(22.dp, 60)
    val actionShape = AbsoluteSmoothCornerShape(18.dp, 60)

    BasicAlertDialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .widthIn(max = 320.dp),
            shape = cardShape,
            color = AppTheme.colorScheme.surfaceContainerHigh,
            tonalElevation = 8.dp,
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                Surface(
                    shape = blockShape,
                    color = AppTheme.colorScheme.surfaceContainer,
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        Text(
                            text = stringResource(R.string.eq_manage_presets),
                            style = AppTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                        )
                        
                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 4.dp),
                            color = AppTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                        )

                        if (customProfiles.isEmpty()) {
                            Text(
                                text = stringResource(R.string.eq_no_custom_presets),
                                style = AppTheme.typography.bodyMedium,
                                color = AppTheme.colorScheme.onSurfaceVariant
                            )
                        } else {
                            LazyColumn(
                                modifier = Modifier.heightIn(max = 300.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                items(customProfiles, key = { it.id }) { profile ->
                                    val isSelected = selectedIds.contains(profile.id)
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(AppTheme.shapes.small)
                                            .bounceClick {
                                                if (isSelected) selectedIds.remove(profile.id)
                                                else selectedIds.add(profile.id)
                                            }
                                            .padding(vertical = 4.dp)
                                    ) {
                                        Checkbox(
                                            checked = isSelected,
                                            onCheckedChange = {
                                                if (it == true) selectedIds.add(profile.id)
                                                else selectedIds.remove(profile.id)
                                            }
                                        )
                                        Spacer(Modifier.width(8.dp))
                                        Text(
                                            text = profile.name,
                                            style = AppTheme.typography.bodyLarge,
                                            color = AppTheme.colorScheme.onSurface
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                HorizontalDivider(
                    modifier = Modifier.padding(bottom = 4.dp),
                    color = AppTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End),
                ) {
                    TextButton(onClick = onDismiss, shape = actionShape) {
                        Text(text = stringResource(R.string.cancel))
                    }
                    
                    if (selectedIds.isNotEmpty()) {
                        OutlinedButton(
                            onClick = { onDeleteSelected(selectedIds.toList()) },
                            shape = actionShape,
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = AppTheme.colorScheme.error
                            )
                        ) {
                            Text(text = stringResource(R.string.eq_delete_selected))
                        }
                    }
                }
            }
        }
    }
}
