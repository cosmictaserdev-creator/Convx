package com.convx.storage.domain

import io.ktor.http.decodeURLPart

object RemotePath {
    fun normalize(value: String): String {
        require('\u0000' !in value) { "Invalid folder path" }
        val parts = value.replace('\\', '/').split('/').filter(String::isNotEmpty)
        require(parts.none { it == ".." }) { "Folder path escapes the source" }
        return parts.filterNot { it == "." }.joinToString("/")
    }

    fun parent(path: String): String = normalize(path).substringBeforeLast('/', "")
    fun name(path: String): String = normalize(path).substringAfterLast('/')
    fun child(folder: String, name: String): String {
        require(name.isNotBlank() && '/' !in name && '\\' !in name && name != "." && name != "..") { "Invalid file name" }
        return normalize(listOf(folder, name).filter(String::isNotEmpty).joinToString("/"))
    }
}

data class NetworkMediaLocation(val serverId: String, val path: String)

object NetworkMediaUri {
    const val Scheme = "convx-network"

    fun create(serverId: String, path: String): String {
        require(serverId.matches(Regex("[A-Za-z0-9_-]{1,80}"))) { "Invalid server identifier" }
        return "$Scheme://$serverId/" + encodeRemoteUriPath(RemotePath.normalize(path))
    }

    fun parse(value: String): NetworkMediaLocation? = try {
        if (!value.startsWith("$Scheme://") || '?' in value || '#' in value) null
        else {
            val rest = value.removePrefix("$Scheme://")
            val authority = rest.substringBefore('/')
            if (!authority.matches(Regex("[A-Za-z0-9_-]{1,80}"))) null
            else NetworkMediaLocation(authority, RemotePath.normalize(rest.substringAfter('/', "").decodeURLPart()))
        }
    } catch (_: Exception) { null }
}

data class NetworkEntry(val path: String, val name: String, val directory: Boolean, val size: Long = -1)

data class NetworkLibrary(val tracks: List<NetworkTrack>, val truncated: Boolean = false)

data class NetworkTrack(
    val id: String,
    val title: String,
    val artist: String?,
    val album: String?,
    val artwork: String?,
    val size: Long,
    val albumArtist: String? = null,
    val trackNumber: Int? = null,
    val discNumber: Int? = null,
    val year: Int? = null,
    val durationSeconds: Int = 0,
)

object NetworkMedia {
    private val audioExtensions = setOf("mp3", "m4a", "flac", "ogg", "opus", "aac", "wav", "wma", "aif", "aiff", "webm")
    private val imageExtensions = setOf("jpg", "jpeg", "png", "webp")
    fun isAudio(name: String): Boolean = name.substringAfterLast('.', "").lowercase() in audioExtensions
    fun isImage(name: String): Boolean = name.substringAfterLast('.', "").lowercase() in imageExtensions

    /** "01 - Title", "1-01 - Title", "05. Title", "01 Title": digits, then a separator. */
    private val numberPrefixRegex = Regex("^(?:(\\d{1,2})[-.])?(\\d{1,3})(\\s*[-. ]\\s*)")

    /**
     * The part after " - " in "Song Name - Remastered 2011" is a version note, not a title, so the whole name is the
     * title and no artist can be inferred. Only genuine suffix phrases count: "Queen - Radio Ga Ga",
     * "Oasis - Live Forever" and "Prince - 1999" are still artist - title.
     */
    private val titleQualifier = Regex(
        "^[(\\[]?(\\d{4}\\s+(remaster(ed)?|re-?mix|mix|version|edit|digital remaster)|re-?master(ed)?|remix(ed)?)\\b|" +
            "^[(\\[]?(live (at|in|from|on|version)\\b|live$|acoustic( version)?\\b|demo( version)?\\b|mono\\b|stereo\\b|" +
            "instrumental\\b|radio edit\\b|single (version|edit)\\b|album version\\b|extended (mix|version)\\b|" +
            "deluxe( edition| version)?\\b|bonus track\\b|explicit\\b|feat\\.|ft\\.|featuring\\b)",
        RegexOption.IGNORE_CASE,
    )

    /** Only the names people actually use for album art; anything else in a folder is not assumed to be a cover. */
    private val coverNames = listOf("cover", "folder", "front", "albumart", "albumartsmall", "album", "artwork", "thumb")

    /**
     * A leading number is a track number when it is zero-padded ("01 Title") or followed by a dash or dot
     * ("1 - Title", "5. Title"). Digits then only a space are usually part of the artist: "50 Cent - In da Club",
     * "21 Savage", "3 Doors Down", "5 Seconds of Summer" are not tracks 50, 21, 3 and 5.
     */
    private fun trackPrefix(stem: String): MatchResult? {
        val match = numberPrefixRegex.find(stem) ?: return null
        val digits = match.groupValues[2]
        val separator = match.groupValues[3]
        val spaceOnly = separator.none { it == '-' || it == '.' }
        return match.takeUnless { spaceOnly && !digits.startsWith("0") && match.groupValues[1].isEmpty() }
    }

    fun track(serverId: String, entry: NetworkEntry, images: List<NetworkEntry>): NetworkTrack {
        val stem = entry.name.substringBeforeLast('.').trim()
        val numberPrefix = trackPrefix(stem)
        val trackNumber = numberPrefix?.groupValues?.get(2)?.toIntOrNull()
        val stripped = numberPrefix?.let { stem.substring(it.range.last + 1).trim() } ?: stem
        val parts = stripped.split(" - ", limit = 2)
        val first = parts.first().trim()
        val splittable = numberPrefix == null && parts.size == 2 && first.isNotBlank() && first.any(Char::isLetter) &&
            first.toIntOrNull() == null && !titleQualifier.containsMatchIn(parts[1].trim())
        val artist = first.takeIf { splittable }
        val title = if (artist != null) parts[1].trim().ifBlank { stripped } else stripped
        val parent = RemotePath.parent(entry.path)
        val discFolder = Regex("(?i)(?:cd|disc|disk)\\s*([0-9]+)").matchEntire(RemotePath.name(parent))
        val discNumber = numberPrefix?.groupValues?.get(1)?.toIntOrNull() ?: discFolder?.groupValues?.get(1)?.toIntOrNull()
        val albumFolder = if (discFolder == null) parent else RemotePath.parent(parent)
        val sameFolder = images.filter { RemotePath.parent(it.path) == parent }
        val cover = sameFolder.mapNotNull { image ->
            val name = image.name.substringBeforeLast('.').lowercase()
            val rank = when {
                name == stem.lowercase() -> 0
                name == "cover" -> 1
                name == "folder" -> 2
                name == "front" -> 3
                name in coverNames -> 4
                coverNames.any { name.startsWith(it) } -> 5
                else -> null
            }
            rank?.let { it to image }
        }.minByOrNull { it.first }?.second
        // A lone image is almost certainly the cover; with several unrelated images guessing would give every song in
        // a flat folder the same wrong art, so leave it to the embedded picture.
        val artwork = (cover ?: sameFolder.singleOrNull())?.let { NetworkMediaUri.create(serverId, it.path) }
        return NetworkTrack(NetworkMediaUri.create(serverId, entry.path), title, artist, albumFolder.substringAfterLast('/').takeIf(String::isNotBlank), artwork, entry.size, trackNumber = trackNumber, discNumber = discNumber)
    }
}
