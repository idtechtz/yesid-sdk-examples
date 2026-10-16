import SwiftUI
import AVFoundation

/// Confirms the three things that break a first run: configuration, your session endpoint, camera permission.
struct SetupCheckView: View {
    let onBack: () -> Void
    @State private var rows: [(String, Bool, String)] = []
    @State private var running = true

    var body: some View {
        VStack(alignment: .leading, spacing: 14) {
            Text("Setup check").font(.title2).bold()
            ForEach(rows.indices, id: \.self) { i in
                let r = rows[i]
                HStack(alignment: .top) {
                    Text(r.1 ? "✓" : "✗").foregroundColor(r.1 ? .green : .red).bold()
                    VStack(alignment: .leading) { Text(r.0).bold(); Text(r.2).font(.footnote) }
                }
            }
            if running { ProgressView() }
            Button("Back", action: onBack).buttonStyle(.bordered)
        }
        .padding(24)
        .task { await run() }
    }

    private func run() async {
        let missing = Config.missing()
        rows.append(("Configuration", missing.isEmpty,
                     missing.isEmpty ? "All keys are set." : "Fill in: \(missing.joined(separator: ", "))."))
        do {
            _ = try await SessionApi.create()
            rows.append(("Session endpoint", true, "Your backend returned a session id."))
        } catch SessionApi.Failure.message(let m) {
            rows.append(("Session endpoint", false, m))
        } catch {
            rows.append(("Session endpoint", false, "Could not reach SESSION_URL. Is the backend running?"))
        }
        let cam = AVCaptureDevice.authorizationStatus(for: .video) == .authorized
        rows.append(("Camera permission", cam, cam ? "Granted." : "Allow the camera in Settings, or you are asked on first scan."))
        running = false
    }
}
