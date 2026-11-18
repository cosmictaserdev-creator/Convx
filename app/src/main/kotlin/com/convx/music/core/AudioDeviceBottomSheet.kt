@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)

package com.convx.music.core

import com.convx.music.ui.component.volumeBars
import com.convx.music.ui.component.sf
import com.convx.music.ui.component.SymbolIcon
import com.convx.music.ui.ios.foundation.IosTheme
import com.convx.music.ui.ios.foundation.iosMenuSurface
import com.convx.music.ui.player.Centered
import com.convx.music.ui.player.V2Floating
import com.convx.music.ui.ios.foundation.LocalMenuLiquid
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.CompositionLocalProvider
import com.convx.music.ui.component.shapes.ContinuousRoundedRectangle
import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.media.AudioDeviceInfo
import android.media.AudioManager
import android.os.Build
import android.os.Handler
import android.os.Looper
import timber.log.Timber
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.runtime.key
import com.convx.music.core.shapes.RoundedStarShape
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.progressSemantics
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Battery1Bar
import androidx.compose.material.icons.filled.Battery2Bar
import androidx.compose.material.icons.filled.Battery4Bar
import androidx.compose.material.icons.filled.Battery6Bar
import androidx.compose.material.icons.filled.BatteryFull
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Speaker
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material.icons.filled.Usb
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.outlined.Error
import com.convx.music.ui.ios.compat.Button
import androidx.compose.material3.ButtonGroupDefaults
import com.convx.music.ui.ios.compat.CircularProgressIndicator
import androidx.compose.material3.AlertDialogDefaults
import com.convx.music.ui.ios.compat.BasicAlertDialog
import com.convx.music.ui.ios.compat.TextButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import com.convx.music.ui.ios.compat.Icon
import com.convx.music.ui.ios.compat.AppTheme
import com.convx.music.ui.ios.compat.Slider
import androidx.compose.material3.SliderDefaults
import com.convx.music.ui.ios.compat.Surface
import com.convx.music.ui.ios.compat.Text
import com.convx.music.ui.ios.compat.ToggleButton
import com.convx.music.ui.ios.compat.SegmentedButton
import com.convx.music.ui.ios.compat.SingleChoiceSegmentedButtonRow
import com.convx.music.ui.ios.compat.CircularWavyProgressIndicator
import androidx.compose.material3.rememberSliderState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.convx.music.R
import com.convx.music.LocalPlayerConnection
import com.convx.music.constants.AudioQuality
import com.convx.music.constants.AudioQualityKey
import com.convx.music.utils.rememberEnumPreference
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

data class AudioDevice(
    val name: String,
    val type: AudioDeviceType,
    val isConnected: Boolean,
    val isActive: Boolean = false,
    val batteryLevel: Int? = null,
    val deviceId: Int? = null,
)

enum class AudioDeviceType {
    BLUETOOTH,
    WIRED_HEADPHONES,
    PHONE_SPEAKER,
    EXTERNAL_SPEAKER,
    USB_HEADSET,
    HDMI,
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun AudioDeviceMenu(onDismiss: () -> Unit, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    var audioDevices by remember { mutableStateOf<List<AudioDevice>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val audioManager = remember { context.getSystemService(Context.AUDIO_SERVICE) as AudioManager }

    var currentVolume by remember {
        mutableFloatStateOf(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC).toFloat())
    }
    var isUserDragging by remember { mutableStateOf(false) }
    var maxVolume by remember { mutableStateOf(audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)) }

    val playerConnection = LocalPlayerConnection.current
    val service = playerConnection?.service
    var showDevicePopup by remember { mutableStateOf(false) }
    val deviceIcons = rememberAudioDeviceIcons()
    val deviceNames = rememberAudioDeviceNames()
    var iconPickerFor by remember { mutableStateOf<AudioDevice?>(null) }

    val bluetoothLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            loadDevices(context, service?.preferredDeviceId, onSuccess = { devices ->
                audioDevices = devices
                isLoading = false
            }, onError = { error ->
                errorMessage = error
                isLoading = false
            })
        } else {
            errorMessage = context.getString(R.string.bluetooth_permission_required)
            isLoading = false
        }
    }

    fun refreshDevices() {
        loadDevices(context, service?.preferredDeviceId, onSuccess = { devices ->
            audioDevices = devices
        }, onError = {})
    }

    DisposableEffect(Unit) {
        val volumeChangeReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                if (intent.action == "android.media.VOLUME_CHANGED_ACTION") {
                    val streamType = intent.getIntExtra("android.media.EXTRA_VOLUME_STREAM_TYPE", -1)
                    if (streamType == AudioManager.STREAM_MUSIC && !isUserDragging) {
                        currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC).toFloat()
                    }
                }
            }
        }

        val audioDeviceReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                refreshDevices()
            }
        }

        ContextCompat.registerReceiver(
            context,
            volumeChangeReceiver,
            IntentFilter("android.media.VOLUME_CHANGED_ACTION"),
            ContextCompat.RECEIVER_EXPORTED,
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ContextCompat.registerReceiver(
                context,
                audioDeviceReceiver,
                IntentFilter().apply {
                    addAction(AudioManager.ACTION_HEADSET_PLUG)
                    addAction(AudioManager.ACTION_HDMI_AUDIO_PLUG)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        addAction(AudioManager.ACTION_AUDIO_BECOMING_NOISY)
                    }
                },
                ContextCompat.RECEIVER_EXPORTED,
            )
        }

        if (checkBluetoothPermission(context)) {
            loadDevices(context, service?.preferredDeviceId, onSuccess = { devices ->
                audioDevices = devices
                isLoading = false
            }, onError = { error ->
                errorMessage = error
                isLoading = false
            })
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                bluetoothLauncher.launch(Manifest.permission.BLUETOOTH_CONNECT)
            }
        }

        val handler = Handler(Looper.getMainLooper())

        val audioDeviceCallback = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            object : android.media.AudioDeviceCallback() {
                override fun onAudioDevicesAdded(addedDevices: Array<out android.media.AudioDeviceInfo>?) {
                    refreshDevices()
                }
                override fun onAudioDevicesRemoved(removedDevices: Array<out android.media.AudioDeviceInfo>?) {
                    refreshDevices()
                }
            }
        } else null

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && audioDeviceCallback != null) {
            audioManager.registerAudioDeviceCallback(audioDeviceCallback, handler)
        }

        val bluetoothReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                refreshDevices()
                handler.postDelayed({ refreshDevices() }, 1000)
                handler.postDelayed({ refreshDevices() }, 2500)
            }
        }

        context.registerReceiver(
            bluetoothReceiver,
            IntentFilter().apply {
                addAction(BluetoothDevice.ACTION_ACL_CONNECTED)
                addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED)
            }
        )

        val batteryPollingRunnable = object : Runnable {
            override fun run() {
                refreshDevices()
                handler.postDelayed(this, 30000)
            }
        }
        handler.postDelayed(batteryPollingRunnable, 30000)

        onDispose {
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && audioDeviceCallback != null) {
                    audioManager.unregisterAudioDeviceCallback(audioDeviceCallback)
                }
                context.unregisterReceiver(volumeChangeReceiver)
                context.unregisterReceiver(audioDeviceReceiver)
                context.unregisterReceiver(bluetoothReceiver)
                handler.removeCallbacksAndMessages(null)
            } catch (_: IllegalArgumentException) {
                // A callback or receiver may already be unregistered during disposal.
            }
        }
    }

    V2Floating(
        onDismiss = { if (iconPickerFor != null) iconPickerFor = null else onDismiss() },
        placement = Centered,
    ) {
        CompositionLocalProvider(LocalMenuLiquid provides true) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 520.dp)
                .heightIn(max = 680.dp)
                .iosMenuSurface(ContinuousRoundedRectangle(30.dp))
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 20.dp)
                .then(modifier)
                .animateContentSize()
        ) {
            if (iconPickerFor != null) {
                val device = iconPickerFor!!
                AudioDeviceIconPicker(
                    deviceName = device.name,
                    displayName = deviceNames.displayName(device.name),
                    current = deviceIcons.pickFor(device.name),
                    onSave = { name, icon ->
                        deviceNames.set(device.name, name)
                        deviceIcons.set(device.name, icon)
                        iconPickerFor = null
                    },
                    onDismiss = { iconPickerFor = null },
                )
            } else {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = stringResource(R.string.audio_devices),
                    style = AppTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f),
                )
                com.convx.music.ui.ios.compat.TextButton(onClick = onDismiss) { Text(stringResource(R.string.done)) }
            }
            Spacer(Modifier.height(12.dp))
            when {
                isLoading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(48.dp),
                            strokeWidth = 3.dp,
                            color = AppTheme.colorScheme.primary
                        )
                    }
                }

                errorMessage != null -> {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        SymbolIcon(symbol = sf("exclamationmark.circle.fill"), contentDescription = null, size = 48.dp, tint = AppTheme.colorScheme.error)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = errorMessage.orEmpty(),
                            style = AppTheme.typography.bodyMedium,
                            color = AppTheme.colorScheme.onSurface,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = {
                                errorMessage = null
                                isLoading = true
                                refreshDevices()
                            }
                        ) {
                            Text(text = stringResource(R.string.retry))
                        }
                    }
                }

                else -> {
                    val activeDevice = audioDevices.firstOrNull { it.isActive }
                    val hasBluetooth = audioDevices.any { it.type == AudioDeviceType.BLUETOOTH }

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 24.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        activeDevice?.let { device ->
                            Surface(
                                shape = AppTheme.shapes.large,
                                color = androidx.compose.ui.graphics.Color.Transparent,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .animateContentSize(
                                            animationSpec = tween(durationMillis = 400, easing = FastOutSlowInEasing)
                                        ),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    AudioDeviceRow(
                                        device = device,
                                    displayName = deviceNames.displayName(device.name),
                                        currentVolume = currentVolume,
                                        maxVolume = maxVolume,
                                        icons = deviceIcons,
                                        onEditIcon = { iconPickerFor = device },
                                        modifier = Modifier.weight(1f)
                                    )
                                    
                                    androidx.compose.animation.AnimatedVisibility(
                                        visible = hasBluetooth,
                                        enter = androidx.compose.animation.fadeIn() + androidx.compose.animation.expandHorizontally(),
                                        exit = androidx.compose.animation.fadeOut() + androidx.compose.animation.shrinkHorizontally()
                                    ) {
                                        val chevronRotation by animateFloatAsState(
                                            targetValue = if (showDevicePopup) 180f else 0f,
                                            animationSpec = tween(durationMillis = 300),
                                            label = "chevron"
                                        )
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.padding(start = 12.dp)
                                        ) {
                                            val isActive = device.isActive
                                            Surface(
                                                onClick = { showDevicePopup = !showDevicePopup },
                                                shape = CircleShape,
                                                color = if (isActive) AppTheme.colorScheme.primaryContainer else AppTheme.colorScheme.surfaceVariant,
                                                tonalElevation = 2.dp,
                                                modifier = Modifier.size(44.dp)
                                            ) {
                                                Box(contentAlignment = Alignment.Center) {
                                                    SymbolIcon(symbol = sf("chevron.down"), contentDescription = null, size = 20.dp, modifier = Modifier
                                                            
                                                            .graphicsLayer { rotationZ = chevronRotation }, tint = if (isActive) AppTheme.colorScheme.onPrimaryContainer else AppTheme.colorScheme.onSurfaceVariant)
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        androidx.compose.animation.AnimatedVisibility(
                            visible = hasBluetooth && showDevicePopup,
                            enter = androidx.compose.animation.expandVertically(
                                animationSpec = tween(300, easing = FastOutSlowInEasing)
                            ) + androidx.compose.animation.fadeIn(tween(200)),
                            exit = androidx.compose.animation.shrinkVertically(
                                animationSpec = tween(250, easing = FastOutSlowInEasing)
                            ) + androidx.compose.animation.fadeOut(tween(150))
                        ) {
                            Surface(
                                shape = AppTheme.shapes.extraLarge,
                                color = AppTheme.colorScheme.surfaceContainerLow,
                                modifier = Modifier.fillMaxWidth()
                            ) {                                 Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp)
                                ) {
                                    Text(
                                        text = stringResource(R.string.audio_devices),
                                        style = AppTheme.typography.labelMedium,
                                        color = AppTheme.colorScheme.primary,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 8.dp)
                                    )

                                    audioDevices.forEachIndexed { index, dev ->
                                        key(dev.deviceId) {
                                            val isSelected = dev.isActive
                                            val deviceIcon = deviceIcons.symbol(dev.name, dev.type)

                                            val itemShape = remember(index, audioDevices.size) {
                                                when {
                                                    audioDevices.size == 1 -> RoundedCornerShape(24.dp)
                                                    index == 0 -> RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp, bottomStart = 4.dp, bottomEnd = 4.dp)
                                                    index == audioDevices.lastIndex -> RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp, bottomStart = 24.dp, bottomEnd = 24.dp)
                                                    else -> RoundedCornerShape(4.dp)
                                                }
                                            }

                                            Surface(
                                                onClick = {
                                                    service?.setPreferredAudioDevice(dev.deviceId)
                                                    refreshDevices()
                                                    showDevicePopup = false
                                                },
                                                shape = itemShape,
                                                color = if (isSelected)
                                                    AppTheme.colorScheme.secondaryContainer
                                                else
                                                    AppTheme.colorScheme.surfaceContainerHigh,
                                                modifier = Modifier.fillMaxWidth()
                                                    .padding(vertical = 1.dp)
                                                    .pointerInput(dev.name) { detectTapGestures(onLongPress = { iconPickerFor = dev }) }
                                            ) {
                                                Row(
                                                    modifier = Modifier
                                                        .padding(horizontal = 16.dp, vertical = 14.dp)
                                                        .fillMaxWidth(),
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                                                ) {
                                                    SymbolIcon(
                                                        symbol = sf(deviceIcon),
 bounceOnEnter = true,
 bounceOnChange = true,
                                                        contentDescription = null,
 size = 20.dp,
                                                        tint = if (isSelected)
                                                            AppTheme.colorScheme.onSecondaryContainer
                                                        else
                                                            AppTheme.colorScheme.onSurfaceVariant
                                                    )
                                                    Text(
                                                        text = if (dev.type == AudioDeviceType.PHONE_SPEAKER)
                                                            stringResource(R.string.this_phone) else deviceNames.displayName(dev.name),
                                                        style = AppTheme.typography.bodyLarge,
                                                        color = if (isSelected)
                                                            AppTheme.colorScheme.onSecondaryContainer
                                                        else
                                                            AppTheme.colorScheme.onSurface,
                                                        maxLines = 1,
                                                        overflow = TextOverflow.Ellipsis,
                                                        modifier = Modifier.weight(1f)
                                                    )
                                                    if (isSelected) {
                                                        SymbolIcon(symbol = sf("speaker.wave.3.fill"), contentDescription = null, size = 16.dp, tint = AppTheme.colorScheme.secondary)
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }


                    AudioQualitySelector(context)

                    Spacer(modifier = Modifier.height(24.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (activeDevice?.type == AudioDeviceType.BLUETOOTH && activeDevice.batteryLevel != null) {
                            val density = LocalDensity.current
                            val strokeWidthPx = with(density) { 4.dp.toPx() }
                            val wavyStroke = remember(strokeWidthPx) {
                                Stroke(width = strokeWidthPx, cap = StrokeCap.Round)
                            }

                            Box(
                                modifier = Modifier
                                    .padding(start = 8.dp)
                                    .size(56.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularWavyProgressIndicator(
                                    progress = { activeDevice.batteryLevel.toFloat() / 100f },
                                    modifier = Modifier.fillMaxSize(),
                                    color = AppTheme.colorScheme.primary,
                                    trackColor = AppTheme.colorScheme.primaryContainer,
                                    stroke = wavyStroke,
                                    trackStroke = wavyStroke,
                                    gapSize = 3.dp
                                )
                                Text(
                                    text = "${activeDevice.batteryLevel}%",
                                    style = AppTheme.typography.labelMedium,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = AppTheme.colorScheme.onSurface,
                                    textAlign = TextAlign.Center
                                )
                            }
                        } else {
                            Spacer(modifier = Modifier.width(1.dp))
                        }

                        Button(
                            onClick = onDismiss,
                            shape = RoundedCornerShape(24.dp)
                        ) {
                            Text(stringResource(R.string.done))
                        }
                    }
                }
            }
            }
        }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun VolumeControlRow(
    label: String,
    icon: ImageVector,
    volume: Float,
    maxVolume: Int,
    onVolumeChange: (Float) -> Unit,
    onDragStart: () -> Unit = {},
    onDragEnd: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val coroutineScope = rememberCoroutineScope()
    val sliderState = rememberSliderState(
        valueRange = 0f..maxVolume.toFloat(),
    )

    val snapAnimationSpec = AppTheme.motionScheme.fastEffectsSpec<Float>()
    var currentValue by rememberSaveable { mutableFloatStateOf(volume) }
    var animateJob: Job? by remember { mutableStateOf(null) }

    LaunchedEffect(volume) {
        if (!sliderState.isDragging) {
            currentValue = volume
            sliderState.value = volume
        }
    }

    sliderState.onValueChange = { newValue ->
        currentValue = newValue
        if (sliderState.isDragging) {
            onDragStart()
            animateJob?.cancel()
            sliderState.value = newValue
            onVolumeChange(newValue)
        }
    }

    sliderState.onValueChangeFinished = {
        animateJob = coroutineScope.launch {
            animate(
                initialValue = sliderState.value,
                targetValue = currentValue,
                animationSpec = snapAnimationSpec
            ) { value, _ ->
                sliderState.value = value
            }
        }
        onDragEnd()
    }

    val interactionSource = remember { MutableInteractionSource() }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(72.dp),
        shape = CircleShape,
        color = AppTheme.colorScheme.surfaceContainerHigh,
        tonalElevation = 1.dp
    ) {
        Box(contentAlignment = Alignment.CenterStart) {
            // Animate the fill as the volume changes.
            val animatedVolumeFraction by animateFloatAsState(
                targetValue = currentValue / maxVolume.toFloat(),
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioNoBouncy,
                    stiffness = Spring.StiffnessMediumLow
                ),
                label = "VolumeFillAnimation"
            )

            val widthState = remember { mutableFloatStateOf(0f) }
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .onSizeChanged { widthState.floatValue = it.width.toFloat() }
                    .pointerInput(maxVolume) {
                        detectTapGestures { offset ->
                            val percent = (offset.x / widthState.floatValue).coerceIn(0f, 1f)
                            val newValue = percent * maxVolume
                            currentValue = newValue
                            onVolumeChange(newValue)
                        }
                    }
                    .pointerInput(maxVolume) {
                        detectDragGestures(
                            onDragStart = { onDragStart() },
                            onDragEnd = { onDragEnd() },
                            onDragCancel = { onDragEnd() }
                        ) { change, _ ->
                            change.consume()
                            val percent = (change.position.x / widthState.floatValue).coerceIn(0f, 1f)
                            val newValue = percent * maxVolume
                            currentValue = newValue
                            onVolumeChange(newValue)
                        }
                    }
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(animatedVolumeFraction)
                        .background(AppTheme.colorScheme.primaryContainer)
                )
            }

            // The icon, label, and max-volume dot sit over the animated fill.
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically, 
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.padding(start = 24.dp)
                ) {
                    SymbolIcon(symbol = if (currentValue > 0) sf("speaker.wave.3.fill") else sf("speaker.slash.fill"), contentDescription = null, size = 24.dp, variableValue = volumeBars(currentValue / maxVolume), tint = if (currentValue / maxVolume > 0.2f) AppTheme.colorScheme.onPrimaryContainer else AppTheme.colorScheme.onSurface)
                    Text(
                        text = label,
                        style = AppTheme.typography.titleMedium,
                        color = if (currentValue / maxVolume > 0.4f) AppTheme.colorScheme.onPrimaryContainer else AppTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                
                // Changes color near maximum volume.
                Box(
                    modifier = Modifier
                        .padding(end = 16.dp)
                        .size(6.dp)
                        .background(
                            color = if (currentValue / maxVolume > 0.95f) AppTheme.colorScheme.onPrimaryContainer else AppTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                            shape = CircleShape
                        )
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun AudioQualitySelector(context: Context) {
    val (audioQuality, onAudioQualityChange) = rememberEnumPreference(
        key = AudioQualityKey,
        defaultValue = AudioQuality.AUTO
    )

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = stringResource(R.string.audio_quality_title),
            style = AppTheme.typography.titleMedium,
            color = AppTheme.colorScheme.onSurface,
            modifier = Modifier
                .padding(bottom = 12.dp)
                .fillMaxWidth()
        )

        val options = listOf(
            stringResource(R.string.audio_quality_auto),
            stringResource(R.string.audio_quality_high),
            stringResource(R.string.audio_quality_low)
        )
        val selectedIndex = when (audioQuality) {
            AudioQuality.AUTO -> 0
            AudioQuality.HIGH -> 1
            AudioQuality.LOW -> 2
        }

        SingleChoiceSegmentedButtonRow(
            modifier = Modifier
                .padding(vertical = 8.dp)
                .fillMaxWidth(),
        ) {
            options.forEachIndexed { index, label ->
                SegmentedButton(
                    selected = selectedIndex == index,
                    onClick = {
                        val newQuality = when (index) {
                            0 -> AudioQuality.AUTO
                            1 -> AudioQuality.HIGH
                            else -> AudioQuality.LOW
                        }
                        onAudioQualityChange(newQuality)
                        applyAudioQuality(context, newQuality)
                    },
                    label = {
                        Text(
                            text = label,
                            style = AppTheme.typography.bodyMedium
                        )
                    },
                )
            }
        }
    }
}

fun applyAudioQuality(context: Context, quality: AudioQuality) {
}

internal fun loadDevices(
    context: Context,
    preferredDeviceId: Int?,
    onSuccess: (List<AudioDevice>) -> Unit,
    onError: (String) -> Unit
) {
    try {
        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val audioDevices = audioManager.getDevices(AudioManager.GET_DEVICES_OUTPUTS)
            val devices = audioDevices.mapNotNull { deviceInfo ->
                mapAudioDevice(context, deviceInfo)
            }

            val activeDevice = determineActiveDevice(audioDevices, preferredDeviceId)
            val updatedDevices = devices.map { device ->
                device.copy(isActive = device.deviceId == activeDevice?.id)
            }

            // Maintain a stable order: Phone Speaker -> Wired -> Bluetooth -> Others
            val sortedDevices = updatedDevices.sortedWith(compareBy<AudioDevice> {
                when (it.type) {
                    AudioDeviceType.PHONE_SPEAKER -> 0
                    AudioDeviceType.WIRED_HEADPHONES -> 1
                    AudioDeviceType.USB_HEADSET -> 2
                    AudioDeviceType.BLUETOOTH -> 3
                    else -> 4
                }
            }.thenBy { it.name })

            onSuccess(sortedDevices.distinctBy { it.name })
        } else {
            loadDevicesLegacy(context, onSuccess, onError)
        }
    } catch (e: Exception) {
        onError("Failed to load devices: ${e.message}")
    }
}

private fun mapAudioDevice(context: Context, deviceInfo: AudioDeviceInfo): AudioDevice? {
    val deviceId = deviceInfo.id
    return when (deviceInfo.type) {
        AudioDeviceInfo.TYPE_BLUETOOTH_A2DP -> AudioDevice(
            name = bluetoothDisplayName(context, deviceInfo) ?: "Bluetooth Device",
            type = AudioDeviceType.BLUETOOTH,
            isConnected = true,
            batteryLevel = bluetoothBatteryLevel(context, deviceInfo.productName.toString()),
            deviceId = deviceId,
        )
        AudioDeviceInfo.TYPE_WIRED_HEADPHONES, AudioDeviceInfo.TYPE_WIRED_HEADSET -> AudioDevice(
            name = context.getString(R.string.wired_headphones),
            type = AudioDeviceType.WIRED_HEADPHONES,
            isConnected = true,
            deviceId = deviceId,
        )
        AudioDeviceInfo.TYPE_USB_HEADSET, AudioDeviceInfo.TYPE_USB_DEVICE -> AudioDevice(
            name = deviceInfo.productName?.toString() ?: "USB Audio",
            type = AudioDeviceType.USB_HEADSET,
            isConnected = true,
            deviceId = deviceId,
        )
        AudioDeviceInfo.TYPE_HDMI -> AudioDevice(
            name = context.getString(R.string.hdmi),
            type = AudioDeviceType.HDMI,
            isConnected = true,
            deviceId = deviceId,
        )
        AudioDeviceInfo.TYPE_BUILTIN_SPEAKER -> AudioDevice(
            name = context.getString(R.string.phone_speaker),
            type = AudioDeviceType.PHONE_SPEAKER,
            isConnected = true,
            deviceId = deviceId,
        )
        else -> null
    }
}

private fun bluetoothBatteryLevel(context: Context, deviceName: String): Int? {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) return null

    return try {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) !=
            PackageManager.PERMISSION_GRANTED
        ) return null

        val bluetoothManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        val device = bluetoothManager.adapter?.bondedDevices?.find { it.name == deviceName } ?: return null

        @SuppressLint("MissingPermission")
        val batteryLevel = device.readBatteryLevel() ?: return null
        batteryLevel.takeIf { it in 0..100 }
    } catch (_: Exception) {
        null
    }
}

@SuppressLint("MissingPermission")
private fun BluetoothDevice.readBatteryLevel(): Int? = try {
    val method = android.bluetooth.BluetoothDevice::class.java.getMethod("getBatteryLevel")
    method.invoke(this) as? Int
} catch (_: Exception) {
    null
}

private fun determineActiveDevice(
    audioDevices: Array<AudioDeviceInfo>,
    preferredDeviceId: Int?
): AudioDeviceInfo? = preferredDeviceId?.let { id ->
    audioDevices.firstOrNull { it.id == id }
} ?: audioDevices.firstOrNull { it.type == AudioDeviceInfo.TYPE_BLUETOOTH_A2DP }
    ?: audioDevices.firstOrNull {
        it.type == AudioDeviceInfo.TYPE_WIRED_HEADPHONES ||
            it.type == AudioDeviceInfo.TYPE_WIRED_HEADSET
    }
    ?: audioDevices.firstOrNull { it.type == AudioDeviceInfo.TYPE_BUILTIN_SPEAKER }

@Suppress("DEPRECATION")
private fun loadDevicesLegacy(context: Context, onSuccess: (List<AudioDevice>) -> Unit, onError: (String) -> Unit) {
    val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    val devices = mutableListOf<AudioDevice>()

    if (audioManager.isBluetoothA2dpOn) {
        devices.add(AudioDevice("Bluetooth Device", AudioDeviceType.BLUETOOTH, true, true))
    }
    if (audioManager.isWiredHeadsetOn) {
        devices.add(AudioDevice("Wired Headphones", AudioDeviceType.WIRED_HEADPHONES, true, true))
    }
    if (audioManager.isSpeakerphoneOn) {
        devices.add(AudioDevice("External Speaker", AudioDeviceType.EXTERNAL_SPEAKER, true, true))
    }
    if (devices.isEmpty() || !devices.any { it.isActive }) {
        devices.add(AudioDevice("Phone Speaker", AudioDeviceType.PHONE_SPEAKER, true, true))
    }
    onSuccess(devices.filter { it.isActive }.take(1))
}

private fun checkBluetoothPermission(context: Context): Boolean = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
    ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.BLUETOOTH_CONNECT
    ) == PackageManager.PERMISSION_GRANTED
} else true

/** Floating liquid-glass editor for one output's saved display name and icon. */
@Composable
private fun AudioDeviceIconPicker(
    deviceName: String,
    displayName: String,
    current: String?,
    onSave: (String, String?) -> Unit,
    onDismiss: () -> Unit,
) {
    var editedName by remember(deviceName, displayName) { mutableStateOf(displayName) }
    var selectedIcon by remember(deviceName, current) { mutableStateOf(current) }
    var query by remember { mutableStateOf("") }
    val choices = remember(query) {
        AudioDeviceSymbolChoices.filter { query.isBlank() || it.contains(query.trim(), ignoreCase = true) }
    }
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = stringResource(R.string.audio_devices),
                style = AppTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f),
            )
            com.convx.music.ui.ios.compat.TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.done))
            }
        }
        Text(
            text = deviceName,
            style = AppTheme.typography.labelMedium,
            color = AppTheme.colorScheme.onSurfaceVariant,
        )
        com.convx.music.ui.ios.compat.OutlinedTextField(
            value = editedName,
            onValueChange = { editedName = it },
            singleLine = true,
            label = { Text("Device name") },
            modifier = Modifier.fillMaxWidth(),
        )
        com.convx.music.ui.ios.compat.TextButton(
            onClick = { onSave(editedName, selectedIcon) },
            modifier = Modifier.align(Alignment.End),
        ) {
            Text("Save")
        }
        com.convx.music.ui.ios.compat.OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            singleLine = true,
            placeholder = { Text(stringResource(R.string.search)) },
            modifier = Modifier.fillMaxWidth(),
        )
        androidx.compose.foundation.lazy.grid.LazyVerticalGrid(
            columns = androidx.compose.foundation.lazy.grid.GridCells.Adaptive(64.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxWidth().heightIn(max = 320.dp),
        ) {
            item(key = "auto") {
                PickerTile(selected = selectedIcon == null, onClick = {
                    selectedIcon = null
                }) {
                    Text("Auto", style = AppTheme.typography.labelLarge)
                }
            }
            choices.forEach { name ->
                item(key = name) {
                    PickerTile(selected = selectedIcon == name, onClick = {
                        selectedIcon = name
                    }) {
                        SymbolIcon(symbol = sf(name), contentDescription = name, size = 26.dp)
                    }
                }
            }
        }
    }
}

@Composable
private fun PickerTile(selected: Boolean, onClick: () -> Unit, content: @Composable () -> Unit) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(64.dp)
            .clip(CircleShape)
            .border(
                width = 1.dp,
                color = AppTheme.colorScheme.onSurface.copy(alpha = if (selected) 0.24f else 0.10f),
                shape = CircleShape,
            )
            .background(
                if (selected) AppTheme.colorScheme.primary.copy(alpha = 0.26f)
                else AppTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.32f),
            )
            .clickable(onClick = onClick),
    ) { content() }
}

@Composable
private fun AudioDeviceRow(
    device: AudioDevice,
    displayName: String,
    currentVolume: Float,
    maxVolume: Int,
    icons: AudioDeviceIcons,
    onEditIcon: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isActiveDevice = device.isActive
    
    val containerColor = if (isActiveDevice) AppTheme.colorScheme.primaryContainer else AppTheme.colorScheme.surfaceVariant
    val onContainer = if (isActiveDevice) AppTheme.colorScheme.onPrimaryContainer else AppTheme.colorScheme.onSurface

    val scallopShape = RoundedStarShape(sides = 8, curve = 0.10, rotation = 0f)

    val backgroundScale by animateFloatAsState(
        targetValue = if (isActiveDevice) 1.10f else 1f,
        animationSpec = tween(durationMillis = 400, easing = FastOutSlowInEasing),
        label = "activeDeviceScale"
    )

    val deviceIcon = icons.symbol(device.name, device.type)

    Surface(
        modifier = modifier
            .clip(CircleShape)
            .pointerInput(device.name) { detectTapGestures(onLongPress = { onEditIcon() }) },
        color = containerColor,
        tonalElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .padding(start = 4.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .graphicsLayer(
                            scaleX = backgroundScale,
                            scaleY = backgroundScale
                        )
                        .background(
                            color = onContainer.copy(alpha = 0.12f),
                            shape = if (isActiveDevice) scallopShape else CircleShape
                        )
                )

                SymbolIcon(
                    symbol = sf(deviceIcon),
 bounceOnEnter = true,
 bounceOnChange = true,
                    contentDescription = null,
                    tint = onContainer,
 size = 24.dp
                )
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = displayName,
                    style = AppTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = onContainer,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                val statusText = if (isActiveDevice) "Connected" else "Available"
                
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(onContainer.copy(alpha = 0.08f))
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = statusText,
                        maxLines = 1,
                        style = AppTheme.typography.labelMedium,
                        overflow = TextOverflow.Ellipsis,
                        color = onContainer
                    )
                }
            }

            SymbolIcon(
                symbol = sf("pencil"),
                contentDescription = stringResource(R.string.edit),
                size = 18.dp,
                tint = onContainer.copy(alpha = 0.6f),
                modifier = Modifier
                    .clip(CircleShape)
                    .clickable(onClick = onEditIcon)
                    .padding(6.dp),
            )

            if (isActiveDevice) {
                val value = ((currentVolume / maxVolume) * 100).toInt()
                
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.padding(end = 4.dp)
                ) {
                    SymbolIcon(symbol = sf("speaker.wave.3.fill"), contentDescription = "Volume level", size = 14.dp, tint = onContainer)
                    Text(
                        text = "$value%",
                        style = AppTheme.typography.labelSmall,
                        color = onContainer
                    )
                }
            }
        }
    }
}

@Composable
fun DeviceSelector(
    devices: List<AudioDevice>,
    onDeviceSelect: (AudioDevice) -> Unit,
    modifier: Modifier = Modifier
) {
    androidx.compose.foundation.layout.FlowRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(ButtonGroupDefaults.ConnectedSpaceBetween),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        devices.forEach { device ->
            val isSelected = device.isActive
            val deviceIcon = audioDeviceSymbol(device.name, device.type)

            ToggleButton(
                checked = isSelected,
                onCheckedChange = { if (!isSelected) onDeviceSelect(device) },
                modifier = Modifier
                    .weight(1f)
                    .semantics { role = Role.RadioButton }
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    SymbolIcon(
                        symbol = sf(deviceIcon),
 bounceOnEnter = true,
 bounceOnChange = true,
                        contentDescription = null,
 size = 18.dp
                    )
                    Text(
                        text = if (device.type == AudioDeviceType.PHONE_SPEAKER) stringResource(R.string.this_phone) else device.name,
                        style = AppTheme.typography.labelLarge,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}
