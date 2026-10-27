import Foundation
import Combine
import WatchConnectivity

@MainActor
final class WatchSession: NSObject, ObservableObject, WCSessionDelegate {
    @Published private(set) var snapshot = WatchSnapshot()
    @Published private(set) var reachable = false
    @Published private(set) var connectionMessage: String?
    private var volumeTask: Task<Void, Never>?

    override init() {
        super.init()
        guard WCSession.isSupported() else { return }
        WCSession.default.delegate = self
        WCSession.default.activate()
    }

    func send(_ command: WatchCommand) {
        guard WCSession.default.isReachable else {
            reachable = false
            connectionMessage = "Open Convx on your iPhone to connect."
            return
        }
        guard let data = try? JSONEncoder().encode(command), let json = String(data: data, encoding: .utf8) else { return }
        WCSession.default.sendMessage(["json": json], replyHandler: nil) { [weak self] _ in
            Task { @MainActor [weak self] in self?.connectionMessage = "Couldn't reach your iPhone. Try again." }
        }
    }

    func setVolume(_ value: Double) {
        volumeTask?.cancel()
        volumeTask = Task { @MainActor [weak self] in
            try? await Task.sleep(for: .milliseconds(140))
            guard !Task.isCancelled else { return }
            self?.send(WatchCommand(action: "VOLUME", volume: Float(value)))
        }
    }

    private func receive(_ json: String) {
        guard let data = json.data(using: .utf8), let incoming = try? JSONDecoder().decode(WatchSnapshot.self, from: data), incoming.version == 1 else { return }
        guard incoming.sessionId != snapshot.sessionId || incoming.sequence >= snapshot.sequence else { return }
        snapshot = incoming
        connectionMessage = nil
    }

    nonisolated func session(_ session: WCSession, activationDidCompleteWith activationState: WCSessionActivationState, error: Error?) {
        let cached = session.receivedApplicationContext["json"] as? String
        let connected = session.isReachable
        Task { @MainActor [weak self] in
            if let cached { self?.receive(cached) }
            self?.reachable = connected
            if connected { self?.send(WatchCommand(action: "SYNC")) }
        }
    }
    nonisolated func sessionReachabilityDidChange(_ session: WCSession) {
        let connected = session.isReachable
        Task { @MainActor [weak self] in
            self?.reachable = connected
            if connected { self?.send(WatchCommand(action: "SYNC")) }
        }
    }
    nonisolated func session(_ session: WCSession, didReceiveApplicationContext applicationContext: [String: Any]) {
        guard let json = applicationContext["json"] as? String else { return }
        Task { @MainActor [weak self] in self?.receive(json) }
    }
    nonisolated func session(_ session: WCSession, didReceiveMessage message: [String: Any]) {
        guard let json = message["json"] as? String else { return }
        Task { @MainActor [weak self] in self?.receive(json) }
    }
}
