/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convx.music.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RawQuery
import androidx.room.RewriteQueriesToDropUnusedColumns
import androidx.room.RoomWarnings
import androidx.room.Transaction
import androidx.room.Update
import androidx.room.Upsert
import androidx.sqlite.db.SupportSQLiteQuery
import com.music.innertube.models.PlaylistItem
import com.music.innertube.models.SongItem
import com.music.innertube.pages.AlbumPage
import com.music.innertube.pages.ArtistPage
import com.convx.music.constants.AlbumSortType
import com.convx.music.constants.ArtistSongSortType
import com.convx.music.constants.ArtistSortType
import com.convx.music.constants.PlaylistSortType
import com.convx.music.constants.SongSortType
import com.convx.music.db.entities.Album
import com.convx.music.db.entities.AlbumArtistMap
import com.convx.music.db.entities.AlbumEntity
import com.convx.music.db.entities.AlbumWithSongs
import com.convx.music.db.entities.Artist
import com.convx.music.db.entities.ArtistEntity
import com.convx.music.db.entities.Event
import com.convx.music.db.entities.EventWithSong
import com.convx.music.db.entities.FormatEntity
import com.convx.music.db.entities.LyricsEntity
import com.convx.music.db.entities.PlayCountEntity
import com.convx.music.db.entities.Playlist
import com.convx.music.db.entities.PlaylistEntity
import com.convx.music.db.entities.PlaylistSong
import com.convx.music.db.entities.PlaylistSongMap
import com.convx.music.db.entities.RecognitionHistory
import com.convx.music.db.entities.RelatedSongMap
import com.convx.music.db.entities.SearchHistory
import com.convx.music.db.entities.SetVideoIdEntity
import com.convx.music.db.entities.Song
import com.convx.music.db.entities.SongAlbumMap
import com.convx.music.db.entities.SongAnalysisEntity
import com.convx.music.db.entities.SongArtistMap
import com.convx.music.db.entities.SongEntity
import com.convx.music.db.entities.SongWithStats
import com.convx.music.extensions.reversed
import com.convx.music.extensions.toSQLiteQuery
import com.convx.music.models.MediaMetadata
import com.convx.music.models.toMediaMetadata
import com.convx.music.ui.utils.resize
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import java.text.Collator
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.Locale
import java.util.concurrent.ConcurrentHashMap

// Cache artist IDs across imports to avoid repeating the same database lookup for each song.
private val artistIdCache = ConcurrentHashMap<String, String>()

private fun DatabaseDao.resolveArtistId(name: String): String? =
    artistIdCache[name] ?: artistByName(name)?.id?.also { artistIdCache[name] = it }

/** Drop stale name->id entries (rename/delete). Call after artist mutations. */
fun invalidateArtistCache(name: String? = null, id: String? = null) {
    if (name != null) artistIdCache.remove(name)
    else if (id != null) artistIdCache.entries.removeIf { it.value == id }
    else artistIdCache.clear()
}

/** Sorts a loaded relation graph by artist; callers run this CPU work on [Dispatchers.Default]. */
private fun sortSongsByArtistGraph(songs: List<Song>): List<Song> {
    val collator = Collator.getInstance(Locale.getDefault())
    collator.strength = Collator.PRIMARY
    return songs
        .sortedWith(
            compareBy(collator) { song ->
                song.artists.joinToString("") { it.name }
            },
        ).groupBy { it.album?.title }
        .flatMap { (_, songsByAlbum) ->
            songsByAlbum.sortedBy { album ->
                album.artists.joinToString("") { it.name }
            }
        }
}

/** Sorts an album relation graph by artist on the caller's dispatcher. */
private fun sortAlbumsByArtistGraph(albums: List<Album>): List<Album> {
    val collator = Collator.getInstance(Locale.getDefault())
    collator.strength = Collator.PRIMARY
    return albums.sortedWith(compareBy(collator) { album -> album.artists.joinToString("") { it.name } })
}

@Dao
interface DatabaseDao {
    @Transaction
    @Query("SELECT * FROM song WHERE inLibrary IS NOT NULL ORDER BY rowId")
    fun songsByRowIdAsc(): Flow<List<Song>>

    @Transaction
    @Query("SELECT * FROM song WHERE inLibrary IS NOT NULL ORDER BY inLibrary")
    fun songsByCreateDateAsc(): Flow<List<Song>>

    @Transaction
    @Query("SELECT * FROM song WHERE inLibrary IS NOT NULL ORDER BY title COLLATE NOCASE")
    fun songsByNameAsc(): Flow<List<Song>>

    @Transaction
    @Query("SELECT * FROM song WHERE inLibrary IS NOT NULL ORDER BY totalPlayTime")
    fun songsByPlayTimeAsc(): Flow<List<Song>>


    fun songs(
        sortType: SongSortType,
        descending: Boolean,
    ) = when (sortType) {
        SongSortType.CREATE_DATE -> songsByCreateDateAsc()
        SongSortType.NAME -> songsByNameAsc()

        SongSortType.ARTIST ->
            songsByRowIdAsc().map(::sortSongsByArtistGraph)
                .flowOn(Dispatchers.Default)

        SongSortType.PLAY_TIME -> songsByPlayTimeAsc()
    }.map { it.reversed(descending) }

    @Transaction
    @Query("SELECT * FROM song WHERE liked ORDER BY rowId")
    fun likedSongsByRowIdAsc(): Flow<List<Song>>

    @Transaction
    @Query("SELECT * FROM song WHERE liked ORDER BY likedDate")
    fun likedSongsByCreateDateAsc(): Flow<List<Song>>

    @Transaction
    @Query("SELECT * FROM song WHERE liked ORDER BY title COLLATE NOCASE")
    fun likedSongsByNameAsc(): Flow<List<Song>>

    @Transaction
    @Query("SELECT * FROM song WHERE liked ORDER BY totalPlayTime")
    fun likedSongsByPlayTimeAsc(): Flow<List<Song>>

    /** Ids only — [DownloadUtil] watches this to auto-download newly liked songs. */
    @Query("SELECT id FROM song WHERE liked")
    fun likedSongIds(): Flow<List<String>>

    @Query("SELECT title FROM song WHERE id = :songId")
    suspend fun songTitle(songId: String): String?

    fun likedSongs(
        sortType: SongSortType,
        descending: Boolean,
    ) = when (sortType) {
        SongSortType.CREATE_DATE -> likedSongsByCreateDateAsc()
        SongSortType.NAME -> likedSongsByNameAsc()

        SongSortType.ARTIST ->
            likedSongsByRowIdAsc().map(::sortSongsByArtistGraph)
                .flowOn(Dispatchers.Default)

        SongSortType.PLAY_TIME -> likedSongsByPlayTimeAsc()
    }.map { it.reversed(descending) }

    @Transaction
    @Query("SELECT COUNT(1) FROM song WHERE liked")
    fun likedSongsCount(): Flow<Int>

    @Transaction
    @Query("SELECT song.* FROM song JOIN song_album_map ON song.id = song_album_map.songId WHERE song_album_map.albumId = :albumId")
    fun albumSongs(albumId: String): Flow<List<Song>>

    @Transaction
    @Query("SELECT * FROM playlist_song_map WHERE playlistId = :playlistId ORDER BY position")
    fun playlistSongs(playlistId: String): Flow<List<PlaylistSong>>

    @Transaction
    @Query(
        "SELECT song.* FROM song_artist_map JOIN song ON song_artist_map.songId = song.id WHERE artistId = :artistId AND inLibrary IS NOT NULL ORDER BY inLibrary",
    )
    fun artistSongsByCreateDateAsc(artistId: String): Flow<List<Song>>

    @Transaction
    @Query(
        "SELECT song.* FROM song_artist_map JOIN song ON song_artist_map.songId = song.id WHERE artistId = :artistId AND inLibrary IS NOT NULL ORDER BY title COLLATE NOCASE",
    )
    fun artistSongsByNameAsc(artistId: String): Flow<List<Song>>

    @Transaction
    @Query(
        "SELECT song.* FROM song_artist_map JOIN song ON song_artist_map.songId = song.id WHERE artistId = :artistId AND inLibrary IS NOT NULL ORDER BY totalPlayTime",
    )
    fun artistSongsByPlayTimeAsc(artistId: String): Flow<List<Song>>

    fun artistSongs(
        artistId: String,
        sortType: ArtistSongSortType,
        descending: Boolean,
        fromTimeStamp: Long? = null,
        toTimeStamp: Long? = null,
        limit: Int = -1
    ): Flow<List<Song>> {
        val songsFlow = when (sortType) {
            ArtistSongSortType.CREATE_DATE -> artistSongsByCreateDateAsc(artistId)
            ArtistSongSortType.NAME -> artistSongsByNameAsc(artistId)

            ArtistSongSortType.PLAY_TIME -> {
                if (fromTimeStamp != null && toTimeStamp != null) {
                    mostPlayedSongsByArtist(artistId, fromTimeStamp, toTimeStamp)
                } else {
                    artistSongsByPlayTimeAsc(artistId)
                }
            }
        }

        return songsFlow.map { songs ->
            val limitedSongs = if (limit > 0) songs.take(limit) else songs
            limitedSongs.reversed(descending)
        }
    }

    @Transaction
    @RewriteQueriesToDropUnusedColumns
    @Query(
        """
        SELECT s.*
        FROM song s
        JOIN (
            SELECT e.songId, SUM(e.playTime) as totalPlayTime
            FROM event e
            JOIN song_artist_map sam ON e.songId = sam.songId
            WHERE sam.artistId = :artistId AND e.timestamp >= :fromTimeStamp AND e.timestamp <= :toTimeStamp
            GROUP BY e.songId
        ) AS play_times ON s.id = play_times.songId
        ORDER BY play_times.totalPlayTime DESC
        """
    )
    fun mostPlayedSongsByArtist(artistId: String, fromTimeStamp: Long, toTimeStamp: Long): Flow<List<Song>>

    @Transaction
    @Query(
        "SELECT song.* FROM song_artist_map JOIN song ON song_artist_map.songId = song.id WHERE artistId = :artistId AND inLibrary IS NOT NULL LIMIT :previewSize",
    )
    fun artistSongsPreview(
        artistId: String,
        previewSize: Int = 3,
    ): Flow<List<Song>>

    @Transaction
    @Query(
        """
        SELECT song.*
        FROM (SELECT *, COUNT(1) AS referredCount
              FROM related_song_map
              GROUP BY relatedSongId) map
                 JOIN song ON song.id = map.relatedSongId
        WHERE songId IN (SELECT songId
                         FROM (SELECT songId
                               FROM event
                               ORDER BY ROWID DESC
                               LIMIT 5)
                         UNION
                         SELECT songId
                         FROM (SELECT songId
                               FROM event
                               WHERE timestamp > :now - 86400000 * 7
                               GROUP BY songId
                               ORDER BY SUM(playTime) DESC
                               LIMIT 5)
                         UNION
                         SELECT id
                         FROM (SELECT id
                               FROM song
                               ORDER BY totalPlayTime DESC
                               LIMIT 10))
        ORDER BY referredCount DESC
        LIMIT 100
    """,
    )
    fun quickPicks(now: Long = System.currentTimeMillis()): Flow<List<Song>>

    @Transaction
    @Query(
        """
        SELECT
            song.*
        FROM
            event
        JOIN
            song ON event.songId = song.id
        WHERE
            event.timestamp > (:now - 86400000 * 7 * 2)
        GROUP BY
            song.albumId
        HAVING
            song.albumId IS NOT NULL
        ORDER BY
            sum(event.playTime) DESC
        LIMIT :limit
        OFFSET :offset

        """,
    )
    fun getRecommendationAlbum(
        now: Long = System.currentTimeMillis(),
        limit: Int = 5,
        offset: Int = 0,
    ): Flow<List<Song>>

    @Transaction
    @Query(
        """
        SELECT s.id, s.title, s.thumbnailUrl, s.isVideo,
               (SELECT name FROM artist WHERE id = sam.artistId) as artistName,
               (SELECT COUNT(1)
                FROM event
                WHERE songId = s.id
                  AND timestamp > :fromTimeStamp AND timestamp <= :toTimeStamp) AS songCountListened,
               (SELECT SUM(event.playTime)
                FROM event
                WHERE songId = s.id
                  AND timestamp > :fromTimeStamp AND timestamp <= :toTimeStamp) AS timeListened
        FROM song s
        LEFT JOIN song_artist_map sam ON s.id = sam.songId
        JOIN (SELECT songId
              FROM event
              WHERE timestamp > :fromTimeStamp
                AND timestamp <= :toTimeStamp
              GROUP BY songId
              ORDER BY SUM(playTime) DESC
              LIMIT :limit) AS top_songs ON s.id = top_songs.songId
        GROUP BY s.id
        ORDER BY timeListened DESC
        LIMIT :limit OFFSET :offset
        """,
    )
    fun mostPlayedSongsStats(
        fromTimeStamp: Long,
        limit: Int = 6,
        offset: Int = 0,
        toTimeStamp: Long? = LocalDateTime.now().toInstant(ZoneOffset.UTC).toEpochMilli(),
    ): Flow<List<SongWithStats>>

    @Transaction
    @RewriteQueriesToDropUnusedColumns
    @Query(
        """
        SELECT song.*,
               (SELECT COUNT(1)
                FROM event
                WHERE songId = song.id
                  AND timestamp > :fromTimeStamp AND timestamp <= :toTimeStamp) AS songCountListened,
               (SELECT SUM(event.playTime)
                FROM event
                WHERE songId = song.id
                  AND timestamp > :fromTimeStamp AND timestamp <= :toTimeStamp) AS timeListened
        FROM song
        JOIN (SELECT songId
                     FROM event
                     WHERE timestamp > :fromTimeStamp
                     AND timestamp <= :toTimeStamp
                     GROUP BY songId
                     ORDER BY SUM(playTime) DESC
                     LIMIT :limit)
        ON song.id = songId
        LIMIT :limit
        OFFSET :offset
    """,
    )
    fun mostPlayedSongs(
        fromTimeStamp: Long,
        limit: Int = 6,
        offset: Int = 0,
        toTimeStamp: Long? = LocalDateTime.now().toInstant(ZoneOffset.UTC).toEpochMilli(),
    ): Flow<List<Song>>

    @Transaction
    @SuppressWarnings(RoomWarnings.QUERY_MISMATCH)
    @Query(
        """
        SELECT artist.*,
               (SELECT COUNT(1)
                FROM song_artist_map
                         JOIN event ON song_artist_map.songId = event.songId
                WHERE artistId = artist.id
                  AND timestamp > :fromTimeStamp AND timestamp <= :toTimeStamp) AS songCount,
               (SELECT SUM(event.playTime)
                FROM song_artist_map
                         JOIN event ON song_artist_map.songId = event.songId
                WHERE artistId = artist.id
                  AND timestamp > :fromTimeStamp AND timestamp <= :toTimeStamp) AS timeListened
        FROM artist
                 JOIN(SELECT artistId, SUM(songTotalPlayTime) AS totalPlayTime
                      FROM song_artist_map
                               JOIN (SELECT songId, SUM(playTime) AS songTotalPlayTime
                                     FROM event
                                     WHERE timestamp > :fromTimeStamp
                                     AND timestamp <= :toTimeStamp
                                     GROUP BY songId) AS e
                                    ON song_artist_map.songId = e.songId
                      GROUP BY artistId
                      ORDER BY totalPlayTime DESC
                      LIMIT :limit
                      OFFSET :offset)
                     ON artist.id = artistId
    """,
    )
    fun mostPlayedArtists(
        fromTimeStamp: Long,
        limit: Int = 6,
        offset: Int = 0,
        toTimeStamp: Long? = LocalDateTime.now().toInstant(ZoneOffset.UTC).toEpochMilli(),
    ): Flow<List<Artist>>

    @Transaction
    @SuppressWarnings(RoomWarnings.QUERY_MISMATCH)
    @Query(
        """
    SELECT album.*,
           COUNT(DISTINCT song_album_map.songId) as downloadCount,
           (SELECT COUNT(1)
            FROM song_album_map
                     JOIN event e ON song_album_map.songId = e.songId
            WHERE albumId = album.id
              AND e.timestamp > :fromTimeStamp
              AND e.timestamp <= :toTimeStamp) AS songCountListened,
           (SELECT SUM(e.playTime)
            FROM song_album_map
                     JOIN event e ON song_album_map.songId = e.songId
            WHERE albumId = album.id
              AND e.timestamp > :fromTimeStamp
              AND e.timestamp <= :toTimeStamp) AS timeListened
    FROM album
    JOIN song_album_map ON album.id = song_album_map.albumId
    WHERE album.id IN (
        SELECT sam.albumId
        FROM event
                 JOIN song_album_map sam ON event.songId = sam.songId
        WHERE event.timestamp > :fromTimeStamp
          AND event.timestamp <= :toTimeStamp
        GROUP BY sam.albumId
        HAVING sam.albumId IS NOT NULL
    )
    GROUP BY album.id
    ORDER BY timeListened DESC
    LIMIT :limit OFFSET :offset
    """
    )
    fun mostPlayedAlbums(
        fromTimeStamp: Long,
        limit: Int = 6,
        offset: Int = 0,
        toTimeStamp: Long? = LocalDateTime.now().toInstant(ZoneOffset.UTC).toEpochMilli(),
    ): Flow<List<Album>>

    @Query("SELECT SUM(playTime) FROM event WHERE timestamp >= :fromTimeStamp AND timestamp <= :toTimeStamp")
    fun getTotalPlayTimeInRange(fromTimeStamp: Long, toTimeStamp: Long): Flow<Long?>

    @Query("SELECT COUNT(DISTINCT songId) FROM event WHERE timestamp >= :fromTimeStamp AND timestamp <= :toTimeStamp")
    fun getUniqueSongCountInRange(fromTimeStamp: Long, toTimeStamp: Long): Flow<Int>

    @Query(
        """
        SELECT COUNT(DISTINCT artistId)
        FROM event
        JOIN song_artist_map ON event.songId = song_artist_map.songId
        WHERE timestamp >= :fromTimeStamp AND timestamp <= :toTimeStamp
    """
    )
    fun getUniqueArtistCountInRange(fromTimeStamp: Long, toTimeStamp: Long): Flow<Int>

    @Query(
        """
        SELECT COUNT(DISTINCT albumId)
        FROM event
        JOIN song ON event.songId = song.id
        WHERE timestamp >= :fromTimeStamp AND timestamp <= :toTimeStamp
    """
    )
    fun getUniqueAlbumCountInRange(fromTimeStamp: Long, toTimeStamp: Long): Flow<Int>

    @Transaction
    @SuppressWarnings(RoomWarnings.QUERY_MISMATCH)
    @Query(
        """
        SELECT album.*, count(song.dateDownload) downloadCount
        FROM album_artist_map
            JOIN album ON album_artist_map.albumId = album.id
            JOIN song ON album_artist_map.albumId = song.albumId
        WHERE artistId = :artistId
        GROUP BY album.id
        LIMIT :previewSize
    """
    )
    fun artistAlbumsPreview(artistId: String, previewSize: Int = 6): Flow<List<Album>>

    @Query("SELECT sum(count) from playCount WHERE song = :songId")
    fun getLifetimePlayCount(songId: String?): Flow<Int>

    @Query("SELECT sum(count) from playCount WHERE song = :songId AND year = :year")
    fun getPlayCountByYear(songId: String?, year: Int): Flow<Int>

    @Query("SELECT count from playCount WHERE song = :songId AND year = :year AND month = :month")
    fun getPlayCountByMonth(songId: String?, year: Int, month: Int): Flow<Int>

    @Transaction
    @Query(
        """
        SELECT song.*
        FROM (SELECT n.songId      AS eid,
                     SUM(playTime) AS oldPlayTime,
                     newPlayTime
              FROM event
                       JOIN
                   (SELECT songId, SUM(playTime) AS newPlayTime
                    FROM event
                    WHERE timestamp > (:now - 86400000 * 30 * 1)
                    GROUP BY songId
                    ORDER BY newPlayTime) as n
                   ON event.songId = n.songId
              WHERE timestamp < (:now - 86400000 * 30 * 1)
              GROUP BY n.songId
              ORDER BY oldPlayTime) AS t
                 JOIN song on song.id = t.eid
        WHERE 0.2 * t.oldPlayTime > t.newPlayTime
        LIMIT 100
    """
    )
    fun forgottenFavorites(now: Long = System.currentTimeMillis()): Flow<List<Song>>

    @Transaction
    @Query(
        """
        SELECT song.*
        FROM event
                 JOIN
             song ON event.songId = song.id
        WHERE event.timestamp > (:now - 86400000 * 7 * 2)
        GROUP BY song.albumId
        HAVING song.albumId IS NOT NULL
        ORDER BY sum(event.playTime) DESC
        LIMIT :limit
        OFFSET :offset
        """,
    )
    fun recommendedAlbum(
        now: Long = System.currentTimeMillis(),
        limit: Int = 5,
        offset: Int = 0,
    ): Flow<List<Song>>

    @Transaction
    @Query("SELECT * FROM song WHERE id = :songId")
    fun song(songId: String?): Flow<Song?>

    @Transaction
    @Query("SELECT * FROM song WHERE id = :songId LIMIT 1")
    suspend fun getSongById(songId: String): Song?

    @Transaction
    @Query("SELECT * FROM song WHERE id = :songId LIMIT 1")
    fun getSongByIdBlocking(songId: String): Song?

    @Transaction
    @Query("SELECT * FROM song WHERE id IN (:songIds)")
    suspend fun getSongsByIds(songIds: List<String>): List<Song>

    @Transaction
    @Query("SELECT * FROM song WHERE id IN (:songIds)")
    fun getSongsByIdsBlocking(songIds: List<String>): List<Song>

    @Transaction
    @Query("SELECT * FROM song WHERE id IN (:songIds)")
    fun songsByIds(songIds: List<String>): Flow<List<Song>>


    @Transaction
    @Query("SELECT * FROM song_artist_map WHERE songId = :songId")
    fun songArtistMap(songId: String): List<SongArtistMap>

    @Transaction
    @Query("SELECT * FROM song")
    fun allSongs(): Flow<List<Song>>

    @Transaction
    @SuppressWarnings(RoomWarnings.QUERY_MISMATCH)
    @Query(
        """
        SELECT DISTINCT artist.*,
               (SELECT COUNT(1)
                FROM song_artist_map
                         JOIN event ON song_artist_map.songId = event.songId
                WHERE artistId = artist.id) AS songCount
        FROM artist
                 LEFT JOIN(SELECT artistId, SUM(songTotalPlayTime) AS totalPlayTime
                      FROM song_artist_map
                               JOIN (SELECT songId, SUM(playTime) AS songTotalPlayTime
                                     FROM event
                                     GROUP BY songId) AS e
                                    ON song_artist_map.songId = e.songId
                      GROUP BY artistId
                      ORDER BY totalPlayTime DESC) AS artistTotalPlayTime
                     ON artist.id = artistId
                     OR artist.bookmarkedAt IS NOT NULL
                     ORDER BY
                      CASE
                        WHEN artistTotalPlayTime.artistId IS NULL THEN 1
                        ELSE 0
                      END,
                      artistTotalPlayTime.totalPlayTime DESC
    """,
    )
    fun allArtistsByPlayTime(): Flow<List<Artist>>

    @Query("SELECT * FROM set_video_id WHERE videoId = :videoId")
    suspend fun getSetVideoId(videoId: String): SetVideoIdEntity?

    @Transaction
    @Query("SELECT * FROM format WHERE id = :id")
    fun format(id: String?): Flow<FormatEntity?>

    /** Cached DJ analysis, filtered to the current algorithm version — a grid
     *  from an older detector is re-analyzed rather than trusted. Callers pass
     *  `SongAnalysisEntity.CURRENT_ANALYSIS_VERSION`; Room does not reliably
     *  honour Kotlin default arguments on `@Query` methods. */
    @Query("SELECT * FROM song_analysis WHERE id = :id AND analysisVersion = :version")
    suspend fun songAnalysis(id: String, version: Int): SongAnalysisEntity?

    @Query("SELECT COALESCE(SUM(duration), 0) FROM song WHERE (isLocal = 1 OR isDownloaded = 1) AND duration > 0")
    suspend fun localDurationTotal(): Long

    /** Device and downloaded songs as (id, duration in seconds), for the background analysis pass. */
    @Query("SELECT id, duration, title FROM song WHERE isLocal = 1 OR isDownloaded = 1")
    suspend fun localSongDurations(): List<LocalSongDuration>

    @Query("SELECT * FROM song_analysis WHERE id IN (:ids) AND analysisVersion = :version")
    suspend fun songAnalyses(ids: List<String>, version: Int): List<SongAnalysisEntity>

    @Transaction
    @Query("SELECT * FROM lyrics WHERE id = :id")
    fun lyrics(id: String?): Flow<LyricsEntity?>

    @Transaction
    @SuppressWarnings(RoomWarnings.QUERY_MISMATCH)
    @Query("SELECT *, (SELECT COUNT(1) FROM song_artist_map JOIN song ON song_artist_map.songId = song.id WHERE artistId = artist.id AND song.inLibrary IS NOT NULL) AS songCount FROM artist WHERE songCount > 0 ORDER BY rowId")
    fun artistsByCreateDateAsc(): Flow<List<Artist>>

    @Transaction
    @SuppressWarnings(RoomWarnings.QUERY_MISMATCH)
    @Query("SELECT *, (SELECT COUNT(1) FROM song_artist_map JOIN song ON song_artist_map.songId = song.id WHERE artistId = artist.id AND song.inLibrary IS NOT NULL) AS songCount FROM artist WHERE songCount > 0 ORDER BY name")
    fun artistsByNameAsc(): Flow<List<Artist>>

    @Transaction
    @SuppressWarnings(RoomWarnings.QUERY_MISMATCH)
    @Query("SELECT *, (SELECT COUNT(1) FROM song_artist_map JOIN song ON song_artist_map.songId = song.id WHERE artistId = artist.id AND song.inLibrary IS NOT NULL) AS songCount FROM artist WHERE songCount > 0 ORDER BY songCount")
    fun artistsBySongCountAsc(): Flow<List<Artist>>

    @Transaction
    @SuppressWarnings(RoomWarnings.QUERY_MISMATCH)
    @Query(
        """
        SELECT artist.*,
               (SELECT COUNT(1)
                FROM song_artist_map
                         JOIN song ON song_artist_map.songId = song.id
                WHERE artistId = artist.id
                  AND song.inLibrary IS NOT NULL) AS songCount
        FROM artist
                 JOIN(SELECT artistId, SUM(totalPlayTime) AS totalPlayTime
                      FROM song_artist_map
                               JOIN song
                                    ON song_artist_map.songId = song.id
                      GROUP BY artistId
                      ORDER BY totalPlayTime)
                     ON artist.id = artistId
        WHERE songCount > 0
    """
    )
    fun artistsByPlayTimeAsc(): Flow<List<Artist>>

    @Transaction
    @SuppressWarnings(RoomWarnings.QUERY_MISMATCH)
    @Query("SELECT *, (SELECT COUNT(1) FROM song_artist_map JOIN song ON song_artist_map.songId = song.id WHERE artistId = artist.id AND song.inLibrary IS NOT NULL) AS songCount FROM artist WHERE bookmarkedAt IS NOT NULL ORDER BY bookmarkedAt")
    fun artistsBookmarkedByCreateDateAsc(): Flow<List<Artist>>

    @Transaction
    @SuppressWarnings(RoomWarnings.QUERY_MISMATCH)
    @Query("SELECT *, (SELECT COUNT(1) FROM song_artist_map JOIN song ON song_artist_map.songId = song.id WHERE artistId = artist.id AND song.inLibrary IS NOT NULL) AS songCount FROM artist WHERE bookmarkedAt IS NOT NULL ORDER BY name")
    fun artistsBookmarkedByNameAsc(): Flow<List<Artist>>

    @Transaction
    @SuppressWarnings(RoomWarnings.QUERY_MISMATCH)
    @Query("SELECT *, (SELECT COUNT(1) FROM song_artist_map JOIN song ON song_artist_map.songId = song.id WHERE artistId = artist.id AND song.inLibrary IS NOT NULL) AS songCount FROM artist WHERE bookmarkedAt IS NOT NULL ORDER BY songCount")
    fun artistsBookmarkedBySongCountAsc(): Flow<List<Artist>>

    @Transaction
    @SuppressWarnings(RoomWarnings.QUERY_MISMATCH)
    @Query(
        """
        SELECT artist.*,
               (SELECT COUNT(1)
                FROM song_artist_map
                         JOIN song ON song_artist_map.songId = song.id
                WHERE artistId = artist.id
                  AND song.inLibrary IS NOT NULL) AS songCount
        FROM artist
                 JOIN(SELECT artistId, SUM(totalPlayTime) AS totalPlayTime
                      FROM song_artist_map
                               JOIN song
                                    ON song_artist_map.songId = song.id
                      GROUP BY artistId
                      ORDER BY totalPlayTime)
                     ON artist.id = artistId
        WHERE bookmarkedAt IS NOT NULL
    """
    )
    fun artistsBookmarkedByPlayTimeAsc(): Flow<List<Artist>>

    fun artists(sortType: ArtistSortType, descending: Boolean) =
        when (sortType) {
            ArtistSortType.CREATE_DATE -> artistsByCreateDateAsc()
            ArtistSortType.NAME -> artistsByNameAsc()
            ArtistSortType.SONG_COUNT -> artistsBySongCountAsc()
            ArtistSortType.PLAY_TIME -> artistsByPlayTimeAsc()
        }.map { artists ->
            artists
                .filter { it.artist.isYouTubeArtist || it.artist.isLocal }
                .reversed(descending)
        }

    fun artistsBookmarked(sortType: ArtistSortType, descending: Boolean) =
        when (sortType) {
            ArtistSortType.CREATE_DATE -> artistsBookmarkedByCreateDateAsc()
            ArtistSortType.NAME -> artistsBookmarkedByNameAsc()
            ArtistSortType.SONG_COUNT -> artistsBookmarkedBySongCountAsc()
            ArtistSortType.PLAY_TIME -> artistsBookmarkedByPlayTimeAsc()
        }.map { artists ->
            artists
                .filter { it.artist.isYouTubeArtist || it.artist.isLocal }
                .reversed(descending)
        }

    @Transaction
    @SuppressWarnings(RoomWarnings.QUERY_MISMATCH)
    @Query(
        """
        SELECT artist.*,
               (SELECT COUNT(1)
                FROM song_artist_map
                         JOIN song ON song_artist_map.songId = song.id
                WHERE artistId = artist.id
                  AND song.inLibrary IS NOT NULL) AS songCount
        FROM artist
                 JOIN(SELECT artistId, SUM(totalPlayTime) AS totalPlayTime
                      FROM song_artist_map
                               JOIN song
                                    ON song_artist_map.songId = song.id
                      GROUP BY artistId
                      ORDER BY totalPlayTime)
                     ON artist.id = artistId
        WHERE artist.isLocal = 1
        ORDER BY artist.name
    """
    )
    fun artistsLocalByNameAsc(): Flow<List<Artist>>

    /** Uses row id as insertion order because local artists have no creation timestamp. */
    @Transaction
    @SuppressWarnings(RoomWarnings.QUERY_MISMATCH)
    @Query(
        """
        SELECT artist.*,
               (SELECT COUNT(1)
                FROM song_artist_map
                         JOIN song ON song_artist_map.songId = song.id
                WHERE artistId = artist.id
                  AND song.inLibrary IS NOT NULL) AS songCount
        FROM artist
                 JOIN(SELECT artistId, SUM(totalPlayTime) AS totalPlayTime
                      FROM song_artist_map
                               JOIN song
                                    ON song_artist_map.songId = song.id
                      GROUP BY artistId
                      ORDER BY totalPlayTime)
                     ON artist.id = artistId
        WHERE artist.isLocal = 1
        ORDER BY artist.rowId
    """
    )
    fun artistsLocalByRowIdAsc(): Flow<List<Artist>>

    @Transaction
    @SuppressWarnings(RoomWarnings.QUERY_MISMATCH)
    @Query(
        """
        SELECT artist.*,
               (SELECT COUNT(1)
                FROM song_artist_map
                         JOIN song ON song_artist_map.songId = song.id
                WHERE artistId = artist.id
                  AND song.inLibrary IS NOT NULL) AS songCount
        FROM artist
                 JOIN(SELECT artistId, SUM(totalPlayTime) AS totalPlayTime
                      FROM song_artist_map
                               JOIN song
                                    ON song_artist_map.songId = song.id
                      GROUP BY artistId
                      ORDER BY totalPlayTime)
                     ON artist.id = artistId
        WHERE isLocal = 1
        ORDER BY artist.name DESC
    """
    )
    fun artistsLocalByNameDesc(): Flow<List<Artist>>

    @Transaction
    @SuppressWarnings(RoomWarnings.QUERY_MISMATCH)
    @Query(
        """
        SELECT artist.*,
               (SELECT COUNT(1)
                FROM song_artist_map
                         JOIN song ON song_artist_map.songId = song.id
                WHERE artistId = artist.id
                  AND song.inLibrary IS NOT NULL) AS songCount
        FROM artist
                 JOIN(SELECT artistId, SUM(totalPlayTime) AS totalPlayTime
                      FROM song_artist_map
                               JOIN song
                                    ON song_artist_map.songId = song.id
                      GROUP BY artistId
                      ORDER BY totalPlayTime)
                     ON artist.id = artistId
        WHERE isLocal = 1
        ORDER BY songCount
    """
    )
    fun artistsLocalBySongCountAsc(): Flow<List<Artist>>

    @Transaction
    @SuppressWarnings(RoomWarnings.QUERY_MISMATCH)
    @Query(
        """
        SELECT artist.*,
               (SELECT COUNT(1)
                FROM song_artist_map
                         JOIN song ON song_artist_map.songId = song.id
                WHERE artistId = artist.id
                  AND song.inLibrary IS NOT NULL) AS songCount
        FROM artist
                 JOIN(SELECT artistId, SUM(totalPlayTime) AS totalPlayTime
                      FROM song_artist_map
                               JOIN song
                                    ON song_artist_map.songId = song.id
                      GROUP BY artistId
                      ORDER BY totalPlayTime)
                     ON artist.id = artistId
        WHERE isLocal = 1
        ORDER BY songCount DESC
    """
    )
    fun artistsLocalBySongCountDesc(): Flow<List<Artist>>

    @Transaction
    @SuppressWarnings(RoomWarnings.QUERY_MISMATCH)
    @Query(
        """
        SELECT artist.*,
               (SELECT COUNT(1)
                FROM song_artist_map
                         JOIN song ON song_artist_map.songId = song.id
                WHERE artistId = artist.id
                  AND song.inLibrary IS NOT NULL) AS songCount
        FROM artist
                 JOIN(SELECT artistId, SUM(totalPlayTime) AS totalPlayTime
                      FROM song_artist_map
                               JOIN song
                                    ON song_artist_map.songId = song.id
                      GROUP BY artistId
                      ORDER BY totalPlayTime)
                     ON artist.id = artistId
        WHERE isLocal = 1
        ORDER BY totalPlayTime
    """
    )
    fun artistsLocalByPlayTimeAsc(): Flow<List<Artist>>

    @Transaction
    @SuppressWarnings(RoomWarnings.QUERY_MISMATCH)
    @Query(
        """
        SELECT artist.*,
               (SELECT COUNT(1)
                FROM song_artist_map
                         JOIN song ON song_artist_map.songId = song.id
                WHERE artistId = artist.id
                  AND song.inLibrary IS NOT NULL) AS songCount
        FROM artist
                 JOIN(SELECT artistId, SUM(totalPlayTime) AS totalPlayTime
                      FROM song_artist_map
                               JOIN song
                                    ON song_artist_map.songId = song.id
                      GROUP BY artistId
                      ORDER BY totalPlayTime)
                     ON artist.id = artistId
        WHERE isLocal = 1
        ORDER BY totalPlayTime DESC
    """
    )
    fun artistsLocalByPlayTimeDesc(): Flow<List<Artist>>

    fun artistsLocal(sortType: ArtistSortType, descending: Boolean) =
        when (sortType) {
            ArtistSortType.CREATE_DATE -> artistsLocalByRowIdAsc()
            ArtistSortType.NAME -> artistsLocalByNameAsc()
            ArtistSortType.SONG_COUNT -> artistsLocalBySongCountAsc()
            ArtistSortType.PLAY_TIME -> artistsLocalByPlayTimeAsc()
        }.map { it.reversed(descending) }

    @SuppressWarnings(RoomWarnings.QUERY_MISMATCH)
    @Query("SELECT *, (SELECT COUNT(1) FROM song_artist_map JOIN song ON song_artist_map.songId = song.id WHERE artistId = artist.id AND song.inLibrary IS NOT NULL) AS songCount FROM artist WHERE id = :id")
    fun artist(id: String): Flow<Artist?>

    @Transaction
    @SuppressWarnings(RoomWarnings.QUERY_MISMATCH)
    @Query("SELECT * FROM album WHERE EXISTS(SELECT * FROM song WHERE song.albumId = album.id AND song.inLibrary IS NOT NULL) ORDER BY rowId")
    fun albumsByCreateDateAsc(): Flow<List<Album>>

    @Transaction
    @SuppressWarnings(RoomWarnings.QUERY_MISMATCH)
    @Query("SELECT * FROM album WHERE EXISTS(SELECT * FROM song WHERE song.albumId = album.id AND song.inLibrary IS NOT NULL) ORDER BY title COLLATE NOCASE")
    fun albumsByNameAsc(): Flow<List<Album>>

    @Transaction
    @SuppressWarnings(RoomWarnings.QUERY_MISMATCH)
    @Query("SELECT * FROM album WHERE EXISTS(SELECT * FROM song WHERE song.albumId = album.id AND song.inLibrary IS NOT NULL) ORDER BY year")
    fun albumsByYearAsc(): Flow<List<Album>>

    @Transaction
    @SuppressWarnings(RoomWarnings.QUERY_MISMATCH)
    @Query("SELECT * FROM album WHERE EXISTS(SELECT * FROM song WHERE song.albumId = album.id AND song.inLibrary IS NOT NULL) ORDER BY songCount")
    fun albumsBySongCountAsc(): Flow<List<Album>>

    @Transaction
    @SuppressWarnings(RoomWarnings.QUERY_MISMATCH)
    @Query("SELECT * FROM album WHERE EXISTS(SELECT * FROM song WHERE song.albumId = album.id AND song.inLibrary IS NOT NULL) ORDER BY duration")
    fun albumsByLengthAsc(): Flow<List<Album>>

    @Transaction
    @SuppressWarnings(RoomWarnings.QUERY_MISMATCH)
    @Query(
        """
        SELECT album.*
        FROM album
                 JOIN song
                      ON song.albumId = album.id
        WHERE EXISTS(SELECT * FROM song WHERE song.albumId = album.id AND song.inLibrary IS NOT NULL)
        GROUP BY album.id
        ORDER BY SUM(song.totalPlayTime)
    """,
    )
    fun albumsByPlayTimeAsc(): Flow<List<Album>>

    @Transaction
    @SuppressWarnings(RoomWarnings.QUERY_MISMATCH)
    @Query("SELECT * FROM album WHERE bookmarkedAt IS NOT NULL ORDER BY rowId")
    fun albumsLikedByCreateDateAsc(): Flow<List<Album>>

    @Transaction
    @SuppressWarnings(RoomWarnings.QUERY_MISMATCH)
    @Query("SELECT * FROM album WHERE bookmarkedAt IS NOT NULL ORDER BY title COLLATE NOCASE")
    fun albumsLikedByNameAsc(): Flow<List<Album>>

    @Transaction
    @SuppressWarnings(RoomWarnings.QUERY_MISMATCH)
    @Query("SELECT * FROM album WHERE bookmarkedAt IS NOT NULL ORDER BY year")
    fun albumsLikedByYearAsc(): Flow<List<Album>>

    @Transaction
    @SuppressWarnings(RoomWarnings.QUERY_MISMATCH)
    @Query("SELECT * FROM album WHERE bookmarkedAt IS NOT NULL ORDER BY songCount")
    fun albumsLikedBySongCountAsc(): Flow<List<Album>>

    @Transaction
    @SuppressWarnings(RoomWarnings.QUERY_MISMATCH)
    @Query("SELECT * FROM album WHERE bookmarkedAt IS NOT NULL ORDER BY duration")
    fun albumsLikedByLengthAsc(): Flow<List<Album>>

    @Transaction
    @SuppressWarnings(RoomWarnings.QUERY_MISMATCH)
    @Query(
        """
        SELECT album.*
        FROM album
                 JOIN song
                      ON song.albumId = album.id
        WHERE bookmarkedAt IS NOT NULL
        GROUP BY album.id
        ORDER BY SUM(song.totalPlayTime)
    """
    )
    fun albumsLikedByPlayTimeAsc(): Flow<List<Album>>

    @Transaction
    @SuppressWarnings(RoomWarnings.QUERY_MISMATCH)
    @Query("SELECT * FROM album WHERE isUploaded = 1 ORDER BY rowId")
    fun albumsUploadedByCreateDateAsc(): Flow<List<Album>>

    @Transaction
    @SuppressWarnings(RoomWarnings.QUERY_MISMATCH)
    @Query("SELECT * FROM album WHERE bookmarkedAt IS NOT NULL ORDER BY title COLLATE NOCASE")
    fun albumsUploadedByNameAsc(): Flow<List<Album>>

    @Transaction
    @SuppressWarnings(RoomWarnings.QUERY_MISMATCH)
    @Query("SELECT * FROM album WHERE bookmarkedAt IS NOT NULL ORDER BY year")
    fun albumsUploadedByYearAsc(): Flow<List<Album>>

    @Transaction
    @SuppressWarnings(RoomWarnings.QUERY_MISMATCH)
    @Query("SELECT * FROM album WHERE bookmarkedAt IS NOT NULL ORDER BY songCount")
    fun albumsUploadedBySongCountAsc(): Flow<List<Album>>

    @Transaction
    @SuppressWarnings(RoomWarnings.QUERY_MISMATCH)
    @Query("SELECT * FROM album WHERE bookmarkedAt IS NOT NULL ORDER BY duration")
    fun albumsUploadedByLengthAsc(): Flow<List<Album>>

    @Transaction
    @SuppressWarnings(RoomWarnings.QUERY_MISMATCH)
    @Query(
        """
        SELECT album.*
        FROM album
                 JOIN song
                      ON song.albumId = album.id
        WHERE bookmarkedAt IS NOT NULL
        GROUP BY album.id
        ORDER BY SUM(song.totalPlayTime)
    """
    )
    fun albumsUploadedByPlayTimeAsc(): Flow<List<Album>>

    fun albums(
        sortType: AlbumSortType,
        descending: Boolean,
    ) = when (sortType) {
        AlbumSortType.CREATE_DATE -> albumsByCreateDateAsc()
        AlbumSortType.NAME -> albumsByNameAsc()

        AlbumSortType.ARTIST ->
            albumsByCreateDateAsc().map(::sortAlbumsByArtistGraph)
                .flowOn(Dispatchers.Default)

        AlbumSortType.YEAR -> albumsByYearAsc()
        AlbumSortType.SONG_COUNT -> albumsBySongCountAsc()
        AlbumSortType.LENGTH -> albumsByLengthAsc()
        AlbumSortType.PLAY_TIME -> albumsByPlayTimeAsc()
    }.map { it.reversed(descending) }

    fun albumsLiked(
        sortType: AlbumSortType,
        descending: Boolean,
    ) = when (sortType) {
        AlbumSortType.CREATE_DATE -> albumsLikedByCreateDateAsc()
        AlbumSortType.NAME -> albumsLikedByNameAsc()

        AlbumSortType.ARTIST ->
            albumsLikedByCreateDateAsc().map(::sortAlbumsByArtistGraph)
                .flowOn(Dispatchers.Default)

        AlbumSortType.YEAR -> albumsLikedByYearAsc()
        AlbumSortType.SONG_COUNT -> albumsLikedBySongCountAsc()
        AlbumSortType.LENGTH -> albumsLikedByLengthAsc()
        AlbumSortType.PLAY_TIME -> albumsLikedByPlayTimeAsc()
    }.map { it.reversed(descending) }

    fun albumsUploaded(
        sortType: AlbumSortType,
        descending: Boolean,
    ) = when (sortType) {
        AlbumSortType.CREATE_DATE -> albumsUploadedByCreateDateAsc()
        AlbumSortType.NAME -> albumsUploadedByNameAsc()

        AlbumSortType.ARTIST ->
            albumsUploadedByCreateDateAsc().map(::sortAlbumsByArtistGraph)
                .flowOn(Dispatchers.Default)

        AlbumSortType.YEAR -> albumsUploadedByYearAsc()
        AlbumSortType.SONG_COUNT -> albumsUploadedBySongCountAsc()
        AlbumSortType.LENGTH -> albumsUploadedByLengthAsc()
        AlbumSortType.PLAY_TIME -> albumsUploadedByPlayTimeAsc()
    }.map { it.reversed(descending) }

    @Transaction
    @SuppressWarnings(RoomWarnings.QUERY_MISMATCH)
    @Query("SELECT * FROM album WHERE isLocal = 1 ORDER BY title COLLATE NOCASE")
    fun albumsLocalByNameAsc(): Flow<List<Album>>

    @Transaction
    @SuppressWarnings(RoomWarnings.QUERY_MISMATCH)
    @Query("SELECT * FROM album WHERE isLocal = 1 ORDER BY title COLLATE NOCASE DESC")
    fun albumsLocalByNameDesc(): Flow<List<Album>>

    @Transaction
    @SuppressWarnings(RoomWarnings.QUERY_MISMATCH)
    @Query("SELECT * FROM album WHERE isLocal = 1 ORDER BY year")
    fun albumsLocalByYearAsc(): Flow<List<Album>>

    @Transaction
    @SuppressWarnings(RoomWarnings.QUERY_MISMATCH)
    @Query("SELECT * FROM album WHERE isLocal = 1 ORDER BY year DESC")
    fun albumsLocalByYearDesc(): Flow<List<Album>>

    @Transaction
    @SuppressWarnings(RoomWarnings.QUERY_MISMATCH)
    @Query("SELECT * FROM album WHERE isLocal = 1 ORDER BY songCount")
    fun albumsLocalBySongCountAsc(): Flow<List<Album>>

    @Transaction
    @SuppressWarnings(RoomWarnings.QUERY_MISMATCH)
    @Query("SELECT * FROM album WHERE isLocal = 1 ORDER BY songCount DESC")
    fun albumsLocalBySongCountDesc(): Flow<List<Album>>

    @Transaction
    @SuppressWarnings(RoomWarnings.QUERY_MISMATCH)
    @Query("SELECT * FROM album WHERE isLocal = 1 ORDER BY duration")
    fun albumsLocalByLengthAsc(): Flow<List<Album>>

    @Transaction
    @SuppressWarnings(RoomWarnings.QUERY_MISMATCH)
    @Query("SELECT * FROM album WHERE isLocal = 1 ORDER BY duration DESC")
    fun albumsLocalByLengthDesc(): Flow<List<Album>>

    @Transaction
    @SuppressWarnings(RoomWarnings.QUERY_MISMATCH)
    @Query(
        """
        SELECT album.*
        FROM album
        WHERE album.isLocal = 1
        ORDER BY (SELECT SUM(song.totalPlayTime) FROM song WHERE song.albumId = album.id)
    """
    )
    fun albumsLocalByPlayTimeAsc(): Flow<List<Album>>

    @Transaction
    @SuppressWarnings(RoomWarnings.QUERY_MISMATCH)
    @Query(
        """
        SELECT album.*
        FROM album
        WHERE album.isLocal = 1
        ORDER BY (SELECT SUM(song.totalPlayTime) FROM song WHERE song.albumId = album.id) DESC
    """
    )
    fun albumsLocalByPlayTimeDesc(): Flow<List<Album>>

    @Transaction
    @SuppressWarnings(RoomWarnings.QUERY_MISMATCH)
    @Query("SELECT * FROM album WHERE isLocal = 1 ORDER BY rowId")
    fun albumsLocalByRowIdAsc(): Flow<List<Album>>

    @Transaction
    @SuppressWarnings(RoomWarnings.QUERY_MISMATCH)
    @Query("SELECT * FROM album WHERE isLocal = 1 ORDER BY rowId DESC")
    fun albumsLocalByRowIdDesc(): Flow<List<Album>>

    fun albumsLocal(
        sortType: AlbumSortType,
        descending: Boolean,
    ) = when (sortType) {
        AlbumSortType.CREATE_DATE -> albumsLocalByRowIdAsc()
        AlbumSortType.NAME -> albumsLocalByNameAsc()

        AlbumSortType.ARTIST ->
            albumsLocalByRowIdAsc().map(::sortAlbumsByArtistGraph)
                .flowOn(Dispatchers.Default)

        AlbumSortType.YEAR -> albumsLocalByYearAsc()
        AlbumSortType.SONG_COUNT -> albumsLocalBySongCountAsc()
        AlbumSortType.LENGTH -> albumsLocalByLengthAsc()
        AlbumSortType.PLAY_TIME -> albumsLocalByPlayTimeAsc()
    }.map { it.reversed(descending) }

    @Transaction
    @SuppressWarnings(RoomWarnings.QUERY_MISMATCH)
    @Query("SELECT * FROM album WHERE id = :id")
    fun album(id: String): Flow<Album?>

    @Transaction
    @SuppressWarnings(RoomWarnings.QUERY_MISMATCH)
    @Query("SELECT * FROM album WHERE pinnedAt IS NOT NULL ORDER BY pinnedAt")
    fun pinnedAlbums(): Flow<List<Album>>

    @Transaction
    @Query("SELECT * FROM album WHERE id = :albumId")
    fun albumWithSongs(albumId: String): Flow<AlbumWithSongs?>

    @Transaction
    @Query("SELECT * FROM album_artist_map WHERE albumId = :albumId")
    fun albumArtistMaps(albumId: String): List<AlbumArtistMap>

    @Transaction
    @Query("SELECT *, (SELECT COUNT(*) FROM playlist_song_map WHERE playlistId = playlist.id) AS songCount FROM playlist WHERE bookmarkedAt IS NOT NULL ORDER BY rowId")
    fun playlistsByCreateDateAsc(): Flow<List<Playlist>>

    @Transaction
    @Query(
        "SELECT *, (SELECT COUNT(*) FROM playlist_song_map WHERE playlistId = playlist.id) AS songCount FROM playlist WHERE bookmarkedAt IS NOT NULL ORDER BY lastUpdateTime",
    )
    fun playlistsByUpdatedDateAsc(): Flow<List<Playlist>>

    @Transaction
    @Query("SELECT *, (SELECT COUNT(*) FROM playlist_song_map WHERE playlistId = playlist.id) AS songCount FROM playlist WHERE bookmarkedAt IS NOT NULL ORDER BY name COLLATE NOCASE")
    fun playlistsByNameAsc(): Flow<List<Playlist>>

    @Transaction
    @Query("SELECT *, (SELECT COUNT(*) FROM playlist_song_map WHERE playlistId = playlist.id) AS songCount FROM playlist WHERE isEditable AND bookmarkedAt IS NOT NULL ORDER BY name COLLATE NOCASE")
    fun editablePlaylistsByNameAsc(): Flow<List<Playlist>>

    @Transaction
    @Query("SELECT *, (SELECT COUNT(*) FROM playlist_song_map WHERE playlistId = playlist.id) AS songCount FROM playlist WHERE bookmarkedAt IS NOT NULL ORDER BY songCount")
    fun playlistsBySongCountAsc(): Flow<List<Playlist>>

    fun playlists(
        sortType: PlaylistSortType,
        descending: Boolean,
    ) = when (sortType) {
        PlaylistSortType.CREATE_DATE -> playlistsByCreateDateAsc()
        PlaylistSortType.NAME -> playlistsByNameAsc()

        PlaylistSortType.SONG_COUNT -> playlistsBySongCountAsc()
        PlaylistSortType.LAST_UPDATED -> playlistsByUpdatedDateAsc()
    }.map { it.reversed(descending) }

    @Transaction
    @Query("SELECT *, (SELECT COUNT(*) FROM playlist_song_map WHERE playlistId = playlist.id) AS songCount FROM playlist WHERE id = :playlistId")
    fun playlist(playlistId: String): Flow<Playlist?>

    @Transaction
    @Query("SELECT *, (SELECT COUNT(*) FROM playlist_song_map WHERE playlistId = playlist.id) AS songCount FROM playlist WHERE isEditable AND bookmarkedAt IS NOT NULL ORDER BY rowId")
    fun editablePlaylistsByCreateDateAsc(): Flow<List<Playlist>>

    @Transaction
    @Query("SELECT *, (SELECT COUNT(*) FROM playlist_song_map WHERE playlistId = playlist.id) AS songCount FROM playlist WHERE browseId = :browseId")
    fun playlistByBrowseId(browseId: String): Flow<Playlist?>

    @Transaction
    @Query("SELECT COUNT(*) from playlist_song_map WHERE playlistId = :playlistId AND songId = :songId LIMIT 1")
    fun checkInPlaylist(
        playlistId: String,
        songId: String,
    ): Int

    @Query("SELECT songId from playlist_song_map WHERE playlistId = :playlistId AND songId IN (:songIds)")
    fun playlistDuplicates(
        playlistId: String,
        songIds: List<String>,
    ): List<String>

    @Query("SELECT songId FROM playlist_song_map WHERE playlistId = :playlistId ORDER BY position")
    fun playlistSongIds(playlistId: String): List<String>

    /** Appends the songs of [sourceIds] that [target] lacks, keeping source order and skipping duplicates. */
    @Transaction
    fun mergePlaylists(target: Playlist, sourceIds: List<String>) {
        val known = playlistSongIds(target.id).toHashSet()
        val incoming = sourceIds.filter { it != target.id }.flatMap(::playlistSongIds).filter(known::add)
        if (incoming.isNotEmpty()) addSongToPlaylist(target, incoming)
    }

    @Transaction
    fun addSongToPlaylist(playlist: Playlist, songIds: List<String>) {
        var position = playlist.songCount
        songIds.forEach { id ->
            insert(
                PlaylistSongMap(
                    songId = id,
                    playlistId = playlist.id,
                    position = position++
                )
            )
        }
    }

    fun downloadedSongs(
        sortType: SongSortType,
        descending: Boolean
    ): Flow<List<Song>> = when (sortType) {
        SongSortType.CREATE_DATE -> downloadedSongsByCreateDateAsc()
        SongSortType.NAME -> downloadedSongsByNameAsc()

        SongSortType.ARTIST ->
            downloadedSongsByNameAsc().map(::sortSongsByArtistGraph)
                .flowOn(Dispatchers.Default)

        SongSortType.PLAY_TIME -> downloadedSongsByPlayTimeAsc()
    }.map { it.reversed(descending) }

    @Transaction
    @Query("SELECT * FROM song WHERE isDownloaded = 1 ORDER BY dateDownload")
    fun downloadedSongsByCreateDateAsc(): Flow<List<Song>>

    @Transaction
    @Query("SELECT * FROM song WHERE isDownloaded = 1 ORDER BY title COLLATE NOCASE")
    fun downloadedSongsByNameAsc(): Flow<List<Song>>

    @Transaction
    @Query("SELECT * FROM song WHERE isDownloaded = 1 ORDER BY totalPlayTime")
    fun downloadedSongsByPlayTimeAsc(): Flow<List<Song>>

    @Query("UPDATE song SET isDownloaded = :downloaded, dateDownload = :date WHERE id = :songId")
    fun updateDownloadedInfo(songId: String, downloaded: Boolean, date: LocalDateTime?)

    @Transaction
    @Query("SELECT * FROM song WHERE isUploaded = 1 ORDER BY dateDownload")
    fun uploadedSongsByCreateDateAsc(): Flow<List<Song>>

    @Transaction
    @Query("SELECT * FROM song WHERE isUploaded = 1 ORDER BY title COLLATE NOCASE")
    fun uploadedSongsByNameAsc(): Flow<List<Song>>

    @Transaction
    @Query("SELECT * FROM song WHERE isUploaded = 1 ORDER BY totalPlayTime")
    fun uploadedSongsByPlayTimeAsc(): Flow<List<Song>>

    @Transaction
    @Query("SELECT * FROM song WHERE isUploaded = 1 ORDER BY rowId")
    fun uploadedSongsByRowIdAsc(): Flow<List<Song>>

    fun uploadedSongs(
        sortType: SongSortType,
        descending: Boolean,
    ) = when (sortType) {
        SongSortType.CREATE_DATE -> uploadedSongsByCreateDateAsc()
        SongSortType.NAME -> uploadedSongsByNameAsc()

        SongSortType.ARTIST ->
            uploadedSongsByRowIdAsc().map(::sortSongsByArtistGraph)
                .flowOn(Dispatchers.Default)

        SongSortType.PLAY_TIME -> uploadedSongsByPlayTimeAsc()
    }.map { it.reversed(descending) }

    @Transaction
    // Local songs are scanned, never downloaded, so dateDownload is NULL for all of
    // them and ordering by it did nothing. inLibrary holds the file's MediaStore
    // DATE_ADDED (see LocalAudioScanner).
    @Query("SELECT * FROM song WHERE isLocal = 1 ORDER BY inLibrary")
    fun localSongsByCreateDateAsc(): Flow<List<Song>>

    /** Ids only — the scanner preloads these once per scan instead of one getSongById per MediaStore row. */
    @Query("SELECT id FROM song WHERE isLocal = 1")
    suspend fun localSongIds(): List<String>

    @Query("""
        UPDATE song
        SET inLibrary = :dateAdded,
            dateModified = COALESCE(dateModified, :dateModified)
        WHERE id = :songId AND isLocal = 1 AND inLibrary IS NULL
    """)
    suspend fun backfillLocalSongDates(
        songId: String,
        dateAdded: LocalDateTime,
        dateModified: LocalDateTime,
    ): Int

    @Transaction
    @Query("SELECT * FROM song WHERE isLocal = 1 ORDER BY title COLLATE NOCASE")
    fun localSongsByNameAsc(): Flow<List<Song>>

    @Transaction
    @Query("SELECT * FROM song WHERE isLocal = 1 ORDER BY totalPlayTime")
    fun localSongsByPlayTimeAsc(): Flow<List<Song>>

    @Transaction
    @Query("SELECT * FROM song WHERE isLocal = 1 ORDER BY rowId")
    fun localSongsByRowIdAsc(): Flow<List<Song>>

    fun localSongs(
        sortType: SongSortType,
        descending: Boolean,
    ) = when (sortType) {
        SongSortType.CREATE_DATE -> localSongsByCreateDateAsc()
        SongSortType.NAME -> localSongsByNameAsc()

        SongSortType.ARTIST ->
            localSongsByRowIdAsc().map(::sortSongsByArtistGraph)
                .flowOn(Dispatchers.Default)

        SongSortType.PLAY_TIME -> localSongsByPlayTimeAsc()
    }.map { it.reversed(descending) }

    @Transaction
    @Query("SELECT * FROM song WHERE title LIKE '%' || :query || '%' AND inLibrary IS NOT NULL AND (:localOnly = 0 OR isLocal = 1) LIMIT :previewSize")
    fun searchSongs(
        query: String,
        previewSize: Int = Int.MAX_VALUE,
        localOnly: Boolean = false,
    ): Flow<List<Song>>

    @Transaction
    @SuppressWarnings(RoomWarnings.QUERY_MISMATCH)
    @Query(
        "SELECT *, (SELECT COUNT(1) FROM song_artist_map JOIN song ON song_artist_map.songId = song.id WHERE artistId = artist.id AND song.inLibrary IS NOT NULL AND (:localOnly = 0 OR song.isLocal = 1)) AS songCount FROM artist WHERE name LIKE '%' || :query || '%' AND songCount > 0 LIMIT :previewSize",
    )
    fun searchArtists(
        query: String,
        previewSize: Int = Int.MAX_VALUE,
        localOnly: Boolean = false,
    ): Flow<List<Artist>>

    @Transaction
    @SuppressWarnings(RoomWarnings.QUERY_MISMATCH)
    @Query(
        "SELECT * FROM album WHERE title LIKE '%' || :query || '%' AND EXISTS(SELECT * FROM song WHERE song.albumId = album.id AND song.inLibrary IS NOT NULL AND (:localOnly = 0 OR song.isLocal = 1)) LIMIT :previewSize",
    )
    fun searchAlbums(
        query: String,
        previewSize: Int = Int.MAX_VALUE,
        localOnly: Boolean = false,
    ): Flow<List<Album>>

    @Transaction
    @Query(
        "SELECT *, (SELECT COUNT(*) FROM playlist_song_map WHERE playlistId = playlist.id) AS songCount FROM playlist WHERE name LIKE '%' || :query || '%' AND (:localOnly = 0 OR browseId IS NULL) LIMIT :previewSize",
    )
    fun searchPlaylists(
        query: String,
        previewSize: Int = Int.MAX_VALUE,
        localOnly: Boolean = false,
    ): Flow<List<Playlist>>

    // Same scope as the search* queries above, minus the LIKE predicate — the fuzzy fallback
    // in LocalSearchViewModel scores these in Kotlin when the exact substring search (a typo)
    // comes back empty, so this must stay in sync with whatever "in library" means up there.
    @Transaction
    @Query("SELECT * FROM song WHERE inLibrary IS NOT NULL AND (:localOnly = 0 OR isLocal = 1)")
    fun allSearchableSongs(localOnly: Boolean = false): Flow<List<Song>>

    @Transaction
    @SuppressWarnings(RoomWarnings.QUERY_MISMATCH)
    @Query(
        "SELECT *, (SELECT COUNT(1) FROM song_artist_map JOIN song ON song_artist_map.songId = song.id WHERE artistId = artist.id AND song.inLibrary IS NOT NULL AND (:localOnly = 0 OR song.isLocal = 1)) AS songCount FROM artist WHERE songCount > 0",
    )
    fun allSearchableArtists(localOnly: Boolean = false): Flow<List<Artist>>

    @Transaction
    @SuppressWarnings(RoomWarnings.QUERY_MISMATCH)
    @Query(
        "SELECT * FROM album WHERE EXISTS(SELECT * FROM song WHERE song.albumId = album.id AND song.inLibrary IS NOT NULL AND (:localOnly = 0 OR song.isLocal = 1))",
    )
    fun allSearchableAlbums(localOnly: Boolean = false): Flow<List<Album>>

    @Transaction
    @Query(
        "SELECT *, (SELECT COUNT(*) FROM playlist_song_map WHERE playlistId = playlist.id) AS songCount FROM playlist WHERE (:localOnly = 0 OR browseId IS NULL)",
    )
    fun allSearchablePlaylists(localOnly: Boolean = false): Flow<List<Playlist>>

    @Transaction
    @Query("SELECT * FROM event ORDER BY rowId DESC")
    fun events(): Flow<List<EventWithSong>>

    @Transaction
    @Query(
        """SELECT * FROM event
        WHERE (:hideVideoSongs = 0 OR EXISTS (
            SELECT 1 FROM song WHERE song.id = event.songId AND song.isVideo = 0
        ))
        AND (:query = '' OR EXISTS (
            SELECT 1 FROM song WHERE song.id = event.songId AND song.title LIKE '%' || :query || '%'
        ) OR EXISTS (
            SELECT 1 FROM song_artist_map
            JOIN artist ON artist.id = song_artist_map.artistId
            WHERE song_artist_map.songId = event.songId AND artist.name LIKE '%' || :query || '%'
        ))
        ORDER BY rowId DESC LIMIT :limit OFFSET :offset""",
    )
    suspend fun historyEventsPage(
        limit: Int,
        offset: Int,
        hideVideoSongs: Boolean,
        query: String,
    ): List<EventWithSong>

    /** Newest [limit] plays, for Home's Recently Played — bounded so it never loads the whole event table. */
    @Transaction
    @Query("SELECT * FROM event ORDER BY rowId DESC LIMIT :limit")
    fun recentEvents(limit: Int): Flow<List<EventWithSong>>

    /** Latest play only — hot paths (quick picks seed, explore seed) must not load the full event table. */
    @Transaction
    @Query("SELECT * FROM event ORDER BY rowId DESC LIMIT 1")
    fun latestEvent(): Flow<EventWithSong?>

    @Transaction
    @Query("SELECT * FROM event ORDER BY rowId ASC LIMIT 1")
    fun firstEvent(): Flow<EventWithSong?>

    @Query("SELECT COUNT(*) FROM event")
    fun eventCount(): Flow<Int>

    @Transaction
    @Query("DELETE FROM event")
    fun clearListenHistory()

    @Transaction
    @Query("SELECT * FROM search_history WHERE `query` LIKE :query || '%' ORDER BY id DESC")
    fun searchHistory(query: String = ""): Flow<List<SearchHistory>>

    @Transaction
    @Query("DELETE FROM search_history")
    fun clearSearchHistory()

    @Query("DELETE FROM search_history WHERE id = :entryId")
    fun deleteSearchHistory(entryId: Long)

    // Recognition History
    @Transaction
    @Query("SELECT * FROM recognition_history ORDER BY recognizedAt DESC")
    fun recognitionHistory(): Flow<List<RecognitionHistory>>

    @Transaction
    @Query("SELECT * FROM recognition_history WHERE id = :id")
    fun recognitionHistoryById(id: Long): Flow<RecognitionHistory?>

    @Transaction
    @Query("SELECT * FROM recognition_history WHERE title LIKE '%' || :query || '%' OR artist LIKE '%' || :query || '%' ORDER BY recognizedAt DESC")
    fun searchRecognitionHistory(query: String): Flow<List<RecognitionHistory>>

    @Transaction
    @Query("DELETE FROM recognition_history")
    fun clearRecognitionHistory()

    @Transaction
    @Query("DELETE FROM recognition_history WHERE id = :id")
    fun deleteRecognitionHistoryById(id: Long)

    @Transaction
    @Query("UPDATE recognition_history SET liked = :liked WHERE id = :id")
    fun updateRecognitionHistoryLiked(id: Long, liked: Boolean)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(recognitionHistory: RecognitionHistory): Long

    @Delete
    fun delete(recognitionHistory: RecognitionHistory)

    @Query("UPDATE song SET totalPlayTime = totalPlayTime + :playTime WHERE id = :songId")
    fun incrementTotalPlayTime(songId: String, playTime: Long)

    @Query("UPDATE playCount SET count = count + 1 WHERE song = :songId AND year = :year AND month = :month")
    fun incrementPlayCount(songId: String, year: Int, month: Int)

    @Transaction
    suspend fun incrementPlayCount(songId: String) {
        val time = LocalDateTime.now().atOffset(ZoneOffset.UTC)
        val oldCount = getPlayCountByMonth(songId, time.year, time.monthValue).first()

        if (oldCount <= 0) {
            insert(PlayCountEntity(songId, time.year, time.monthValue, 0))
        }
        incrementPlayCount(songId, time.year, time.monthValue)
    }

    @Transaction
    @Query("UPDATE song SET inLibrary = :inLibrary WHERE id = :songId")
    fun inLibrary(
        songId: String,
        inLibrary: LocalDateTime?,
    )

    @Transaction
    @Query("UPDATE song SET libraryAddToken = :libraryAddToken, libraryRemoveToken = :libraryRemoveToken WHERE id = :songId")
    fun addLibraryTokens(
        songId: String,
        libraryAddToken: String?,
        libraryRemoveToken: String?,
    )

    @Transaction
    @Query("SELECT COUNT(1) FROM related_song_map WHERE songId = :songId LIMIT 1")
    fun hasRelatedSongs(songId: String): Boolean

    @Transaction
    @Query(
        "SELECT song.* FROM (SELECT * from related_song_map GROUP BY relatedSongId) map JOIN song ON song.id = map.relatedSongId where songId = :songId",
    )
    fun getRelatedSongs(songId: String): Flow<List<Song>>

    @Transaction
    @Query(
        """
        SELECT song.*
        FROM (SELECT *
              FROM related_song_map
              GROUP BY relatedSongId) map
                 JOIN
             song
             ON song.id = map.relatedSongId
        WHERE songId = :songId
        """
    )
    fun relatedSongs(songId: String): List<Song>

    @Transaction
    @Query(
        """
        UPDATE playlist_song_map SET position =
            CASE
                WHEN position < :fromPosition THEN position + 1
                WHEN position > :fromPosition THEN position - 1
                ELSE :toPosition
            END
        WHERE playlistId = :playlistId AND position BETWEEN MIN(:fromPosition, :toPosition) AND MAX(:fromPosition, :toPosition)
    """,
    )
    fun move(
        playlistId: String,
        fromPosition: Int,
        toPosition: Int,
    )

    @Transaction
    @Query("DELETE FROM playlist_song_map WHERE playlistId = :playlistId")
    fun clearPlaylist(playlistId: String)

    @Transaction
    @Query("SELECT * FROM artist WHERE name = :name")
    fun artistByName(name: String): ArtistEntity?

    @Query("SELECT * FROM artist WHERE id = :id LIMIT 1")
    fun getArtistById(id: String): ArtistEntity?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(song: SongEntity): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(artist: ArtistEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(album: AlbumEntity): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(playlist: PlaylistEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(map: SongArtistMap)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(map: SongAlbumMap)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(map: AlbumArtistMap)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(map: PlaylistSongMap)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(searchHistory: SearchHistory)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(event: Event)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(map: RelatedSongMap)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(playCountEntity: PlayCountEntity): Long

    @Transaction
    fun insert(
        mediaMetadata: MediaMetadata,
        block: (SongEntity) -> SongEntity = { it },
    ) {
        if (insert(mediaMetadata.toSongEntity().let(block)) == -1L) return

        mediaMetadata.artists.forEachIndexed { index, artist ->
            val artistId = artist.id ?: resolveArtistId(artist.name) ?: ArtistEntity.generateArtistId()
            artistIdCache[artist.name] = artistId

            insert(
                ArtistEntity(
                    id = artistId,
                    name = artist.name,
                    channelId = artist.id,
                )
            )

            insert(
                SongArtistMap(
                    songId = mediaMetadata.id,
                    artistId = artistId,
                    position = index,
                )
            )
        }
    }

    @Transaction
    fun insert(albumPage: AlbumPage) {
        val album = albumPage.album
        val songs = albumPage.songs
        val albumEntity = AlbumEntity(
            id = album.browseId,
            playlistId = album.playlistId,
            title = album.title,
            year = album.year,
            thumbnailUrl = album.thumbnail,
            songCount = songs.size,
            duration = songs.sumOf { it.duration ?: 0 },
            explicit = album.explicit || songs.any { it.explicit },
            description = albumPage.description,
        )
        if (insert(albumEntity) == -1L) return

        val songMetadata = songs.map(SongItem::toMediaMetadata)
        songMetadata.forEach(::insert)

        val existingSongsById = getSongsByIdsBlocking(songMetadata.map { it.id })
            .associateBy { it.song.id }
        songMetadata.forEach { metadata ->
            existingSongsById[metadata.id]?.let { existingSong ->
                update(existingSong, metadata)
            }
        }

        songMetadata.forEachIndexed { index, metadata ->
            upsert(
                SongAlbumMap(
                    songId = metadata.id,
                    albumId = album.browseId,
                    index = index,
                ),
            )
        }

        val albumArtists = album.artists?.map { artist ->
            val artistId =
                artist.id
                    ?: resolveArtistId(artist.name)
                    ?: ArtistEntity.generateArtistId()
            artistIdCache[artist.name] = artistId
            ArtistEntity(
                id = artistId,
                name = artist.name,
            )
        }.orEmpty()
        albumArtists.forEach(::insert)
        albumArtists.forEachIndexed { index, artist ->
            insert(
                AlbumArtistMap(
                    albumId = album.browseId,
                    artistId = artist.id,
                    order = index,
                ),
            )
        }
    }

    @Transaction
    fun update(
        song: Song,
        mediaMetadata: MediaMetadata,
    ) {
        update(
            song.song.copy(
                title = mediaMetadata.title,
                duration = mediaMetadata.duration,
                thumbnailUrl = mediaMetadata.thumbnailUrl,
                albumId = mediaMetadata.album?.id,
                albumName = mediaMetadata.album?.title,
                libraryAddToken = mediaMetadata.libraryAddToken,
                libraryRemoveToken = mediaMetadata.libraryRemoveToken
            ),
        )
        songArtistMap(song.id).forEach(::delete)
        mediaMetadata.artists.forEachIndexed { index, artist ->
            val artistId = artist.id ?: resolveArtistId(artist.name) ?: ArtistEntity.generateArtistId()
            artistIdCache[artist.name] = artistId

            insert(
                ArtistEntity(
                    id = artistId,
                    name = artist.name,
                    channelId = artist.id,
                ),
            )
            insert(
                SongArtistMap(
                    songId = song.id,
                    artistId = artistId,
                    position = index,
                ),
            )
        }
    }

    @Update
    fun update(song: SongEntity)

    @Update
    fun update(artist: ArtistEntity)

    @Update
    fun update(album: AlbumEntity)

    @Update
    fun update(playlist: PlaylistEntity)

    @Update
    fun update(map: PlaylistSongMap)

    @Transaction
    fun update(
        artist: ArtistEntity,
        artistPage: ArtistPage
    ) {
        update(
            artist.copy(
                name = artistPage.artist.title,
                thumbnailUrl = artistPage.artist.thumbnail?.resize(544, 544),
                lastUpdateTime = LocalDateTime.now()
            )
        )
    }

    @Transaction
    fun update(
        album: AlbumEntity,
        albumPage: AlbumPage,
        artists: List<ArtistEntity>? = emptyList(),
    ) {
        val pageAlbum = albumPage.album
        val songs = albumPage.songs
        update(
            album.copy(
                id = pageAlbum.browseId,
                playlistId = pageAlbum.playlistId,
                title = pageAlbum.title,
                year = pageAlbum.year,
                thumbnailUrl = pageAlbum.thumbnail,
                songCount = songs.size,
                duration = songs.sumOf { it.duration ?: 0 },
                explicit = pageAlbum.explicit || songs.any { it.explicit },
                description = albumPage.description ?: album.description,
            ),
        )
        if (artists?.size != pageAlbum.artists?.size) {
            artists?.forEach(::delete)
        }

        val songMetadata = songs.map(SongItem::toMediaMetadata)
        songMetadata.forEach(::insert)

        val existingSongsById = getSongsByIdsBlocking(songMetadata.map { it.id })
            .associateBy { it.song.id }
        songMetadata.forEach { metadata ->
            existingSongsById[metadata.id]?.let { existingSong ->
                update(existingSong, metadata)
            }
        }

        songMetadata.forEachIndexed { index, metadata ->
            upsert(
                SongAlbumMap(
                    songId = metadata.id,
                    albumId = pageAlbum.browseId,
                    index = index,
                ),
            )
        }

        pageAlbum.artists?.let { pageArtists ->
            albumArtistMaps(album.id).forEach(::delete)
            val artistEntities = pageArtists.map { artist ->
                val artistId =
                    artist.id
                        ?: resolveArtistId(artist.name)
                        ?: ArtistEntity.generateArtistId()
                artistIdCache[artist.name] = artistId
                ArtistEntity(
                    id = artistId,
                    name = artist.name,
                )
            }
            artistEntities.forEach(::insert)
            artistEntities.forEachIndexed { index, artist ->
                insert(
                    AlbumArtistMap(
                        albumId = pageAlbum.browseId,
                        artistId = artist.id,
                        order = index,
                    ),
                )
            }
        }
    }

    @Update
    fun update(playlistEntity: PlaylistEntity, playlistItem: PlaylistItem) {
        update(
            playlistEntity.copy(
                name = playlistItem.title,
                browseId = playlistItem.id,
                // A cropped cover is stored in filesDir. Keep that local override
                // when the account sync refreshes the remote playlist metadata.
                thumbnailUrl = playlistEntity.thumbnailUrl
                    ?.takeIf { it.startsWith("file:") }
                    ?: playlistItem.thumbnail,
                isEditable = playlistItem.isEditable,
                remoteSongCount = playlistItem.songCountText?.let { Regex("""\d+""").find(it)?.value?.toIntOrNull() },
                playEndpointParams = playlistItem.playEndpoint?.params,
                shuffleEndpointParams = playlistItem.shuffleEndpoint?.params,
                radioEndpointParams = playlistItem.radioEndpoint?.params
            )
        )
    }

    @Upsert
    fun upsert(map: SongAlbumMap)

    @Upsert
    fun upsert(lyrics: LyricsEntity)

    @Upsert
    fun upsert(format: FormatEntity)

    @Upsert
    fun upsert(analysis: SongAnalysisEntity)

    @Upsert
    fun upsert(song: SongEntity)

    @Delete
    fun delete(song: SongEntity)

    @Delete
    fun delete(songArtistMap: SongArtistMap)

    @Delete
    fun delete(artist: ArtistEntity)

    @Delete
    fun delete(album: AlbumEntity)

    @Delete
    fun delete(albumArtistMap: AlbumArtistMap)

    @Delete
    fun delete(playlist: PlaylistEntity)

    @Delete
    fun delete(playlistSongMap: PlaylistSongMap)

    @Query("DELETE FROM playlist WHERE browseId = :browseId")
    fun deletePlaylistById(browseId: String)

    @Delete
    fun delete(lyrics: LyricsEntity)

    @Delete
    fun delete(searchHistory: SearchHistory)

    @Delete
    fun delete(event: Event)

    @Transaction
    @Query("SELECT * FROM playlist_song_map WHERE songId = :songId")
    fun playlistSongMaps(songId: String): List<PlaylistSongMap>

    @Transaction
    @Query("SELECT * FROM playlist_song_map WHERE playlistId = :playlistId AND position >= :from ORDER BY position")
    fun playlistSongMaps(
        playlistId: String,
        from: Int,
    ): List<PlaylistSongMap>

    @RawQuery
    fun raw(supportSQLiteQuery: SupportSQLiteQuery): Int

    fun checkpoint() {
        raw("PRAGMA wal_checkpoint(FULL)".toSQLiteQuery())
    }
}

data class LocalSongDuration(val id: String, val duration: Int, val title: String)
