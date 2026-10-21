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
            rows.append(("Session endpoint", false, "Could not reach \(Config.sessionURLHost). Is the backend running, and are the phone and the computer on the same network?"))
        }
        // Ask here when the user has not been asked yet, so this row turns green after the system prompt.
        var status = AVCaptureDevice.authorizationStatus(for: .video)
        if status == .notDetermined {
            _ = await AVCaptureDevice.requestAccess(for: .video)
            status = AVCaptureDevice.authorizationStatus(for: .video)
        }
        let cam = status == .authorized
        let camHint = cam ? "Granted." : (status == .denied || status == .restricted
            ? "Denied. Turn the camera on for this app in Settings."
            : "Not allowed yet.")
        rows.append(("Camera permission", cam, camHint))
        running = false
    }
}
