package com.convx.music.client.desktop

import com.sun.jna.Library
import com.sun.jna.Memory
import com.sun.jna.Native
import com.sun.jna.Pointer
import com.sun.jna.ptr.IntByReference

/** Applies the Windows 11 system Mica material to the native desktop window. */
internal object WindowsMicaBackdrop {
    private const val DWMWA_SYSTEMBACKDROP_TYPE = 38
    private const val DWMWA_USE_IMMERSIVE_DARK_MODE = 20
    private const val DWMWA_WINDOW_CORNER_PREFERENCE = 33
    private const val DWMSBT_MAINWINDOW = 2
    private const val DWMWCP_ROUND = 2
    private const val MARGINS_SIZE = 16L
    private const val HRESULT_SUCCESS = 0

    private interface DwmApi : Library {
        fun DwmSetWindowAttribute(
            window: Pointer,
            attribute: Int,
            value: IntByReference,
            valueSize: Int,
        ): Int

        fun DwmExtendFrameIntoClientArea(window: Pointer, margins: Pointer): Int
    }

    fun apply(window: java.awt.Window, darkTheme: Boolean = false): Boolean {
        if (!System.getProperty("os.name").startsWith("Windows", ignoreCase = true)) return false
        if (!window.isDisplayable) return false

        return runCatching {
            val nativeWindow = Native.getWindowPointer(window)
            if (Pointer.nativeValue(nativeWindow) == 0L) return false

            val dwmApi = Native.load("dwmapi", DwmApi::class.java)
            dwmApi.DwmSetWindowAttribute(
                window = nativeWindow,
                attribute = DWMWA_USE_IMMERSIVE_DARK_MODE,
                value = IntByReference(if (darkTheme) 1 else 0),
                valueSize = Int.SIZE_BYTES,
            )
            dwmApi.DwmSetWindowAttribute(
                window = nativeWindow,
                attribute = DWMWA_WINDOW_CORNER_PREFERENCE,
                value = IntByReference(DWMWCP_ROUND),
                valueSize = Int.SIZE_BYTES,
            )
            val backdropResult = dwmApi.DwmSetWindowAttribute(
                window = nativeWindow,
                attribute = DWMWA_SYSTEMBACKDROP_TYPE,
                value = IntByReference(DWMSBT_MAINWINDOW),
                valueSize = Int.SIZE_BYTES,
            )
            if (backdropResult != HRESULT_SUCCESS) return false

            // Let DWM draw the system material behind transparent Compose pixels.
            val margins = Memory(MARGINS_SIZE)
            try {
                repeat(4) { index -> margins.setInt(index * Int.SIZE_BYTES.toLong(), -1) }
                dwmApi.DwmExtendFrameIntoClientArea(nativeWindow, margins) == HRESULT_SUCCESS
            } finally {
                margins.close()
            }
        }.getOrDefault(false)
    }

}
