import SwiftUI
import FaceAuthFramework

/// Face liveness and match against the portrait from the document. The SDK shows the camera and the head-turn prompts; you
/// receive the match score and, shortly after, an advisory capture assessment.
struct FaceView: View {
    let sessionId: String
    let portrait: UIImage?
    let onDone: ([String: String]) -> Void
    let onNeedNewSession: () -> Void

    @State private var similarity: Double?
    @State private var assessment: String?
    @State private var authenticated: UIImage?

    var body: some View {
        FaceAuthCameraUI(
            configurationBuilder: FaceAuthConfigurationBuilder()
                .setUserLicense(userLicense: Config.faceLicense)
                .setSessionID(sessionID: sessionId),
            firstImage: portrait ?? UIImage(),
            secondImage: portrait ?? UIImage(),
            onMatchResultsFirstImage: { results in
                DispatchQueue.main.async { similarity = results.similarity; finish() }
            },
            onMatchResultsSecondImage: { _ in },
            onCaptureAssessment: { a in
                DispatchQueue.main.async { assessment = a.map { String(describing: $0) } ?? "no opinion"; finish() }
            },
            onLivenessOutcome: { _ in },
            onRecaptureRequested: { _ in DispatchQueue.main.async { onNeedNewSession() } },
            authenticatedFace: $authenticated
        )
    }

    private func finish() {
        guard let s = similarity else { return }
        var summary = ["faceSimilarity": String(format: "%.1f %%", s)]
        if let assessment { summary["captureAssessment"] = assessment }
        onDone(summary)
    }
}
