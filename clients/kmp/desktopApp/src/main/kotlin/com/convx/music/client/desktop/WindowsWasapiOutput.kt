package com.convx.music.client.desktop

import com.convx.music.playback.*
import com.sun.jna.*
import com.sun.jna.Function
import com.sun.jna.ptr.IntByReference
import com.sun.jna.ptr.LongByReference
import com.sun.jna.ptr.PointerByReference
import com.sun.jna.win32.StdCallLibrary
import java.io.IOException
import java.util.UUID

/** All interfaces belong to the calling audio thread's COM apartment. */
internal class WindowsWasapiOutput private constructor(
    override val format: PcmFormat,
    exclusive: Boolean,
    deviceId: String?,
) : DesktopPcmOutput {
    private val apartment = ComApartment()
    private var enumerator: ComObject? = null
    private var device: ComObject? = null
    private var client: ComObject? = null
    private var render: ComObject? = null
    private var bufferFrames = 0
    private var writtenFrames = 0L
    private var closed = false
    override lateinit var description: PlaybackAudioOutput
        private set

    init {
        try {
            enumerator = apartment.enumerator()
            device = selectedDevice(enumerator!!, deviceId)
            client = activate(device!!)
            val mix = PointerByReference()
            client!!.check(8, "Read the device format", mix)
            val mixFormat = try { readWaveFormat(mix.value) } finally { ole.CoTaskMemFree(mix.value) }
            val wave = waveFormat(format, extensible = true)
            var selectedWave = wave
            if (exclusive) {
                var supported = client!!.call(7, 1, wave, null)
                if (supported != 0 && format.channels <= 2) {
                    selectedWave = waveFormat(format, extensible = false)
                    supported = client!!.call(7, 1, selectedWave, null)
                }
                checked(supported, "This device doesn't support exclusive ${format.sampleRate} Hz / ${format.bitsPerSample}-bit audio")
                if (supported != 0) throw IOException("The device cannot provide this exact format in exclusive mode.")
            }
            val flags = if (exclusive) 0 else 0x88000000.toInt()
            var initialized = client!!.call(3, if (exclusive) 1 else 0, flags, 1_000_000L, 0L, selectedWave, null)
            if (initialized == 0x88890019.toInt()) {
                val alignedFrames = IntByReference()
                client!!.check(4, "Read the aligned device buffer", alignedFrames)
                client!!.close()
                client = activate(device!!)
                val duration = (10_000_000L * alignedFrames.value + format.sampleRate / 2) / format.sampleRate
                initialized = client!!.call(3, if (exclusive) 1 else 0, flags, duration, 0L, selectedWave, null)
            }
            checked(initialized, "Open the audio device")
            val size = IntByReference()
            client!!.check(4, "Read the audio buffer size", size)
            bufferFrames = size.value
            check(bufferFrames > 0) { "The audio device returned an empty buffer." }
            val service = PointerByReference()
            client!!.check(14, "Open the audio render service", guid(RENDER_IID), service)
            render = ComObject(service.value)
            description = PlaybackAudioOutput(format, if (exclusive) format else mixFormat,
                if (exclusive) PlaybackAudioOutputMode.EXCLUSIVE else PlaybackAudioOutputMode.SYSTEM_MIXER,
                deviceName(device!!), processingApplied = false)
        } catch (failure: Throwable) { close(); throw failure }
    }

    override val queuedFrames: Int get() {
        val padding = IntByReference()
        client!!.check(6, "Read audio playback progress", padding)
        return padding.value.coerceAtLeast(0)
    }
    override val playedFrames: Long get() = (writtenFrames - queuedFrames).coerceAtLeast(0)
    override fun write(bytes: ByteArray, offset: Int, length: Int): Int {
        val frames = minOf(length / format.frameSize, (bufferFrames - queuedFrames).coerceAtLeast(0))
        if (frames == 0) return 0
        val data = PointerByReference()
        render!!.check(3, "Acquire an audio buffer", frames, data)
        try { data.value.write(0, bytes, offset, frames * format.frameSize) }
        catch (failure: Throwable) { render!!.call(4, frames, 2); throw failure }
        render!!.check(4, "Submit the audio buffer", frames, 0)
        writtenFrames += frames
        return frames * format.frameSize
    }
    override fun start() { client!!.check(10, "Start audio playback") }
    override fun pause() { client!!.check(11, "Pause audio playback") }
    override fun flush() { pause(); client!!.check(12, "Reset audio playback"); writtenFrames = 0 }
    override fun close() {
        if (closed) return
        closed = true
        runCatching { client?.call(11) }
        render?.close(); render = null
        client?.close(); client = null
        device?.close(); device = null
        enumerator?.close(); enumerator = null
        apartment.close()
    }

    companion object {
        fun open(format: PcmFormat, exclusive: Boolean, deviceId: String?): WindowsWasapiOutput {
            require(!format.bigEndian) { "Windows output requires little-endian PCM." }
            return WindowsWasapiOutput(format, exclusive, deviceId)
        }

        fun devices(): List<DesktopAudioDevice> = ComApartment().use { apartment ->
            apartment.enumerator().use { enumerator ->
                val result = PointerByReference()
                enumerator.check(3, "List audio devices", 0, 1, result)
                ComObject(result.value).use { collection ->
                    val size = IntByReference()
                    collection.check(3, "Count audio devices", size)
                    (0 until size.value.coerceIn(0, 256)).map { index ->
                        val item = PointerByReference()
                        collection.check(4, "Read an audio device", index, item)
                        ComObject(item.value).use { device ->
                            val identifier = PointerByReference()
                            device.check(5, "Read the audio device identifier", identifier)
                            val id = try { identifier.value.getWideString(0) } finally { ole.CoTaskMemFree(identifier.value) }
                            DesktopAudioDevice(id, deviceName(device))
                        }
                    }
                }
            }
        }

        internal fun waveFormat(format: PcmFormat, extensible: Boolean): Memory = Memory(if (extensible) 40 else 18).apply {
            clear()
            setShort(0, (if (extensible) 0xfffe else if (format.encoding == PcmEncoding.FLOAT) 3 else 1).toShort())
            setShort(2, format.channels.toShort())
            setInt(4, format.sampleRate)
            setInt(8, format.sampleRate * format.frameSize)
            setShort(12, format.frameSize.toShort())
            setShort(14, format.bitsPerSample.toShort())
            setShort(16, (if (extensible) 22 else 0).toShort())
            if (extensible) {
                setShort(18, format.bitsPerSample.toShort())
                setInt(20, when (format.channels) { 1 -> 4; 2 -> 3; 6 -> 0x3f; 8 -> 0x63f; else -> 0 })
                val subtype = guid(if (format.encoding == PcmEncoding.FLOAT) FLOAT_SUBTYPE else PCM_SUBTYPE)
                write(24, subtype.getByteArray(0, 16), 0, 16)
            }
        }

        private fun readWaveFormat(pointer: Pointer): PcmFormat {
            val tag = pointer.getShort(0).toInt() and 0xffff
            val bits = pointer.getShort(14).toInt() and 0xffff
            val floating = tag == 3 || tag == 0xfffe && pointer.getInt(24) == 3
            return PcmFormat(pointer.getInt(4), pointer.getShort(2).toInt() and 0xffff, bits,
                if (floating) PcmEncoding.FLOAT else if (bits == 8) PcmEncoding.UNSIGNED_INTEGER else PcmEncoding.SIGNED_INTEGER)
        }

        private fun selectedDevice(enumerator: ComObject, id: String?): ComObject {
            val result = PointerByReference()
            if (id == null) enumerator.check(4, "Open the default audio device", 0, 1, result)
            else enumerator.check(5, "Open the selected audio device", WString(id), result)
            return ComObject(result.value)
        }
        private fun activate(device: ComObject): ComObject {
            val result = PointerByReference()
            device.check(3, "Activate the audio device", guid(CLIENT_IID), 23, null, result)
            return ComObject(result.value)
        }
        private fun deviceName(device: ComObject): String = runCatching {
            val store = PointerByReference()
            device.check(4, "Read the device properties", 0, store)
            ComObject(store.value).use { properties ->
                val key = Memory(20).apply { write(0, guid("a45c254e-df1c-4efd-8020-67d146a850e0").getByteArray(0, 16), 0, 16); setInt(16, 14) }
                val value = Memory(24).apply { clear() }
                properties.check(5, "Read the audio device name", key, value)
                try { if (value.getShort(0).toInt() == 31) value.getPointer(8).getWideString(0) else "Audio device" }
                finally { ole.PropVariantClear(value) }
            }
        }.getOrDefault("Audio device")
    }
}

private interface WasapiOle32 : StdCallLibrary {
    fun CoInitializeEx(reserved: Pointer?, flags: Int): Int
    fun CoUninitialize()
    fun CoCreateInstance(classId: Pointer, outer: Pointer?, context: Int, interfaceId: Pointer, result: PointerByReference): Int
    fun CoTaskMemFree(pointer: Pointer?)
    fun PropVariantClear(value: Pointer): Int
}

private val ole: WasapiOle32 by lazy { Native.load("ole32", WasapiOle32::class.java) }
private const val CLIENT_IID = "1cb9ad4c-dbfa-4c32-b178-c2f568a703b2"
private const val RENDER_IID = "f294acfc-3146-4483-a7bf-addca7c260e2"
private const val PCM_SUBTYPE = "00000001-0000-0010-8000-00aa00389b71"
private const val FLOAT_SUBTYPE = "00000003-0000-0010-8000-00aa00389b71"

private class ComApartment : AutoCloseable {
    private val initialized = ole.CoInitializeEx(null, 0).also { checked(it, "Initialize Windows audio") } >= 0
    fun enumerator(): ComObject {
        val result = PointerByReference()
        checked(ole.CoCreateInstance(guid("bcde0395-e52f-467c-8e3d-c4579291692e"), null, 23,
            guid("a95664d2-9614-4f35-a746-de8db63617e6"), result), "Open Windows audio devices")
        return ComObject(result.value)
    }
    override fun close() { if (initialized) ole.CoUninitialize() }
}

private class ComObject(private val pointer: Pointer) : AutoCloseable {
    fun call(index: Int, vararg arguments: Any?): Int = Function.getFunction(
        pointer.getPointer(0).getPointer(index.toLong() * Native.POINTER_SIZE), Function.ALT_CONVENTION)
        .invokeInt(arrayOf(pointer, *arguments))
    fun check(index: Int, action: String, vararg arguments: Any?) { checked(call(index, *arguments), action) }
    override fun close() { call(2) }
}

private fun checked(status: Int, action: String) {
    if (status >= 0) return
    val detail = when (status) {
        0x88890004.toInt() -> "The audio device was disconnected. Choose an available output device."
        0x88890008.toInt() -> "The device doesn't support this audio format. Choose another device or turn off exclusive output."
        0x8889000a.toInt() -> "Another application is using this audio device. Close its audio stream or turn off exclusive output."
        0x8889000e.toInt() -> "Exclusive access is disabled for this device in Windows sound settings."
        else -> "Windows audio error 0x${status.toUInt().toString(16)}."
    }
    throw IOException("$action. $detail")
}

private fun guid(value: String): Memory {
    val uuid = UUID.fromString(value)
    return Memory(16).apply {
        setInt(0, (uuid.mostSignificantBits ushr 32).toInt())
        setShort(4, (uuid.mostSignificantBits ushr 16).toShort())
        setShort(6, uuid.mostSignificantBits.toShort())
        repeat(8) { setByte(8L + it, (uuid.leastSignificantBits ushr (56 - 8 * it)).toByte()) }
    }
}
