package com.convx.music.core.updater

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.pm.PackageInfoCompat
import java.io.File
import java.security.MessageDigest
import java.util.zip.ZipFile

class ApkVerifier(private val context: Context) {
    @Suppress("DEPRECATION")
    fun verify(file: File, asset: UpdateAsset) {
        if (!file.isFile || file.length() != asset.size) throw UpdateException(UpdateFailure.VERIFICATION)
        val digest = MessageDigest.getInstance("SHA-256")
        file.inputStream().use { input ->
            val buffer = ByteArray(64 * 1024)
            while (true) {
                val read = input.read(buffer)
                if (read == -1) break
                digest.update(buffer, 0, read)
            }
        }
        val actual = digest.digest().joinToString("") { "%02x".format(it) }
        if (!MessageDigest.isEqual(asset.sha256.lowercase().toByteArray(), actual.toByteArray())) throw UpdateException(UpdateFailure.VERIFICATION)
        val pm = context.packageManager
        val flags = if (Build.VERSION.SDK_INT >= 28) PackageManager.GET_SIGNING_CERTIFICATES else PackageManager.GET_SIGNATURES
        val candidate = pm.getPackageArchiveInfo(file.absolutePath, flags) ?: throw UpdateException(UpdateFailure.VERIFICATION)
        val installed = pm.getPackageInfo(context.packageName, flags)
        if (candidate.packageName != context.packageName || PackageInfoCompat.getLongVersionCode(candidate) != asset.versionCode ||
            asset.versionCode <= PackageInfoCompat.getLongVersionCode(installed) ||
            (candidate.applicationInfo?.minSdkVersion ?: Int.MAX_VALUE) > Build.VERSION.SDK_INT) throw UpdateException(UpdateFailure.INCOMPATIBLE)
        val matching = if (Build.VERSION.SDK_INT >= 28) {
            val old = installed.signingInfo ?: throw UpdateException(UpdateFailure.VERIFICATION)
            val next = candidate.signingInfo ?: throw UpdateException(UpdateFailure.VERIFICATION)
            if (old.hasMultipleSigners() || next.hasMultipleSigners()) old.apkContentsSigners.toSet() == next.apkContentsSigners.toSet()
            else next.signingCertificateHistory.any { it == old.apkContentsSigners.single() }
        } else installed.signatures?.toSet() == candidate.signatures?.toSet() && !candidate.signatures.isNullOrEmpty()
        if (!matching) throw UpdateException(UpdateFailure.VERIFICATION)
        val compatible = ZipFile(file).use { zip ->
            val nativeAbis = zip.entries().asSequence().map { it.name }.filter { it.startsWith("lib/") && it.endsWith(".so") }
                .map { it.split('/')[1] }.toSet()
            nativeAbis.isEmpty() || nativeAbis.any { it in Build.SUPPORTED_ABIS }
        }
        if (!compatible) throw UpdateException(UpdateFailure.INCOMPATIBLE)
    }
}
