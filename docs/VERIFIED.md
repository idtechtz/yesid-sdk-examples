# Clean-room runs

| Date | Platform | SDK | Machine / setup | Steps | Result |
|---|---|---|---|---|---|
| 2026-10-15 | Android | 0.7.0 (published) | macOS, JDK 21, empty Gradle home, no local Maven, arm64 emulator (Pixel 9 Pro image), sandbox session from the reference backend | build from published artifacts (15 min cold); install; two-card fixture; single fixture | build OK; two-card fixture blocked ("Keep only one card in view"); single specimen card answered with the typed error "We couldn't read the document" (expected for a specimen) |
| 2026-10-15 | iOS | 0.7.0 (published pods) | macOS, Xcode 27, `pod install --repo-update` from the published spec repo, device (arm64) build with signing off | install pods; build | pods install (3 Yesid pods plus ML Kit and TensorFlow Lite); build succeeded. NOT run: the simulator rejects the app (no arm64 simulator slice in ML Kit 3.2.0), so a physical iPhone run is still to do |

Not covered yet: the face check (needs a real device or a camera that shows a face), a release/R8 build, a second machine, the iOS app running on a physical iPhone.
| 2026-10-21 | iOS | 0.7.1 (published pods) | macOS, Xcode 27, `pod install --repo-update` from the published spec repo, Release device build, signing off | sample as is; sample + Firebase Crashlytics 10.29.0; sample + Firebase Crashlytics 12.19.0 at iOS 15.5 | all three BUILD SUCCEEDED. Resolved: ML Kit 6.0.0 / GoogleDataTransport 9.4.1 (as is), the same with Firebase 10.29.0 (one GoogleDataTransport), ML Kit 9.0.0 / MLKitCommon 14 / GoogleUtilities 8.1.5 / GoogleDataTransport 10.1.1 with Firebase 12.19.0. NOT run: launching or scanning with Firebase on a device (the demo app, which has no Firebase, was run on an iPhone 15 Pro Max) |
