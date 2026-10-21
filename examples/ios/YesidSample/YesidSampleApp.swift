import SwiftUI
import YesidLicensingFramework

@main
struct YesidSampleApp: App {
    init() {
        // 1. Tell the SDK who you are. The secret API key is NOT needed in the app: your backend creates the session with it.
        if Config.missing().isEmpty {
            let env: YesidEnvironment = Config.environment.lowercased() == "production" ? .production : .sandbox
            SessionManager.shared.configure(publishableKey: Config.publishableKey,
                                            consumerUsername: Config.consumerUsername, environment: env)
        }
    }

    var body: some Scene {
        WindowGroup { RootView() }
    }
}
