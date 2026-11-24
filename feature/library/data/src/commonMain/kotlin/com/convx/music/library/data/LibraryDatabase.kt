package com.convx.music.library.data

import androidx.room.ConstructedBy
import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Relation
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.room.Transaction
import androidx.room.Update
import androidx.room.Upsert
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.convx.music.library.PlaylistSyncTrack
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json
import org.koin.core.module.Module
import org.koin.dsl.module
import com.convx.music.library.PlaylistRepository
import com.convx.music.library.MusicLibraryWriter
import kotlin.time.Clock

@Entity(tableName = "music_library_playlist")
data class MusicLibraryPlaylistRecord(
    @PrimaryKey val id: String,
    val name: String,
    val browseId: String?,
    val createdAtEpochMillis: Long,
    val lastUpdatedAtEpochMillis: Long,
    val isEditable: Boolean,
    val bookmarkedAtEpochMillis: Long?,
    val remoteSongCount: Int?,
    val playEndpointParams: String?,
    val thumbnailUrl: String?,
    val artworkUrlsJson: String,
    val description: String?,
    val shuffleEndpointParams: String?,
    val radioEndpointParams: String?,
    val isLocal: Boolean,
    val isAutoSync: Boolean,
    val pinnedAtEpochMillis: Long?,
)

@Entity(tableName = "music_library_track")
data class MusicLibraryTrackRecord(
    @PrimaryKey val id: String,
    val title: String,
    val artistNamesJson: String,
    val albumTitle: String?,
    val durationMillis: Long?,
    val thumbnailUrl: String?,
    val totalPlayTimeMillis: Long,
    val isExplicit: Boolean,
    val isVideo: Boolean,
    val isPodcastEpisode: Boolean,
    val musicVideoType: String?,
    val libraryAddToken: String?,
    val libraryRemoveToken: String?,
)

@Entity(
    tableName = "music_library_playlist_entry",
    foreignKeys = [
        ForeignKey(
            entity = MusicLibraryPlaylistRecord::class,
            parentColumns = ["id"],
            childColumns = ["playlistId"],
            onDelete = ForeignKey.CASCADE,
        ),
        ForeignKey(
            entity = MusicLibraryTrackRecord::class,
            parentColumns = ["id"],
            childColumns = ["songId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("playlistId"), Index("songId"), Index(value = ["playlistId", "position"])],
)
data class MusicLibraryPlaylistEntryRecord(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val playlistId: String,
    val songId: String,
    val position: Int,
    val setVideoId: String?,
)

data class MusicLibraryPlaylistWithCount(
    @Embedded val playlist: MusicLibraryPlaylistRecord,
    @ColumnInfo(name = "trackCount") val trackCount: Int,
)

data class MusicLibraryEntryWithTrack(
    @Embedded val entry: MusicLibraryPlaylistEntryRecord,
    @Relation(parentColumn = "songId", entityColumn = "id")
    val track: MusicLibraryTrackRecord,
)

@Dao
interface MusicLibraryDao {
    @Query(
        """
        SELECT playlist.*, COUNT(entry.id) AS trackCount
        FROM music_library_playlist AS playlist
        LEFT JOIN music_library_playlist_entry AS entry ON entry.playlistId = playlist.id
        GROUP BY playlist.id
        """,
    )
    fun observePlaylists(): Flow<List<MusicLibraryPlaylistWithCount>>

    @Query(
        """
        SELECT playlist.*, COUNT(entry.id) AS trackCount
        FROM music_library_playlist AS playlist
        LEFT JOIN music_library_playlist_entry AS entry ON entry.playlistId = playlist.id
        WHERE playlist.id = :playlistId
        GROUP BY playlist.id
        LIMIT 1
        """,
    )
    fun observePlaylist(playlistId: String): Flow<MusicLibraryPlaylistWithCount?>

    @Query("SELECT * FROM music_library_playlist WHERE id = :playlistId LIMIT 1")
    suspend fun getPlaylist(playlistId: String): MusicLibraryPlaylistRecord?

    @Upsert
    suspend fun upsertPlaylist(playlist: MusicLibraryPlaylistRecord)

    @Update
    suspend fun updatePlaylist(playlist: MusicLibraryPlaylistRecord)

    @Query("DELETE FROM music_library_playlist WHERE id = :playlistId")
    suspend fun deletePlaylist(playlistId: String)

    @Transaction
    @Query(
        "SELECT * FROM music_library_playlist_entry WHERE playlistId = :playlistId ORDER BY position, id",
    )
    fun observeEntriesWithTracks(playlistId: String): Flow<List<MusicLibraryEntryWithTrack>>

    @Transaction
    @Query(
        "SELECT * FROM music_library_playlist_entry WHERE playlistId = :playlistId ORDER BY position, id",
    )
    suspend fun getEntriesWithTracks(playlistId: String): List<MusicLibraryEntryWithTrack>

    @Query("SELECT songId FROM music_library_playlist_entry WHERE playlistId = :playlistId ORDER BY position, id")
    suspend fun getSongIds(playlistId: String): List<String>

    @Query(
        "SELECT DISTINCT songId FROM music_library_playlist_entry WHERE playlistId = :playlistId AND songId IN (:songIds)",
    )
    suspend fun getDuplicateSongIds(playlistId: String, songIds: List<String>): List<String>

    @Query("SELECT * FROM music_library_track WHERE id IN (:songIds)")
    suspend fun getTracks(songIds: List<String>): List<MusicLibraryTrackRecord>

    @Query("SELECT * FROM music_library_playlist_entry WHERE playlistId = :playlistId ORDER BY position, id")
    suspend fun getEntries(playlistId: String): List<MusicLibraryPlaylistEntryRecord>

    @Query("SELECT * FROM music_library_playlist_entry WHERE id = :entryId AND playlistId = :playlistId LIMIT 1")
    suspend fun getEntry(playlistId: String, entryId: Long): MusicLibraryPlaylistEntryRecord?

    @Upsert
    suspend fun insertTrack(track: MusicLibraryTrackRecord)

    @Upsert
    suspend fun insertTracks(tracks: List<MusicLibraryTrackRecord>)

    @Insert
    suspend fun insertEntry(entry: MusicLibraryPlaylistEntryRecord): Long

    @Delete
    suspend fun deleteEntry(entry: MusicLibraryPlaylistEntryRecord)

    @Query("DELETE FROM music_library_playlist_entry WHERE playlistId = :playlistId")
    suspend fun clearEntries(playlistId: String)

    @Query("UPDATE music_library_playlist_entry SET position = :position WHERE id = :entryId")
    suspend fun updateEntryPosition(entryId: Long, position: Int)

    @Transaction
    suspend fun togglePlaylistPin(playlistId: String, nowEpochMillis: Long): Boolean {
        val playlist = getPlaylist(playlistId) ?: return false
        updatePlaylist(
            playlist.copy(
                pinnedAtEpochMillis = if (playlist.pinnedAtEpochMillis == null) nowEpochMillis else null,
            ),
        )
        return true
    }

    @Transaction
    suspend fun togglePlaylistBookmark(playlistId: String, nowEpochMillis: Long): Boolean? {
        val playlist = getPlaylist(playlistId) ?: return null
        val isBookmarked = playlist.bookmarkedAtEpochMillis == null
        updatePlaylist(playlist.copy(bookmarkedAtEpochMillis = if (isBookmarked) nowEpochMillis else null))
        return isBookmarked
    }

    @Transaction
    suspend fun normalizeEntryPositions(playlistId: String) {
        getEntries(playlistId).forEachIndexed { position, entry ->
            if (entry.position != position) updateEntryPosition(entry.id, position)
        }
    }

    @Transaction
    suspend fun moveEntry(
        playlistId: String,
        fromPosition: Int,
        toPosition: Int,
    ): List<MusicLibraryPlaylistEntryRecord> {
        val entries = getEntries(playlistId).toMutableList()
        val fromIndex = entries.indexOfFirst { it.position == fromPosition }
        if (fromIndex >= 0 && entries.isNotEmpty()) {
            val entry = entries.removeAt(fromIndex)
            entries.add(toPosition.coerceIn(0, entries.size), entry)
            entries.forEachIndexed { position, orderedEntry ->
                if (orderedEntry.position != position) updateEntryPosition(orderedEntry.id, position)
            }
        }
        return getEntries(playlistId)
    }

    @Transaction
    suspend fun removeEntryAndNormalize(
        playlistId: String,
        entryId: Long,
        songId: String,
    ): MusicLibraryPlaylistEntryRecord? {
        val entry = getEntry(playlistId, entryId) ?: return null
        if (entry.songId != songId) return null
        deleteEntry(entry)
        getEntries(playlistId).forEachIndexed { position, remaining ->
            if (remaining.position != position) updateEntryPosition(remaining.id, position)
        }
        return entry
    }

    @Transaction
    suspend fun replacePlaylistTracks(playlistId: String, tracks: List<PlaylistSyncTrack>): Boolean {
        val playlist = getPlaylist(playlistId) ?: return false
        val currentPlayTimes = if (tracks.isEmpty()) {
            emptyMap()
        } else {
            getTracks(tracks.map(PlaylistSyncTrack::id).distinct()).associate { it.id to it.totalPlayTimeMillis }
        }
        clearEntries(playlistId)
        tracks.forEachIndexed { position, track ->
            insertTrack(track.toRecord().copy(totalPlayTimeMillis = currentPlayTimes[track.id] ?: 0))
            insertEntry(
                MusicLibraryPlaylistEntryRecord(
                    playlistId = playlistId,
                    songId = track.id,
                    position = position,
                    setVideoId = track.setVideoId,
                ),
            )
        }
        val artwork = tracks.asSequence().mapNotNull(PlaylistSyncTrack::thumbnailUrl).distinct().take(4).toList()
        updatePlaylist(
            playlist.copy(
                remoteSongCount = tracks.size,
                artworkUrlsJson = artwork.encodeStoredStringList(),
                lastUpdatedAtEpochMillis = nowEpochMillis(),
            ),
        )
        return true
    }

    @Transaction
    suspend fun appendSongs(playlistId: String, songIds: List<String>): Boolean {
        if (getPlaylist(playlistId) == null) return false
        if (songIds.isEmpty()) return true
        val tracksById = getTracks(songIds.distinct()).associateBy(MusicLibraryTrackRecord::id)
        if (songIds.any { it !in tracksById }) return false
        var position = getEntries(playlistId).size
        songIds.forEach { songId ->
            insertEntry(
                MusicLibraryPlaylistEntryRecord(
                    playlistId = playlistId,
                    songId = songId,
                    position = position++,
                    setVideoId = null,
                ),
            )
        }
        return true
    }

    @Transaction
    suspend fun mergeEntries(sourcePlaylistId: String, targetPlaylistId: String): Boolean {
        val source = getPlaylist(sourcePlaylistId) ?: return false
        val target = getPlaylist(targetPlaylistId) ?: return false
        if (!source.isEditable || !target.isEditable || sourcePlaylistId == targetPlaylistId) return false
        var position = getEntries(targetPlaylistId).size
        getEntries(sourcePlaylistId).forEach { entry ->
            insertEntry(entry.copy(id = 0, playlistId = targetPlaylistId, position = position++))
        }
        deletePlaylist(sourcePlaylistId)
        return true
    }
}

@Database(
    entities = [
        MusicLibraryPlaylistRecord::class,
        MusicLibraryTrackRecord::class,
        MusicLibraryPlaylistEntryRecord::class,
    ],
    version = 1,
    exportSchema = true,
)
@ConstructedBy(MusicLibraryDatabaseConstructor::class)
abstract class MusicLibraryDatabase : RoomDatabase() {
    abstract fun musicLibraryDao(): MusicLibraryDao
}

@Suppress("NO_ACTUAL_FOR_EXPECT")
expect object MusicLibraryDatabaseConstructor : RoomDatabaseConstructor<MusicLibraryDatabase> {
    override fun initialize(): MusicLibraryDatabase
}

/** New-client storage module. The Android shipping app continues to use its schema-40 database. */
fun libraryDataModule(database: MusicLibraryDatabase): Module = module {
    single { database }
    single { RoomPlaylistRepository(get()) }
    single<PlaylistRepository> { get<RoomPlaylistRepository>() }
    single<MusicLibraryWriter> { get<RoomPlaylistRepository>() }
}

/** Applies the shared SQLite driver and migration policy to each platform's path-specific builder. */
fun buildMusicLibraryDatabase(
    builder: RoomDatabase.Builder<MusicLibraryDatabase>,
): MusicLibraryDatabase = builder
    .setDriver(BundledSQLiteDriver())
    .build()

private val stringListSerializer = ListSerializer(String.serializer())
private val databaseJson = Json

internal fun List<String>.encodeStoredStringList(): String =
    databaseJson.encodeToString(stringListSerializer, this)

internal fun String.decodeStoredStringList(): List<String> =
    databaseJson.decodeFromString(stringListSerializer, this)

internal fun nowEpochMillis(): Long = Clock.System.now().toEpochMilliseconds()
