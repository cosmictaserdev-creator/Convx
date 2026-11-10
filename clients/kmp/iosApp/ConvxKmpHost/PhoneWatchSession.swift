import Foundation
import WatchConnectivity

@MainActor
final class PhoneWatchSession: NSObject, WCSessionDelegate {
    private var snapshot: String?
    private var lastContext: Data?
    private var observer: NSObjectProtocol?

    func start() {
        guard WCSession.isSupported(), observer == nil else { return }
        observer = NotificationCenter.default.addObserver(
            forName: Notification.Name("ConvxWatchSnapshot"), object: nil, queue: .main
        ) { [weak self] note in
            guard let json = note.userInfo?["json"] as? String else { return }
            Task { @MainActor [weak self] in self?.publish(json) }
        }
        WCSession.default.delegate = self
        WCSession.default.activate()
    }

    private func publish(_ json: String) {
        snapshot = json
        let session = WCSession.default
        guard session.activationState == .activated, session.isPaired, session.isWatchAppInstalled else { return }
        if let data = json.data(using: .utf8), var context = (try? JSONSerialization.jsonObject(with: data)) as? [String: Any] {
            context.removeValue(forKey: "sequence")
            context.removeValue(forKey: "positionMs")
            let fingerprint = try? JSONSerialization.data(withJSONObject: context, options: .sortedKeys)
            if fingerprint != lastContext {
                do {
                    try session.updateApplicationContext(["json": json])
                    lastContext = fingerprint
                } catch { /* A later snapshot retries the current context. */ }
            }
        }
        if session.isReachable { session.sendMessage(["json": json], replyHandler: nil, errorHandler: nil) }
    }

    private func refresh() {
        if let snapshot { publish(snapshot) }
        NotificationCenter.default.post(name: Notification.Name("ConvxWatchCommand"), object: nil,
                                        userInfo: ["json": "{\"action\":\"SYNC\"}"])
    }

    nonisolated func session(_ session: WCSession, activationDidCompleteWith activationState: WCSessionActivationState, error: Error?) {
        Task { @MainActor [weak self] in self?.refresh() }
    }
    nonisolated func sessionReachabilityDidChange(_ session: WCSession) {
        Task { @MainActor [weak self] in self?.refresh() }
    }
    nonisolated func sessionWatchStateDidChange(_ session: WCSession) {
        Task { @MainActor [weak self] in self?.refresh() }
    }
    nonisolated func session(_ session: WCSession, didReceiveMessage message: [String: Any]) {
        guard let json = message["json"] as? String else { return }
        Task { @MainActor in
            NotificationCenter.default.post(name: Notification.Name("ConvxWatchCommand"), object: nil, userInfo: ["json": json])
        }
    }
    nonisolated func sessionDidBecomeInactive(_ session: WCSession) {}
    nonisolated func sessionDidDeactivate(_ session: WCSession) { session.activate() }
}
