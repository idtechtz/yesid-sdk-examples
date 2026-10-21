import SwiftUI
import AVFoundation
import YesidOCRFramework

/// Document scan. The SDK shows its own camera UI (guidance, errors); you give it the licence key and the session id and
/// receive the result or a terminal state. In "library" mode you pick a photo and submit it with `submitGalleryFront`.
struct OcrView: View {
    let sessionId: String
    let upload: Bool
    let onDone: ([String: String], UIImage?) -> Void
    let onNeedNewSession: () -> Void

    @State private var lens: AVCaptureDevice.Position = .back
    @State private var picking = false
    @State private var message: String?
    @State private var builder = OCRConfigurationBuilder()

    var body: some View {
        Group {
            if upload { libraryBody } else { cameraBody }
        }
        .onAppear {
            _ = builder.setUserLicense(userLicense: Config.ocrLicense).setSessionId(sessionId: sessionId)
        }
    }

    private var cameraBody: some View {
        OCRCameraUI(
            configurationBuilder: builder,
            cameraLensFacing: $lens,
            onResults: { response in
                DispatchQueue.main.async { onDone(response.documentText ?? [:], response.documentImages["portrait"]) }
            },
            // The SDK ended this session without a result and says whether a NEW one is needed.
            onTerminal: { state in
                DispatchQueue.main.async { if !state.sessionReusable { onNeedNewSession() } }
            }
        )
    }

    private var libraryBody: some View {
        VStack(spacing: 14) {
            Text("Choose a photo of one ID document").font(.headline)
            Button("Select photo") { picking = true }
            if let message { Text(message).multilineTextAlignment(.center) }
        }
        .padding(24)
        .sheet(isPresented: $picking) {
            ImagePicker { image in
                picking = false
                guard let image else { return }
                builder.submitGalleryFront(image, sessionId: sessionId) { outcome in
                    DispatchQueue.main.async {
                        switch outcome {
                        case .completed(let r): onDone(r.documentText ?? [:], r.documentImages["portrait"])
                        case .backRequired: message = "The back of the document is needed too."
                        case .failed(let f): message = f.message
                        case .inconclusive(let f): message = f.message
                        }
                    }
                }
            }
        }
    }
}

/// Minimal photo-library picker.
struct ImagePicker: UIViewControllerRepresentable {
    let onPick: (UIImage?) -> Void
    func makeCoordinator() -> Coordinator { Coordinator(onPick) }
    func makeUIViewController(context: Context) -> UIImagePickerController {
        let p = UIImagePickerController(); p.sourceType = .photoLibrary; p.delegate = context.coordinator; return p
    }
    func updateUIViewController(_ vc: UIImagePickerController, context: Context) {}
    final class Coordinator: NSObject, UIImagePickerControllerDelegate, UINavigationControllerDelegate {
        let onPick: (UIImage?) -> Void
        init(_ onPick: @escaping (UIImage?) -> Void) { self.onPick = onPick }
        func imagePickerController(_ picker: UIImagePickerController, didFinishPickingMediaWithInfo info: [UIImagePickerController.InfoKey: Any]) {
            onPick(info[.originalImage] as? UIImage)
        }
        func imagePickerControllerDidCancel(_ picker: UIImagePickerController) { onPick(nil) }
    }
}
