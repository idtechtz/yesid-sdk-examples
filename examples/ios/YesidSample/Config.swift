import Foundation

/// Settings from `Config.plist` (copy `Config.example.plist`). Missing file or empty values show a readable screen.
enum Config {
    private static let values: [String: String] = {
        guard let url = Bundle.main.url(forResource: "Config", withExtension: "plist"),
              let dict = NSDictionary(contentsOf: url) as? [String: String] else { return [:] }
        return dict
    }()

    static var publishableKey: String { values["PUBLISHABLE_KEY"] ?? "" }
    static var consumerUsername: String { values["CONSUMER_USERNAME"] ?? "" }
    static var environment: String { values["ENVIRONMENT"] ?? "sandbox" }
    static var sessionURL: String { values["SESSION_URL"] ?? "" }
    /// Host and port of SESSION_URL only (safe to show on screen), for the setup check.
    static var sessionURLHost: String {
        guard let url = URL(string: sessionURL), let host = url.host else { return "SESSION_URL" }
        return url.port.map { "\(host):\($0)" } ?? host
    }
    static var ocrLicense: String { values["OCR_LICENSE"] ?? "" }
    static var faceLicense: String { values["FACE_LICENSE"] ?? "" }

    /// Names of the settings that are still empty; empty = ready to run.
    static func missing() -> [String] {
        var m: [String] = []
        if publishableKey.isEmpty { m.append("PUBLISHABLE_KEY") }
        if consumerUsername.isEmpty { m.append("CONSUMER_USERNAME") }
        if sessionURL.isEmpty { m.append("SESSION_URL") }
        if ocrLicense.isEmpty { m.append("OCR_LICENSE") }
        if faceLicense.isEmpty { m.append("FACE_LICENSE") }
        return m
    }
}
