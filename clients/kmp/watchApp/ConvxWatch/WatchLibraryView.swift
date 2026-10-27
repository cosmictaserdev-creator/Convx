import SwiftUI

struct WatchLibraryView: View {
    @EnvironmentObject private var session: WatchSession
    @State private var nowPlaying = false
    private let collections = [
        ("Playlists", "music.note.list", "PLAYLISTS"),
        ("Artists", "mic", "ARTISTS"),
        ("Albums", "square.stack", "ALBUMS"),
        ("Songs", "music.note", "SONGS")
    ]

    var body: some View {
        NavigationStack {
            List {
                ForEach(collections, id: \.2) { title, symbol, collection in
                    NavigationLink {
                        WatchBrowserView(title: title, collection: collection)
                    } label: {
                        Label { Text(title).font(.system(size: 18, weight: .semibold)) }
                        icon: { Image(systemName: symbol).foregroundStyle(.pink).frame(width: 25) }
                        .padding(.vertical, 9)
                    }
                }
                if !session.reachable {
                    Text(session.connectionMessage ?? "Open Convx on your iPhone to connect.")
                        .font(.footnote).foregroundStyle(.secondary)
                }
            }
            .listStyle(.carousel)
            .navigationTitle("Library")
            .toolbar {
                ToolbarItem(placement: .primaryAction) {
                    Button("Now Playing", systemImage: "waveform") { nowPlaying = true }
                        .disabled(session.snapshot.track == nil)
                }
            }
            .sheet(isPresented: $nowPlaying) { NavigationStack { WatchNowPlayingView() } }
        }
    }
}

struct WatchBrowserView: View {
    @EnvironmentObject private var session: WatchSession
    @State private var nowPlaying = false
    let title: String
    var collection: String? = nil
    var openedEntry: WatchEntry? = nil
    @State private var child: WatchEntry?
    @State private var content = WatchSnapshot()

    var body: some View {
        List {
            if content.isLoading { ProgressView().frame(maxWidth: .infinity) }
            if let message = content.message { Text(message).font(.footnote).foregroundStyle(.secondary) }
            ForEach(content.library, id: \.identity) { entry in
                Button {
                    if entry.kind == "TRACK" {
                        session.send(WatchCommand(action: "PLAY", entry: entry)); nowPlaying = true
                    } else {
                        child = entry
                    }
                } label: { WatchTrackRow(entry: entry) }
                .disabled(!session.reachable)
            }
            if !content.isLoading && content.library.isEmpty && content.message == nil {
                Text("Your library will appear here.").foregroundStyle(.secondary)
            }
        }
        .listStyle(.carousel)
        .navigationTitle(title)
        .onReceive(session.$snapshot) { snapshot in
            if snapshot.libraryTitle == title { content = snapshot }
        }
        .task {
            if let openedEntry { session.send(WatchCommand(action: "OPEN", entry: openedEntry)) }
            else if let collection { session.send(WatchCommand(action: "BROWSE", collection: collection)) }
        }
        .navigationDestination(isPresented: Binding(get: { child != nil }, set: { if !$0 { child = nil } })) {
            if let child { WatchBrowserView(title: child.title, openedEntry: child) }
        }
        .sheet(isPresented: $nowPlaying) { NavigationStack { WatchNowPlayingView() } }
    }
}

struct WatchTrackRow: View {
    let entry: WatchEntry
    var body: some View {
        HStack(spacing: 9) {
            WatchArtwork(url: entry.artworkUrl).frame(width: 38, height: 38).clipShape(.rect(cornerRadius: 5))
            VStack(alignment: .leading, spacing: 3) {
                Text(entry.title).font(.system(size: 14, weight: .semibold)).lineLimit(2)
                if !entry.artists.isEmpty { Text(entry.artists.joined(separator: ", ")).font(.caption2).foregroundStyle(.secondary).lineLimit(1) }
            }
        }
    }
}

struct WatchArtwork: View {
    let url: String?
    var body: some View {
        ZStack {
            RoundedRectangle(cornerRadius: 10).fill(.purple.opacity(0.18))
            Image(systemName: "music.note").resizable().scaledToFit().padding(25).foregroundStyle(.purple.opacity(0.7))
            if let url, let address = URL(string: url) {
                AsyncImage(url: address) { image in image.resizable().scaledToFill() } placeholder: { Color.clear }
            }
        }.clipped()
    }
}
