package com.convx.music.lyrics

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import com.convx.music.constants.EnableBetterLyricsKey
import com.convx.music.constants.EnableKugouKey
import com.convx.music.constants.EnableLrcLibKey
import com.convx.music.constants.EnableMusixmatchKey
import com.convx.music.constants.EnablePaxsenixKey
import com.convx.music.constants.EnableSimpMusicKey
import com.convx.music.constants.EnableUnisonKey
import com.convx.music.constants.EnableYouLyPlusKey
import com.convx.music.constants.PreferredLyricsProvider
import com.convx.music.lyrics.bitchord.PaxSenix
import com.convx.music.utils.dataStore
import com.convx.music.utils.get

object LyricsProviderSettings {
    fun idForLegacyPreference(provider: PreferredLyricsProvider): String = when (provider) {
        PreferredLyricsProvider.LRCLIB -> "LrcLib"
        PreferredLyricsProvider.KUGOU -> "Kugou"
        PreferredLyricsProvider.BETTER_LYRICS -> "BetterLyrics"
        PreferredLyricsProvider.SIMPMUSIC -> "SimpMusic"
        PreferredLyricsProvider.YOULYPLUS -> "YouLyPlus"
        PreferredLyricsProvider.PAXSENIX -> "Paxsenix"
    }

    fun enabledKey(id: String): Preferences.Key<Boolean> = when (id) {
        "BetterLyrics" -> EnableBetterLyricsKey
        "Kugou" -> EnableKugouKey
        "LrcLib" -> EnableLrcLibKey
        "Musixmatch" -> EnableMusixmatchKey
        "Paxsenix" -> EnablePaxsenixKey
        "SimpMusic" -> EnableSimpMusicKey
        "Unison" -> EnableUnisonKey
        "YouLyPlus" -> EnableYouLyPlusKey
        else -> booleanPreferencesKey("enableLyrics$id")
    }

    fun isEnabled(context: Context, provider: LyricsProvider): Boolean =
        context.dataStore[enabledKey(provider.id)] ?: true

    fun initialize(context: Context, provider: LyricsProvider) {
        if (provider !is BitChordLyricsProvider) return
        val credentials = context.applicationContext.getSharedPreferences("lyrics_credentials", Context.MODE_PRIVATE)
        PaxSenix.setApiKey(credentials.getString("paxsenix_api_key", "").orEmpty())
    }
}
