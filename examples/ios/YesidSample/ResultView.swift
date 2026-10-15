import SwiftUI

/// Shows what the SDK returned. Field names are the SDK's; render the ones your product needs.
struct ResultView: View {
    let title: String
    let fields: [String: String]
    let image: UIImage?
    let primary: (String, () -> Void)
    let secondary: (String, () -> Void)

    var body: some View {
        ScrollView {
            VStack(alignment: .leading, spacing: 10) {
                Text(title).font(.title2).bold()
                if let image { Image(uiImage: image).resizable().scaledToFit().frame(height: 180) }
                ForEach(fields.keys.sorted(), id: \.self) { k in
                    HStack { Text(k); Spacer(); Text(fields[k] ?? "").multilineTextAlignment(.trailing) }.font(.subheadline)
                }
                Button(primary.0, action: primary.1).buttonStyle(.borderedProminent)
                Button(secondary.0, action: secondary.1).buttonStyle(.bordered)
            }.padding(24)
        }
    }
}
