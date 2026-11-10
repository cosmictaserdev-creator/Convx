package com.convx.music.client.desktop

import com.convx.music.playback.*
import org.junit.Rule
import org.junit.Test
import org.junit.Assert.*
import org.junit.rules.TemporaryFolder
import java.awt.image.BufferedImage
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream
import javax.imageio.ImageIO

class DesktopPlayerAssetsTest {
    @get:Rule val folder = TemporaryFolder()

    @Test fun presetMovesImagesToAnotherInstallationAndPreservesLayout() {
        val source = folder.newFile("sticker.png")
        ImageIO.write(BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB), "png", source)
        val exporter = DesktopPlayerAssets(folder.newFolder("first"))
        val name = exporter.install(source, PlayerCustomization())
        val player = PlayerCustomization(style = PlayerStyle.STACKED, diy = DiyLayout(stickers = listOf(
            DiySticker("art", DiyStickerKind.IMAGE, name, portrait = DiyTransform(x = .2f), landscape = DiyTransform(x = .8f)))),
            icons = mapOf("PLAY" to PlayerIconOverride(name, tint = true)))
        val archive = folder.newFile("portable.convxpreset")
        exporter.exportPreset(archive, DesktopVisualPreset(player = player, appearance = AppearanceSettings(accentArgb = 0xFF007AFF)))
        val importer = DesktopPlayerAssets(folder.newFolder("second"))
        val imported = importer.importPreset(archive)
        assertEquals(player, imported.player)
        assertEquals(0xFF007AFF, imported.appearance.accentArgb)
        assertArrayEquals(source.readBytes(), importer.file(imported.player.diy.stickers.single().source)!!.readBytes())
        assertTrue(source.isFile)
    }

    @Test fun archiveTraversalIsRejectedBeforeWritingAnyAssets() {
        val archive = folder.newFile("invalid.convxpreset")
        ZipOutputStream(archive.outputStream()).use {
            it.putNextEntry(ZipEntry("assets/../../outside.png")); it.write(byteArrayOf(1)); it.closeEntry()
        }
        val directory = folder.newFolder("assets")
        val error = runCatching { DesktopPlayerAssets(directory).importPreset(archive) }.exceptionOrNull()
        assertTrue(error is IllegalArgumentException)
        assertTrue(directory.listFiles()!!.isEmpty())
    }

    @Test fun androidPresetImportsDiyAndIgnoresUnrelatedPreferences() {
        val archive = folder.newFile("android.convxpreset")
        val manifest = """{"version":1,"name":"Phone look","diy":{"version":1,"stickers":[{"id":"emoji","kind":"EMOJI","source":"✨"}]},"prefs":[{"n":"playerLayoutOrder","t":"STRING","v":"CONTROLS,TRACK_INFO"},{"n":"playerLayoutHiddenSlots","t":"STRING","v":"CONTROLS,SEEK_BAR"},{"n":"proxy","t":"STRING","v":"ignored"}]}"""
        ZipOutputStream(archive.outputStream()).use {
            it.putNextEntry(ZipEntry("manifest.json")); it.write(manifest.toByteArray()); it.closeEntry()
        }
        val imported = DesktopPlayerAssets(folder.newFolder("assets")).importPreset(archive)
        assertEquals("Phone look", imported.name)
        assertEquals("✨", imported.player.diy.stickers.single().source)
        assertEquals(PlayerSection.CONTROLS, imported.player.order.first())
        assertEquals(setOf(PlayerSection.SEEK_BAR), imported.player.hidden)
    }
}
