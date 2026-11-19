import SwiftUI

/// "Step n of 3" strip and the one-line hint saying which SDK call the screen uses and where it lives in this project.
struct StepHeader: View {
    let step: Int
    let title: String
    let hint: String
    /// A "Back" button above the strip when set.
    var onBack: (() -> Void)? = nil

    var body: some View {
        VStack(alignment: .leading, spacing: 4) {
            if let onBack { Button(action: onBack) { Label("Back", systemImage: "chevron.left") }.font(.subheadline) }
            Text("Step \(step) of 3 · \(title)").font(.caption).bold()
            HStack(spacing: 4) {
                ForEach(1...3, id: \.self) { i in
                    Capsule().fill(i <= step ? Color.accentColor : Color.gray.opacity(0.3)).frame(height: 4)
                }
            }
            Text(hint).font(.caption2).foregroundColor(.secondary)
        }
        .padding(.horizontal, 16).padding(.vertical, 8)
    }
}
