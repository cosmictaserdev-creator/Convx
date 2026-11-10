package com.convx.music.client.desktop

import com.convx.music.playback.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.io.File
import java.util.UUID

internal data class DesktopDiyState(
    val selectedId: String? = null,
    val wide: Boolean = true,
    val emoji: String = "✨",
    val message: String? = null,
    val busy: Boolean = false,
)

internal class DesktopDiyStateHolder(private val repository: PlaybackSettingsRepository, val assets: DesktopPlayerAssets) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val mutableState = MutableStateFlow(DesktopDiyState())
    val state = mutableState.asStateFlow()
    fun select(id: String?) { mutableState.update { it.copy(selectedId = id) } }
    fun orientation(wide: Boolean) { mutableState.update { it.copy(wide = wide) } }
    fun emoji(value: String) { mutableState.update { it.copy(emoji = value.take(64)) } }

    fun edit(transform: (PlayerCustomization) -> PlayerCustomization) = task {
        repository.updateSettings { it.copy(playerCustomization = transform(it.playerCustomization).normalized()) }
    }

    fun editSelected(transform: (DiySticker) -> DiySticker) {
        val selected = state.value.selectedId ?: return
        edit { player -> player.copy(diy = player.diy.copy(stickers = player.diy.stickers.map { if (it.id == selected) transform(it) else it })) }
    }

    fun move(id: String, wide: Boolean, transform: DiyTransform) = edit { player ->
        player.copy(diy = player.diy.copy(stickers = player.diy.stickers.map { if (it.id == id) it.withTransform(wide, transform) else it }))
    }

    fun addEmoji() = task {
        val source = state.value.emoji.trim()
        require(source.isNotBlank()) { "Enter an emoji first." }
        addSticker(DiySticker(UUID.randomUUID().toString(), DiyStickerKind.EMOJI, source))
    }

    fun addImage(file: File) = task {
        val player = repository.getSettings().playerCustomization
        require(player.diy.stickers.size < DIY_MAX_STICKERS) { "The player can have up to 10 stickers." }
        val name = assets.install(file, player)
        addSticker(DiySticker(UUID.randomUUID().toString(), DiyStickerKind.IMAGE, name))
    }

    private suspend fun addSticker(sticker: DiySticker) {
        repository.updateSettings { settings ->
            val player = settings.playerCustomization
            require(player.diy.stickers.size < DIY_MAX_STICKERS) { "The player can have up to 10 stickers." }
            settings.copy(playerCustomization = player.copy(diy = player.diy.copy(stickers = player.diy.stickers + sticker)).normalized())
        }
        select(sticker.id)
    }

    fun deleteSelected() {
        val selected = state.value.selectedId ?: return
        edit { it.copy(diy = it.diy.copy(stickers = it.diy.stickers.filterNot { sticker -> sticker.id == selected })) }
        select(null)
    }

    fun moveSection(section: PlayerSection, direction: Int) = edit { player ->
        val order = player.normalized().order.toMutableList()
        val from = order.indexOf(section); val to = from + direction
        if (from >= 0 && to in order.indices) { order.removeAt(from); order.add(to, section) }
        player.copy(order = order)
    }

    fun setIcon(slot: String, source: File) = task {
        val name = assets.install(source, repository.getSettings().playerCustomization)
        repository.updateSettings { it.copy(playerCustomization = it.playerCustomization.copy(icons = it.playerCustomization.icons + (slot to PlayerIconOverride(name)))) }
    }

    fun exportPreset(destination: File) = task {
        val settings = repository.getSettings()
        assets.exportPreset(destination, DesktopVisualPreset(name = destination.nameWithoutExtension,
            appearance = settings.appearance.copy(customFontFile = null), player = settings.playerCustomization,
            darkTheme = settings.darkTheme, lyricsFontSize = settings.lyricsFontSize))
        mutableState.update { it.copy(message = "Preset exported.") }
    }

    fun importPreset(source: File) = task {
        val preset = assets.importPreset(source)
        repository.updateSettings { it.copy(appearance = preset.appearance, playerCustomization = preset.player,
            darkTheme = preset.darkTheme, lyricsFontSize = preset.lyricsFontSize).normalized() }
        mutableState.update { it.copy(selectedId = null, message = "Applied ${preset.name}.") }
    }

    private fun task(action: suspend () -> Unit) {
        scope.launch {
            mutableState.update { it.copy(busy = true, message = null) }
            try { action() }
            catch (cancelled: CancellationException) { throw cancelled }
            catch (error: Exception) { mutableState.update { it.copy(message = error.message ?: "Couldn't save the player.") } }
            finally { mutableState.update { it.copy(busy = false) } }
        }
    }

    fun close() = scope.cancel()
}
