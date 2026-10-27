package com.convx.music.client.desktop

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runInterruptible
import java.io.File
import java.net.URI
import java.util.concurrent.TimeUnit

internal class DesktopFileAssociations {
    val isSupported: Boolean get() = System.getProperty("os.name").startsWith("Windows", true)

    suspend fun openDefaultAppSettings() = runInterruptible(Dispatchers.IO) {
        check(isSupported) { "Choose Convx in your system's default applications settings." }
        val jar = File(DesktopFileAssociations::class.java.protectionDomain.codeSource.location.toURI())
        check(jar.isFile && jar.extension == "jar") { "Launch the packaged desktop application to register audio file types." }
        val runtime = File(System.getProperty("java.home"), "bin/javaw.exe")
        check(runtime.isFile) { "The desktop Java runtime is unavailable." }
        val dataDirectory = File(desktopLibraryDatabasePath()).parentFile.absolutePath
        val command = listOf(runtime.path, "-Duser.home=${System.getProperty("user.home")}",
            "-Dconvx.desktop.data.dir=$dataDirectory", "-jar", jar.absolutePath, "%1")
            .joinToString(" ") { argument -> "\"${argument.replace("\"", "\\\"")}\"" }
        val classes = "HKCU\\Software\\Classes"
        val capabilities = "Software\\Convx\\Capabilities"
        fun value(key: String, name: String?, data: String) {
            val arguments = mutableListOf("reg.exe", "add", key)
            if (name == null) arguments.add("/ve") else arguments.addAll(listOf("/v", name))
            arguments.addAll(listOf("/t", "REG_SZ", "/d", data, "/f"))
            val process = ProcessBuilder(arguments).redirectErrorStream(true).start()
            if (!process.waitFor(10, TimeUnit.SECONDS)) {
                process.destroyForcibly()
                error("Windows took too long to register the audio file types.")
            }
            check(process.exitValue() == 0) { "Windows couldn't register Convx for audio files." }
        }
        value("$classes\\Convx.Audio", null, "Convx audio")
        value("$classes\\Convx.Audio\\shell\\open\\command", null, command)
        value("HKCU\\$capabilities", "ApplicationName", "Convx")
        value("HKCU\\$capabilities", "ApplicationDescription", "Play music and manage your audio library with Convx.")
        for (extension in DesktopMediaLibrary.AUDIO_EXTENSIONS.sorted()) {
            value("HKCU\\$capabilities\\FileAssociations", ".$extension", "Convx.Audio")
            value("$classes\\.$extension\\OpenWithProgids", "Convx.Audio", "")
        }
        value("HKCU\\Software\\RegisteredApplications", "Convx", capabilities)
        java.awt.Desktop.getDesktop().browse(URI("ms-settings:defaultapps?registeredAppUser=Convx"))
    }
}
