import SwiftUI
import UIKit
import ConvxShared

struct ContentView: UIViewControllerRepresentable {
    func makeUIViewController(context: Context) -> UIViewController {
        if ProcessInfo.processInfo.arguments.contains("--convx-keychain-probe") {
            return IosMusicControllerKt.iosMusicControllerWithKeychainProbe()
        }
        return IosMusicControllerKt.iosMusicController()
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}
