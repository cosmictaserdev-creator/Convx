import AppKit
import Darwin
import Foundation
import Vision

guard CommandLine.arguments.count == 2 else {
    fputs("Expected the Keychain round-trip simulator screenshot path.\n", stderr)
    exit(EXIT_FAILURE)
}

let screenshotURL = URL(fileURLWithPath: CommandLine.arguments[1])
let request = VNRecognizeTextRequest()
request.recognitionLevel = .accurate
try VNImageRequestHandler(url: screenshotURL).perform([request])

let visibleText = request.results?.compactMap { $0.topCandidates(1).first?.string } ?? []
print("Keychain probe text: \(visibleText.joined(separator: " | "))")

guard visibleText.contains(where: { $0.localizedCaseInsensitiveContains("Keychain session storage round trip passed") }) else {
    fputs("The signed iOS host did not complete its Keychain write/read/clear round trip.\n", stderr)
    exit(EXIT_FAILURE)
}
