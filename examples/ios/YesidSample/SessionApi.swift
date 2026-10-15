import Foundation

/// Asks YOUR backend for a session. The backend calls the Yesid API with the secret API key and returns {"sessionId": "..."}.
/// The secret key never belongs in the app. See ../../backend/server.js.
enum SessionApi {
    enum Failure: Error { case message(String) }

    static func create() async throws -> String {
        guard let url = URL(string: Config.sessionURL) else { throw Failure.message("SESSION_URL is not a valid URL") }
        var req = URLRequest(url: url, timeoutInterval: 30)
        req.httpMethod = "POST"
        req.setValue("application/json", forHTTPHeaderField: "Content-Type")
        req.httpBody = Data("{}".utf8)
        let (data, response) = try await URLSession.shared.data(for: req)
        guard let http = response as? HTTPURLResponse, (200..<300).contains(http.statusCode) else {
            throw Failure.message("Your session endpoint answered with an error")
        }
        guard let obj = try JSONSerialization.jsonObject(with: data) as? [String: Any],
              let id = obj["sessionId"] as? String, !id.isEmpty else {
            throw Failure.message("Your session endpoint did not return a sessionId")
        }
        return id
    }
}
