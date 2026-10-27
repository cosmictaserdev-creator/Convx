package com.convx.music.client.desktop

import com.convx.music.account.SecureSessionStore
import com.sun.jna.Library
import com.sun.jna.Memory
import com.sun.jna.Native
import com.sun.jna.NativeLibrary
import com.sun.jna.NativeLong
import com.sun.jna.Pointer
import com.sun.jna.ptr.PointerByReference
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption.ATOMIC_MOVE
import java.nio.file.StandardCopyOption.REPLACE_EXISTING
import java.security.MessageDigest
import java.util.Base64
import java.util.UUID
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.withContext

private const val DEFAULT_SESSION_ACCOUNT_KEY = "youtube-music-session"

/** Uses Windows DPAPI, macOS Keychain, or Linux Secret Service. No plaintext file fallback is allowed. */
internal class DesktopSecureSessionStore(
    private val accountKey: String = DEFAULT_SESSION_ACCOUNT_KEY,
) : SecureSessionStore {
    override suspend fun read(): String? = withContext(Dispatchers.IO) {
        when (currentDesktopOs()) {
            DesktopOs.WINDOWS -> WindowsDpapi.read(accountKey)
            DesktopOs.MACOS -> MacKeychain.read(accountKey)
            DesktopOs.LINUX -> LinuxSecretService.read(accountKey)
        }
    }

    override suspend fun write(value: String) = withContext(Dispatchers.IO) {
        when (currentDesktopOs()) {
            DesktopOs.WINDOWS -> WindowsDpapi.write(accountKey, value)
            DesktopOs.MACOS -> MacKeychain.write(accountKey, value)
            DesktopOs.LINUX -> LinuxSecretService.write(accountKey, value)
        }
    }

    override suspend fun clear() = withContext(Dispatchers.IO) {
        when (currentDesktopOs()) {
            DesktopOs.WINDOWS -> WindowsDpapi.clear(accountKey)
            DesktopOs.MACOS -> MacKeychain.clear(accountKey)
            DesktopOs.LINUX -> LinuxSecretService.clear(accountKey)
        }
    }
}

internal suspend fun verifyDesktopSecureSessionStoreRoundTrip() {
    val store = DesktopSecureSessionStore("ci-roundtrip-${UUID.randomUUID()}")
    val payload = "convx-desktop-secure-session-roundtrip"
    try {
        store.write(payload)
        check(store.read() == payload) { "Desktop secure storage did not return the stored session probe." }
        store.clear()
        check(store.read() == null) { "Desktop secure storage returned the probe after deletion." }
    } finally {
        withContext(NonCancellable) { store.clear() }
    }
}

private enum class DesktopOs { WINDOWS, MACOS, LINUX }

private fun currentDesktopOs(): DesktopOs = when {
    System.getProperty("os.name").startsWith("Windows", ignoreCase = true) -> DesktopOs.WINDOWS
    System.getProperty("os.name").startsWith("Mac", ignoreCase = true) -> DesktopOs.MACOS
    System.getProperty("os.name").startsWith("Linux", ignoreCase = true) -> DesktopOs.LINUX
    else -> error("Secure account storage is not available on this desktop OS.")
}

private object WindowsDpapi {
    fun read(accountKey: String): String? {
        val sessionFile = sessionFile(accountKey)
        if (!Files.exists(sessionFile)) return null
        val encrypted = Files.readString(sessionFile).trim()
        if (encrypted.isEmpty()) return null
        val plainBase64 = runPowerShell(UNPROTECT_SCRIPT, encrypted)
        return Base64.getDecoder().decode(plainBase64).decodeToString()
    }

    fun write(accountKey: String, value: String) {
        val plainBase64 = Base64.getEncoder().encodeToString(value.encodeToByteArray())
        val encrypted = runPowerShell(PROTECT_SCRIPT, plainBase64)
        val sessionFile = sessionFile(accountKey)
        val parent = sessionFile.parent
        Files.createDirectories(parent)
        val stagedFile = Files.createTempFile(parent, "session-", ".tmp")
        try {
            Files.writeString(stagedFile, encrypted)
            try {
                Files.move(stagedFile, sessionFile, ATOMIC_MOVE, REPLACE_EXISTING)
            } catch (_: java.nio.file.AtomicMoveNotSupportedException) {
                Files.move(stagedFile, sessionFile, REPLACE_EXISTING)
            }
        } finally {
            Files.deleteIfExists(stagedFile)
        }
    }

    fun clear(accountKey: String) {
        Files.deleteIfExists(sessionFile(accountKey))
    }

    private fun sessionFile(accountKey: String): Path {
        val fileName = if (accountKey == DEFAULT_SESSION_ACCOUNT_KEY) {
            "secure-session.dpapi"
        } else {
            val digest = MessageDigest.getInstance("SHA-256").digest(accountKey.encodeToByteArray())
            "secure-session-${Base64.getUrlEncoder().withoutPadding().encodeToString(digest)}.dpapi"
        }
        return Path.of(System.getProperty("user.home"), ".convx", fileName)
    }

    private fun runPowerShell(script: String, input: String): String {
        val process = ProcessBuilder(
            "powershell.exe",
            "-NoLogo",
            "-NoProfile",
            "-NonInteractive",
            "-Command",
            script,
        ).redirectErrorStream(true).start()
        process.outputStream.bufferedWriter(Charsets.UTF_8).use { it.write(input) }
        if (!process.waitFor(PROCESS_TIMEOUT_SECONDS, TimeUnit.SECONDS)) {
            process.destroyForcibly()
            error("Windows secure storage did not respond.")
        }
        val output = process.inputStream.bufferedReader(Charsets.UTF_8).use { it.readText() }.trim()
        check(process.exitValue() == 0 && output.isNotEmpty()) { "Windows secure storage failed." }
        return output
    }

    private const val ENTROPY = "com.convx.music.youtube-session"
    private const val PROCESS_TIMEOUT_SECONDS = 20L
    private val PROTECT_SCRIPT = """
        Add-Type -AssemblyName System.Security
        ${'$'}inputText = [Console]::In.ReadToEnd()
        ${'$'}plain = [Convert]::FromBase64String(${'$'}inputText)
        ${'$'}entropy = [Text.Encoding]::UTF8.GetBytes('$ENTROPY')
        ${'$'}protected = [Security.Cryptography.ProtectedData]::Protect(${'$'}plain, ${'$'}entropy, [Security.Cryptography.DataProtectionScope]::CurrentUser)
        [Console]::Out.Write([Convert]::ToBase64String(${'$'}protected))
    """.trimIndent()
    private val UNPROTECT_SCRIPT = """
        Add-Type -AssemblyName System.Security
        ${'$'}inputText = [Console]::In.ReadToEnd()
        ${'$'}protected = [Convert]::FromBase64String(${'$'}inputText)
        ${'$'}entropy = [Text.Encoding]::UTF8.GetBytes('$ENTROPY')
        ${'$'}plain = [Security.Cryptography.ProtectedData]::Unprotect(${'$'}protected, ${'$'}entropy, [Security.Cryptography.DataProtectionScope]::CurrentUser)
        [Console]::Out.Write([Convert]::ToBase64String(${'$'}plain))
    """.trimIndent()
}

private object LinuxSecretService {
    fun read(account: String): String? {
        val result = runSecretTool(
            listOf("lookup", "service", SERVICE_NAME, "account", account),
            allowMissing = true,
        ) ?: return null
        return result
    }

    fun write(account: String, value: String) {
        runSecretTool(
            listOf("store", "--label", "Convx YouTube Music session", "service", SERVICE_NAME, "account", account),
            input = value,
        )
    }

    fun clear(account: String) {
        runSecretTool(listOf("clear", "service", SERVICE_NAME, "account", account), allowMissing = true)
    }

    private fun runSecretTool(
        arguments: List<String>,
        input: String? = null,
        allowMissing: Boolean = false,
    ): String? {
        val process = ProcessBuilder(listOf("secret-tool") + arguments).redirectErrorStream(true).start()
        process.outputStream.bufferedWriter(Charsets.UTF_8).use { writer -> input?.let(writer::write) }
        if (!process.waitFor(PROCESS_TIMEOUT_SECONDS, TimeUnit.SECONDS)) {
            process.destroyForcibly()
            error("Linux Secret Service did not respond.")
        }
        val output = process.inputStream.bufferedReader(Charsets.UTF_8).use { it.readText() }
        if (process.exitValue() != 0) {
            if (allowMissing && output.isBlank()) return null
            error("Linux Secret Service operation failed.")
        }
        return output.trimEnd('\r', '\n')
    }

    private const val SERVICE_NAME = "com.convx.music"
    private const val PROCESS_TIMEOUT_SECONDS = 20L
}

private object MacKeychain {
    private val coreFoundation = Native.load("CoreFoundation", CoreFoundationApi::class.java)
    private val security = Native.load("Security", SecurityApi::class.java)
    private val coreFoundationLibrary = NativeLibrary.getInstance("CoreFoundation")
    private val securityLibrary = NativeLibrary.getInstance("Security")
    private val dictionaryKeyCallbacks = globalAddress("CoreFoundation", "kCFTypeDictionaryKeyCallBacks")
    private val dictionaryValueCallbacks = globalAddress("CoreFoundation", "kCFTypeDictionaryValueCallBacks")
    private val itemNotFound = -25_300

    fun read(account: String): String? = withBaseQuery(account) { query ->
        put(query, "kSecReturnData", global("CoreFoundation", "kCFBooleanTrue"))
        put(query, "kSecMatchLimit", global("Security", "kSecMatchLimitOne"))
        val result = PointerByReference()
        val status = security.SecItemCopyMatching(query, result)
        if (status == itemNotFound) return@withBaseQuery null
        check(status == 0) { "macOS Keychain read failed with status $status." }
        val data = result.value ?: error("macOS Keychain returned no session data.")
        try {
            val length = coreFoundation.CFDataGetLength(data).toLong().toInt()
            val bytes = coreFoundation.CFDataGetBytePtr(data)?.getByteArray(0, length)
                ?: error("macOS Keychain returned invalid session data.")
            bytes.decodeToString()
        } finally {
            coreFoundation.CFRelease(data)
        }
    }

    fun write(account: String, value: String) {
        val bytes = value.encodeToByteArray()
        val memory = Memory(bytes.size.toLong()).apply { write(0, bytes, 0, bytes.size) }
        val data = coreFoundation.CFDataCreate(null, memory, NativeLong(bytes.size.toLong()))
            ?: error("Could not prepare secure session data for Keychain.")
        try {
            withBaseQuery(account) { query ->
                val updates = dictionary()
                try {
                    put(updates, "kSecValueData", data)
                    val updateStatus = security.SecItemUpdate(query, updates)
                    val status = if (updateStatus == itemNotFound) {
                        put(query, "kSecValueData", data)
                        put(query, "kSecAttrAccessible", global("Security", "kSecAttrAccessibleAfterFirstUnlock"))
                        security.SecItemAdd(query, null)
                    } else {
                        updateStatus
                    }
                    check(status == 0) { "macOS Keychain write failed with status $status." }
                } finally {
                    coreFoundation.CFRelease(updates)
                }
            }
        } finally {
            coreFoundation.CFRelease(data)
        }
    }

    fun clear(account: String) {
        withBaseQuery(account) { query ->
            val status = security.SecItemDelete(query)
            check(status == 0 || status == itemNotFound) { "macOS Keychain clear failed with status $status." }
        }
    }

    private inline fun <T> withBaseQuery(account: String, block: (Pointer) -> T): T {
        val query = dictionary()
        val service = string(SERVICE_NAME)
        val accountValue = string(account)
        try {
            put(query, "kSecClass", global("Security", "kSecClassGenericPassword"))
            put(query, "kSecAttrService", service)
            put(query, "kSecAttrAccount", accountValue)
            return block(query)
        } finally {
            coreFoundation.CFRelease(accountValue)
            coreFoundation.CFRelease(service)
            coreFoundation.CFRelease(query)
        }
    }

    private fun dictionary(): Pointer = coreFoundation.CFDictionaryCreateMutable(
        null,
        NativeLong(0),
        dictionaryKeyCallbacks,
        dictionaryValueCallbacks,
    ) ?: error("Could not create a Keychain query.")

    private fun string(value: String): Pointer = coreFoundation.CFStringCreateWithCString(
        null,
        value,
        UTF8_ENCODING,
    ) ?: error("Could not prepare a Keychain value.")

    private fun put(dictionary: Pointer, keyName: String, value: Pointer) {
        coreFoundation.CFDictionarySetValue(dictionary, global("Security", keyName), value)
    }

    private fun global(library: String, symbol: String): Pointer =
        (if (library == "Security") securityLibrary else coreFoundationLibrary)
            .getGlobalVariableAddress(symbol)
            .getPointer(0)
            ?: error("Missing Keychain symbol $symbol.")

    private fun globalAddress(library: String, symbol: String): Pointer =
        (if (library == "Security") securityLibrary else coreFoundationLibrary)
            .getGlobalVariableAddress(symbol)

    private interface CoreFoundationApi : Library {
        fun CFStringCreateWithCString(allocator: Pointer?, cString: String, encoding: Int): Pointer?
        fun CFDictionaryCreateMutable(
            allocator: Pointer?,
            capacity: NativeLong,
            keyCallBacks: Pointer,
            valueCallBacks: Pointer,
        ): Pointer?
        fun CFDictionarySetValue(dictionary: Pointer, key: Pointer, value: Pointer)
        fun CFDataCreate(allocator: Pointer?, bytes: Pointer, length: NativeLong): Pointer?
        fun CFDataGetLength(data: Pointer): NativeLong
        fun CFDataGetBytePtr(data: Pointer): Pointer?
        fun CFRelease(value: Pointer)
    }

    private interface SecurityApi : Library {
        fun SecItemCopyMatching(query: Pointer, result: PointerByReference): Int
        fun SecItemUpdate(query: Pointer, attributesToUpdate: Pointer): Int
        fun SecItemAdd(attributes: Pointer, result: PointerByReference?): Int
        fun SecItemDelete(query: Pointer): Int
    }

    private const val SERVICE_NAME = "com.convx.music.youtube"
    private const val UTF8_ENCODING = 0x08000100
}
