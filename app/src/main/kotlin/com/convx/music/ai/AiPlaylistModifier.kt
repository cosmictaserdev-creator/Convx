/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convx.music.ai

import android.content.Context
import com.music.innertube.SearchFilter
import com.music.innertube.YouTube
import com.music.innertube.models.SongItem
import com.convx.music.db.InternalDatabase
import com.convx.music.db.MusicDatabase
import com.convx.music.db.entities.PlaylistSong
import com.convx.music.db.entities.PlaylistSongMap
import com.convx.music.db.entities.SongEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject

object AiPlaylistModifier {

    suspend fun modifyPlaylist(
        context: Context,
        playlistId: String,
        currentSongs: List<PlaylistSong>,
        userPrompt: String,
        onLog: suspend (String) -> Unit
    ) = withContext(Dispatchers.IO) {
        val database = InternalDatabase.newInstance(context)

        onLog("Connecting to AI Provider...")

        val currentPlaylistJson = currentSongs.toPlaylistJson()

        val systemPrompt = """
            You are a highly accurate and strict music historian. The user wants to modify an existing playlist based on a specific prompt.
            The user will provide an INSTRUCTION.
            The CURRENT playlist is:
            $currentPlaylistJson

            You must output ONLY a valid JSON object containing exactly two arrays: "remove_ids" (list of internal IDs to remove) and "additions" (list of objects with "title" and "artist").
            
            CRITICAL RULES:
            1. YOU MUST VERIFY THE RELEASE YEAR, MOVIE, AND ARTIST/ACTOR for EVERY SINGLE TRACK you add or remove.
            2. For removals, ONLY include the "id" from the CURRENT playlist that match the user's removal instruction.
            3. For additions, ONLY include songs that match the user's addition instruction.
            4. You MUST output ONLY raw JSON. Do NOT include any markdown formatting (like ```json), explanations, or conversational text.
            
            Example format:
            {
              "remove_ids": [123, 456],
              "additions": [
                {"title": "Song Name", "artist": "Artist Name"}
              ]
            }
        """.trimIndent()

        val jsonOutput = AiChatClient.complete(context, systemPrompt, userPrompt, onLog) ?: return@withContext

        onLog("Parsing AI response...")
        val cleanJsonStr = AiRequests.jsonPart(jsonOutput, '{')

        val parsedJson = try {
            JSONObject(cleanJsonStr)
        } catch (e: Exception) {
            onLog("Failed to parse AI output.")
            return@withContext
        }

        val removeIds = parsedJson.optJSONArray("remove_ids")
        val additions = parsedJson.optJSONArray("additions")

        val totalToRemove = removeIds?.length() ?: 0
        val totalToAdd = additions?.length() ?: 0

        if (removeIds != null && totalToRemove > 0) {
            onLog("Removing $totalToRemove songs...")
            removeSongs(database, currentSongs, removeIds)
        }

        if (additions != null && totalToAdd > 0) {
            addSongs(database, playlistId, currentSongs, additions, onLog)
        }

        if (totalToRemove == 0 && totalToAdd == 0) {
            onLog("No modifications requested by AI.")
        } else {
            onLog("Done! Playlist modified.")
        }
    }

    private fun List<PlaylistSong>.toPlaylistJson(): String = JSONArray().apply {
        forEach { playlistSong ->
            put(JSONObject().apply {
                put("id", playlistSong.map.id)
                put("title", playlistSong.song.song.title)
                put("artist", playlistSong.song.artists.joinToString { it.name })
            })
        }
    }.toString()

    private suspend fun removeSongs(
        database: MusicDatabase,
        currentSongs: List<PlaylistSong>,
        removeIds: JSONArray
    ) {
        val mapsById = currentSongs.associate { it.map.id to it.map }

        for (index in 0 until removeIds.length()) {
            val id = removeIds.optInt(index, -1)
            if (id != -1) mapsById[id]?.let(database::delete)
        }
    }

    private suspend fun addSongs(
        database: MusicDatabase,
        playlistId: String,
        currentSongs: List<PlaylistSong>,
        additions: JSONArray,
        onLog: suspend (String) -> Unit
    ) {
        val resolvedSongs = mutableListOf<SongItem>()
        onLog("Searching for ${additions.length()} new songs on InnerTube...")

        for (index in 0 until additions.length()) {
            val item = additions.optJSONObject(index) ?: continue
            val title = item.optString("title")
            val artist = item.optString("artist")
            if (title.isEmpty()) continue

            onLog("Searching [${index + 1}/${additions.length()}]: $title - $artist")
            val result = YouTube.search("$title $artist", SearchFilter.FILTER_SONG).getOrNull()
            val song = result?.items?.firstOrNull() as? SongItem ?: continue
            resolvedSongs.add(song)
        }

        if (resolvedSongs.isEmpty()) {
            onLog("Failed to find any of the suggested additions.")
            return
        }

        onLog("Adding ${resolvedSongs.size} songs to playlist...")
        val maxPosition = currentSongs.maxOfOrNull { it.map.position } ?: -1

        resolvedSongs.forEachIndexed { index, song ->
            database.upsert(
                SongEntity(
                    id = song.id,
                    title = song.title,
                    duration = song.duration ?: 0,
                    thumbnailUrl = song.thumbnail
                )
            )
            database.insert(
                PlaylistSongMap(
                    playlistId = playlistId,
                    songId = song.id,
                    position = maxPosition + 1 + index
                )
            )
        }
    }
}
