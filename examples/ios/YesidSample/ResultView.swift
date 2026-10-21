import SwiftUI

/// Shows what the SDK returned. Field names are the SDK's; render the ones your product needs.
struct ResultView: View {
    let title: String
    let fields: [String: String]
    let image: UIImage?
    let primary: (String, () -> Void)
    let secondary: (String, () -> Void)

    private static let headlineCount = 5

    private func row(_ k: String) -> some View {
        HStack { Text(k); Spacer(); Text(fields[k] ?? "").multilineTextAlignment(.trailing) }.font(.subheadline)
    }

    /// The Meta Data button: shown above the fields and again below them. Each one expands on its own.
    private func metaData(_ keys: [String]) -> some View {
        DisclosureGroup("See Meta Data") {
            VStack(spacing: 8) { ForEach(keys, id: \.self) { k in row(k) } }.padding(.top, 8)
        }
        .padding(12)
        .background(RoundedRectangle(cornerRadius: 10).fill(Color(.secondarySystemBackground)))
    }

    var body: some View {
        ScrollView {
            VStack(alignment: .leading, spacing: 10) {
                Text(title).font(.title2).bold()
                if let image { Image(uiImage: image).resizable().scaledToFit().frame(height: 180) }
                let keys = fields.keys.sorted()
                let extras = Array(keys.dropFirst(Self.headlineCount))
                if !extras.isEmpty { metaData(extras) }
                ForEach(keys.prefix(Self.headlineCount), id: \.self) { k in row(k) }
                Button(primary.0, action: primary.1).buttonStyle(.borderedProminent)
                Button(secondary.0, action: secondary.1).buttonStyle(.bordered)
            }.padding(24)
        }
    }
}
