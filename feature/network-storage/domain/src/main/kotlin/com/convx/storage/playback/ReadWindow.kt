package com.convx.storage.playback

class ReadWindow(position: Long, requestedLength: Long, fileLength: Long) {
    init {
        require(position >= 0 && requestedLength >= -1 && fileLength >= -1)
        require(fileLength < 0 || position <= fileLength) { "Position is outside the file" }
    }
    var position = position
        private set
    var remaining: Long = when {
        fileLength >= 0 && requestedLength >= 0 -> minOf(fileLength - position, requestedLength)
        fileLength >= 0 -> fileLength - position
        else -> requestedLength
    }
        private set

    fun readLength(requested: Int): Int = if (remaining < 0) requested else minOf(requested.toLong(), remaining).toInt()
    fun advance(count: Int) {
        require(count >= 0 && (remaining < 0 || count <= remaining))
        require(position <= Long.MAX_VALUE - count)
        position += count
        if (remaining >= 0) remaining -= count
    }
}
