import AppKit
import Darwin
import Foundation
import Vision

guard CommandLine.arguments.count == 2 else {
    fputs("Expected the simulator screenshot path.\n", stderr)
    exit(EXIT_FAILURE)
}

let screenshotURL = URL(fileURLWithPath: CommandLine.arguments[1])
let request = VNRecognizeTextRequest()
request.recognitionLevel = .accurate
try VNImageRequestHandler(url: screenshotURL).perform([request])

let visibleText = request.results?.compactMap { $0.topCandidates(1).first?.string } ?? []
print("Visible simulator text: \(visibleText.joined(separator: " | "))")

guard visibleText.contains(where: { $0.localizedCaseInsensitiveContains("YouTube Music") }) else {
    fputs("Convx account header is not visible in the iOS simulator screenshot.\n", stderr)
    exit(EXIT_FAILURE)
}

guard !visibleText.contains(where: { $0.localizedCaseInsensitiveContains("Secure account storage is unavailable") }) else {
    fputs("iOS secure account storage failed during simulator startup.\n", stderr)
    exit(EXIT_FAILURE)
}
