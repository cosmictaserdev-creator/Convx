import SwiftUI

struct WatchNowPlayingView: View {
    @EnvironmentObject private var session: WatchSession
    @Environment(\.dismiss) private var dismiss
    @State private var more = false
    @State private var crownVolume = 1.0
    private var progress: Double {
        guard let duration = session.snapshot.durationMs, duration > 0 else { return 0 }
        return min(1, max(0, Double(session.snapshot.positionMs) / Double(duration)))
    }

    var body: some View {
        GeometryReader { geometry in
            VStack(spacing: 6) {
                WatchArtwork(url: session.snapshot.track?.artworkUrl)
                    .frame(width: min(130, geometry.size.height * 0.45), height: min(130, geometry.size.height * 0.45))
                    .clipShape(.rect(cornerRadius: 10))
                Text(session.snapshot.track?.title ?? "Nothing Playing").font(.system(size: 18, weight: .semibold)).lineLimit(1)
                Text(session.snapshot.track?.artists.joined(separator: ", ") ?? "Convx")
                    .font(.system(size: 16)).foregroundStyle(.secondary).lineLimit(1)
                HStack {
                    transport("Previous", "backward.fill", enabled: session.snapshot.canSkipPrevious) { session.send(WatchCommand(action: "PREVIOUS")) }
                    Spacer(minLength: 12)
                    Button { session.send(WatchCommand(action: "PLAY_PAUSE")) } label: {
                        Image(systemName: session.snapshot.isPlaying ? "pause.fill" : "play.fill")
                            .contentTransition(.symbolEffect(.replace))
                            .font(.system(size: 27, weight: .semibold)).frame(width: 52, height: 52)
                            .background(.white.opacity(0.07), in: Circle())
                            .overlay(Circle().stroke(.white.opacity(0.18), lineWidth: 2))
                            .overlay(Circle().trim(from: 0, to: progress).stroke(.white, style: StrokeStyle(lineWidth: 2, lineCap: .round)).rotationEffect(.degrees(-90)))
                    }
                    .buttonStyle(.plain).disabled(!session.reachable || session.snapshot.track == nil)
                    .accessibilityLabel(session.snapshot.isPlaying ? "Pause" : "Play")
                    Spacer(minLength: 12)
                    transport("Next", "forward.fill", enabled: session.snapshot.canSkipNext) { session.send(WatchCommand(action: "NEXT")) }
                }.padding(.horizontal, 7)
            }
            .frame(maxWidth: .infinity, maxHeight: .infinity)
        }
        .focusable()
        .digitalCrownRotation($crownVolume, from: 0, through: 1, by: 0.05, sensitivity: .low, isContinuous: false, isHapticFeedbackEnabled: true)
        .onChange(of: crownVolume) { _, value in
            if session.snapshot.canAdjustVolume && abs(value - Double(session.snapshot.volume)) > 0.01 { session.setVolume(value) }
        }
        .onAppear { crownVolume = Double(session.snapshot.volume) }
        .toolbar {
            ToolbarItem(placement: .cancellationAction) { Button("Close", systemImage: "xmark") { dismiss() } }
            ToolbarItem(placement: .primaryAction) { Button("More", systemImage: "ellipsis") { more = true } }
        }
        .sheet(isPresented: $more) { NavigationStack { WatchPlaybackOptions() } }
    }

    private func transport(_ label: String, _ symbol: String, enabled: Bool, action: @escaping () -> Void) -> some View {
        Button(action: action) { Image(systemName: symbol).font(.system(size: 23)).frame(width: 37, height: 42) }
            .buttonStyle(.plain).disabled(!session.reachable || !enabled).accessibilityLabel(label)
    }
}

struct WatchPlaybackOptions: View {
    @EnvironmentObject private var session: WatchSession
    var body: some View {
        List {
            Toggle("Shuffle", isOn: Binding(get: { session.snapshot.shuffle }, set: { _ in session.send(WatchCommand(action: "SHUFFLE")) }))
            Button("Repeat: " + session.snapshot.repeatMode.lowercased()) { session.send(WatchCommand(action: "REPEAT")) }
            NavigationLink("Playing Next") { WatchQueueView() }
            Slider(value: Binding(get: { Double(session.snapshot.volume) }, set: { session.setVolume($0) }), in: 0...1)
                .accessibilityLabel("Volume")
                .disabled(!session.snapshot.canAdjustVolume)
        }
        .navigationTitle("Playback")
        .disabled(!session.reachable)
    }
}

struct WatchQueueView: View {
    @EnvironmentObject private var session: WatchSession
    var body: some View {
        List(session.snapshot.queue, id: \.identity) { entry in
            Button { session.send(WatchCommand(action: "PLAY", entry: entry)) } label: { WatchTrackRow(entry: entry) }
                .disabled(!session.reachable)
        }
        .navigationTitle("Playing Next")
        .task { session.send(WatchCommand(action: "QUEUE")) }
    }
}
