import SwiftUI

@main
@MainActor
struct ConvxWatchApp: App {
    @StateObject private var session = WatchSession()
    var body: some Scene {
        WindowGroup {
            WatchLibraryView().environmentObject(session)
                .tint(Color(red: 250.0 / 255, green: 35.0 / 255, blue: 59.0 / 255))
                .preferredColorScheme(.dark)
        }
    }
}
