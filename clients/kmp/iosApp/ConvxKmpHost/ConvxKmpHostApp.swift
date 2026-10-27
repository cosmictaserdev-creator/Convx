import SwiftUI

@main
@MainActor
struct ConvxKmpHostApp: App {
    private let watchSession: PhoneWatchSession
    init() {
        watchSession = PhoneWatchSession()
        watchSession.start()
    }
    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}
