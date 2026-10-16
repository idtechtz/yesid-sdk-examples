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

    var body: some View {
        ScrollView {
            VStack(alignment: .leading, spacing: 10) {
                Text(title).font(.title2).bold()
                if let image { Image(uiImage: image).resizable().scaledToFit().frame(height: 180) }
                let keys = fields.keys.sorted()
                ForEach(keys.prefix(Self.headlineCount), id: \.self) { k in row(k) }
                if keys.count > Self.headlineCount {
                    // Same collapsible card the Yesid demo uses for its extra result metadata.
                    DisclosureGroup("More Highlights") {
                        VStack(spacing: 8) { ForEach(Array(keys.dropFirst(Self.headlineCount)), id: \.self) { k in row(k) } }
                            .padding(.top, 8)
                    }
                    .padding(12)
                    .background(RoundedRectangle(cornerRadius: 10).fill(Color(.secondarySystemBackground)))
                }
                Button(primary.0, action: primary.1).buttonStyle(.borderedProminent)
                Button(secondary.0, action: secondary.1).buttonStyle(.bordered)
            }.padding(24)
        }
    }
}
