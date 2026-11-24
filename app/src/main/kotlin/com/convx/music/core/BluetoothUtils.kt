package com.convx.music.core

import android.content.Context
import android.media.AudioDeviceInfo
import android.media.AudioManager
import android.os.Build
import android.Manifest
import android.bluetooth.BluetoothManager
import android.content.pm.PackageManager
import android.content.BroadcastReceiver
import android.content.Intent
import android.content.IntentFilter
import android.os.Handler
import android.os.Looper
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.produceState
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat

/** Headphone state that follows device plug/unplug, queried only when the route changes. */
@Composable
fun rememberHeadphoneConnected(): State<Boolean> {
    val context = LocalContext.current
    return produceState(initialValue = isBluetoothHeadphoneConnected(context), context) {
        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                value = isBluetoothHeadphoneConnected(context)
            }
        }

        val callback = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            object : android.media.AudioDeviceCallback() {
                override fun onAudioDevicesAdded(addedDevices: Array<out AudioDeviceInfo>?) {
                    value = isBluetoothHeadphoneConnected(context)
                }
                override fun onAudioDevicesRemoved(removedDevices: Array<out AudioDeviceInfo>?) {
                    value = isBluetoothHeadphoneConnected(context)
                }
            }
        } else null

        val filter = IntentFilter().apply {
            addAction(AudioManager.ACTION_HEADSET_PLUG)
            addAction("android.bluetooth.adapter.action.STATE_CHANGED")
            addAction("android.bluetooth.device.action.ACL_CONNECTED")
            addAction("android.bluetooth.device.action.ACL_DISCONNECTED")
            addAction("android.media.AUDIO_BECOMING_NOISY")
        }

        ContextCompat.registerReceiver(context, receiver, filter, ContextCompat.RECEIVER_EXPORTED)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && callback != null) {
            audioManager.registerAudioDeviceCallback(callback, Handler(Looper.getMainLooper()))
        }

        awaitDispose {
            context.unregisterReceiver(receiver)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && callback != null) {
                audioManager.unregisterAudioDeviceCallback(callback)
            }
        }
    }
}

fun isBluetoothHeadphoneConnected(context: Context): Boolean {
    val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        val audioDevices = audioManager.getDevices(AudioManager.GET_DEVICES_OUTPUTS)
        audioDevices.any { device ->
            device.type == AudioDeviceInfo.TYPE_BLUETOOTH_A2DP ||
                    device.type == AudioDeviceInfo.TYPE_BLUETOOTH_SCO ||
                    // Wired and USB headsets also swap the mini player's speaker glyph for headphones.
                    device.type == AudioDeviceInfo.TYPE_WIRED_HEADPHONES ||
                    device.type == AudioDeviceInfo.TYPE_WIRED_HEADSET ||
                    device.type == AudioDeviceInfo.TYPE_USB_HEADSET
        }
    } else {
        // For older Android versions, use deprecated method
        @Suppress("DEPRECATION")
        audioManager.isBluetoothA2dpOn || audioManager.isBluetoothScoOn
    }
}
/**
 * Returns the active Bluetooth output's name, or null when Bluetooth isn't active.
 */
fun getConnectedBluetoothDeviceName(context: Context): String? {
    val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

    val isBluetoothActive = audioManager.isBluetoothA2dpOn || audioManager.isBluetoothScoOn
    if (!isBluetoothActive) return null

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        val audioDevices = audioManager.getDevices(AudioManager.GET_DEVICES_OUTPUTS)
        
        val activeBluetoothDevice = audioDevices.find { it.type == AudioDeviceInfo.TYPE_BLUETOOTH_A2DP }
            ?: audioDevices.find { it.type == AudioDeviceInfo.TYPE_BLUETOOTH_SCO }
            
        return activeBluetoothDevice?.let { bluetoothDisplayName(context, it) }
    } else {
        return null
    }
}

/** An external audio output with the name and type the players label it with. */
data class ActiveAudioOutput(val name: String, val type: AudioDeviceType)

/** The external output audio is routed to (Bluetooth, USB or wired), or null on the phone speaker. */
fun getActiveAudioOutput(context: Context): ActiveAudioOutput? {
    getConnectedBluetoothDeviceName(context)?.let { return ActiveAudioOutput(it, AudioDeviceType.BLUETOOTH) }
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) return null
    val devices = (context.getSystemService(Context.AUDIO_SERVICE) as AudioManager)
        .getDevices(AudioManager.GET_DEVICES_OUTPUTS)
    devices.firstOrNull { it.type == AudioDeviceInfo.TYPE_USB_HEADSET }?.let {
        return ActiveAudioOutput(it.productName?.toString()?.takeIf(String::isNotBlank) ?: "USB Audio", AudioDeviceType.USB_HEADSET)
    }
    devices.firstOrNull {
        it.type == AudioDeviceInfo.TYPE_WIRED_HEADPHONES || it.type == AudioDeviceInfo.TYPE_WIRED_HEADSET
    }?.let {
        return ActiveAudioOutput(context.getString(com.convx.music.R.string.wired_headphones), AudioDeviceType.WIRED_HEADPHONES)
    }
    return null
}

fun isBuds(name: String?): Boolean {
    if (name == null) return false
    val lowerName = name.lowercase()
    return lowerName.contains("buds") || 
           lowerName.contains("airpods") || 
           lowerName.contains("earpods") || 
           lowerName.contains("earphone") ||
           lowerName.contains("freebuds") ||
           lowerName.contains("pods")
}

fun isSpeaker(name: String?): Boolean {
    if (name == null) return false
    val lowerName = name.lowercase()
    return lowerName.contains("speaker") || 
           lowerName.contains("soundbar") || 
           lowerName.contains("homepod") || 
           lowerName.contains("echo") ||
           lowerName.contains("boombox") ||
           lowerName.contains("audio system") ||
           lowerName.contains("sound") ||
           lowerName.contains("audio") ||
           lowerName.contains("stereo") ||
           lowerName.contains("music") ||
           lowerName.contains("box") ||
           lowerName.contains("party") ||
           lowerName.contains("waves")
}

/** The name the user sees in system settings: a renamed (alias) bonded device wins over the product name. */
fun bluetoothDisplayName(context: Context, info: AudioDeviceInfo): String? {
    val product = info.productName?.toString()
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) return product
    if (ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) return product
    val bonded = (context.getSystemService(Context.BLUETOOTH_SERVICE) as? BluetoothManager)?.adapter?.bondedDevices
    val device = bonded?.firstOrNull { it.address == info.address } ?: bonded?.firstOrNull { it.name == product }
    return device?.alias ?: product
}
