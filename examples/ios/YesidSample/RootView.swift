import SwiftUI
import UIKit

/// Where the sample is: a tiny state machine so the integration code stays easy to read.
enum Step {
    case home
    case setup
    case ocr(upload: Bool)
    case ocrDone(text: [String: String], portrait: UIImage?)
    case face(portrait: UIImage?)
    case error(String)
}

struct RootView: View {
    @State private var step: Step = .home
    @State private var sessionId = ""
    @State private var busy = false

    var body: some View {
        let missing = Config.missing()
        if !missing.isEmpty {
            VStack(alignment: .leading, spacing: 12) {
                Text("Configuration missing").font(.title2).bold()
                Text("Copy Config.example.plist to Config.plist and fill in: \(missing.joined(separator: ", ")). Then rebuild.")
            }.padding(24)
        } else {
            content
        }
    }

    @ViewBuilder private var content: some View {
        switch step {
        case .home:
            VStack(spacing: 14) {
                Text("Yesid SDK sample").font(.title2).bold()
                Text(Config.environment.capitalized).font(.caption).bold()
                    .padding(.horizontal, 10).padding(.vertical, 4)
                    .background(Capsule().fill(Color.orange.opacity(0.25)))
                Button("Full verification (recommended)") { start(.ocr(upload: false)) }.buttonStyle(.borderedProminent)
                Button("Use a photo from the library") { start(.ocr(upload: true)) }.buttonStyle(.bordered)
                Button("Setup check") { step = .setup }.buttonStyle(.bordered)
                if busy { ProgressView() }
            }.padding(24)
        case .setup:
            SetupCheckView(onBack: { step = .home })
        case .ocr(let upload):
            OcrView(sessionId: sessionId, upload: upload,
                    onDone: { text, portrait in step = .ocrDone(text: text, portrait: portrait) },
                    onNeedNewSession: { start(.ocr(upload: upload)) })
        case .ocrDone(let text, let portrait):
            ResultView(title: "Document scanned", fields: text, image: portrait,
                       primary: ("Continue to face check", { start(.face(portrait: portrait)) }),
                       secondary: ("Start over", { step = .home }))
        case .face(let portrait):
            FaceView(sessionId: sessionId, portrait: portrait,
                     onDone: { summary in step = .ocrDone(text: summary, portrait: portrait) },
                     onNeedNewSession: { start(.face(portrait: portrait)) })
        case .error(let message):
            VStack(spacing: 14) {
                Text("Something went wrong").font(.title2).bold()
                Text(message)
                Button("Back") { step = .home }
            }.padding(24)
        }
    }

    /// 2. A session per run, created by YOUR backend.
    private func start(_ next: Step) {
        busy = true
        Task {
            do { sessionId = try await SessionApi.create(); step = next }
            catch SessionApi.Failure.message(let m) { step = .error(m) }
            catch { step = .error("Could not reach your session endpoint") }
            busy = false
        }
    }
}
