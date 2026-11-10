import Foundation

struct WatchEntry: Codable, Hashable {
    let providerId: String
    let id: String
    let title: String
    let kind: String
    let artists: [String]
    let artworkUrl: String?
    let durationMs: Int64?
    let queueIndex: Int?
    var identity: String { providerId + "\n" + id + (queueIndex.map { "@\($0)" } ?? "") }
}

struct WatchSnapshot: Codable {
    var version = 1
    var sessionId = ""
    var sequence: Int64 = 0
    var track: WatchEntry?
    var isPlaying = false
    var canSkipPrevious = false
    var canSkipNext = false
    var positionMs: Int64 = 0
    var durationMs: Int64?
    var volume: Float = 1
    var canAdjustVolume = true
    var shuffle = false
    var repeatMode = "OFF"
    var queue: [WatchEntry] = []
    var libraryTitle = "Library"
    var library: [WatchEntry] = []
    var isLoading = false
    var message: String?
}

struct WatchCommand: Encodable {
    var version = 1
    let action: String
    var collection: String?
    var entry: WatchEntry?
    var positionMs: Int64?
    var volume: Float?
}
