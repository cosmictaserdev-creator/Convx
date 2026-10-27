package com.convx.music.client.ios

import com.convx.music.account.SecureSessionStore
import com.convx.music.client.ios.keychaincompat.convx_keychain_clear
import com.convx.music.client.ios.keychaincompat.convx_keychain_read
import com.convx.music.client.ios.keychaincompat.convx_keychain_write
import kotlinx.cinterop.ByteVar
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.allocArray
import kotlinx.cinterop.cstr
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.readBytes
import kotlinx.cinterop.usePinned
import kotlinx.cinterop.value
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.random.Random

/** Stores the InnerTube session as a device-only Keychain generic password. */
@OptIn(ExperimentalForeignApi::class)
internal class IosSecureSessionStore(
    private val keychainAccount: String = SESSION_ACCOUNT,
) : SecureSessionStore {
    override suspend fun read(): String? = withContext(Dispatchers.Default) {
        memScoped {
            val buffer = allocArray<ByteVar>(MAX_SESSION_BYTES)
            val status = convx_keychain_read(
                keychainAccount.cstr.getPointer(this),
                buffer,
                MAX_SESSION_BYTES.toULong(),
            )
            when {
                status == ITEM_NOT_FOUND -> null
                status >= 0 -> buffer.readBytes(status).decodeToString()
                else -> error("iOS Keychain read failed with status $status.")
            }
        }
    }

    override suspend fun write(value: String) = withContext(Dispatchers.Default) {
        val bytes = value.encodeToByteArray()
        require(bytes.isNotEmpty()) { "The session is empty." }
        require(bytes.size <= MAX_SESSION_BYTES) { "The session is too large for secure storage." }
        memScoped {
            val nativeAccount = keychainAccount.cstr.getPointer(this)
            bytes.usePinned { pinned ->
                val status = convx_keychain_write(
                    nativeAccount,
                    pinned.addressOf(0),
                    bytes.size.toULong(),
                )
                check(status == SUCCESS) { "iOS Keychain write failed with status $status." }
            }
        }
    }

    override suspend fun clear() = withContext(Dispatchers.Default) {
        val status = memScoped {
            convx_keychain_clear(keychainAccount.cstr.getPointer(this))
        }
        check(status == SUCCESS || status == ITEM_NOT_FOUND) {
            "iOS Keychain clear failed with status $status."
        }
    }

    private companion object {
        const val SESSION_ACCOUNT = "session"
        const val MAX_SESSION_BYTES = 65_536
        const val SUCCESS = 0
        const val ITEM_NOT_FOUND = -25_300
    }
}

/** Exercises the same Keychain adapter using an isolated account key in the signed host process. */
internal suspend fun verifyIosSecureSessionStoreRoundTrip(): String {
    val store = IosSecureSessionStore(keychainAccount = "ci-roundtrip-${Random.nextLong()}")
    var result: String? = null
    try {
        val payload = "convx-keychain-roundtrip"
        store.write(payload)
        check(store.read() == payload) { "Keychain did not return the stored session probe." }
        store.clear()
        check(store.read() == null) { "Keychain still returned the session probe after deletion." }
        result = "Keychain session storage round trip passed"
    } catch (cancellation: CancellationException) {
        throw cancellation
    } catch (failure: Exception) {
        result = "Keychain session storage round trip failed: ${failure.message ?: failure::class.simpleName}"
    } finally {
        try {
            store.clear()
        } catch (cancellation: CancellationException) {
            throw cancellation
        } catch (failure: Exception) {
            if (result?.contains("passed") == true) {
                result = "Keychain session storage round trip failed during cleanup: ${failure.message ?: failure::class.simpleName}"
            }
        }
    }
    return result ?: "Keychain session storage round trip failed without a result."
}
