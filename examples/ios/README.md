# Yesid SDK sample: iOS

A small SwiftUI app that scans an ID document and checks the face against its portrait, using the Yesid SDK. Copy the steps below into your own app.

## What you need
| Item | Version / note |
|---|---|
| Xcode | 15 or newer |
| iOS | 15.0 or newer |
| CocoaPods | 1.15 or newer (`brew install cocoapods`) |
| XcodeGen | only to regenerate the project from `project.yml` (`brew install xcodegen`); the generated project is included |
| **A physical iPhone** | see the simulator note below |
| From Yesid | publishable key, consumer username, OCR and face licence keys, and a secret API key for your backend |

## 1. Add the pods (`Podfile`)
```ruby
source 'https://github.com/idtechtz/yesid-ios-pods-frameworks.git'   # the Yesid pods
source 'https://cdn.cocoapods.org/'

platform :ios, '15.0'
use_frameworks!

target 'YourApp' do
  pod 'YesidLicensing',          '0.7.1'
  pod 'YesidOCRCamera',          '0.7.1'
  pod 'YesidFaceAuthentication', '0.7.1'
end
```
Use the version in `../../docs/VERSION_MATRIX.md`. Then `pod install` and open the `.xcworkspace`. The `post_install` block in this sample's Podfile excludes arm64 for the simulator (ML Kit has no arm64 simulator slice).

## 2. Info.plist
`NSCameraUsageDescription` (scanner and liveness) and `NSPhotoLibraryUsageDescription` (choose a photo). The sample also sets `NSAllowsLocalNetworking` so it can reach the reference backend on your Mac over http; remove that in your app and use https.

## 3. Configure the SDK once (`YesidSampleApp.swift`)
```swift
SessionManager.shared.configure(publishableKey: "...", consumerUsername: "...", environment: .sandbox)
```
The secret API key is not needed in the app; your backend uses it to create the session and passes the sessionId to the app. (0.7.0 had no keyless setup: there, call `setup(apiKey: "", ...)`.)

## 4. Get a session from YOUR backend
Your backend calls the Yesid API and returns a session id; the app passes it to the SDK. A 50-line reference backend is in `../backend` (`node server.js`). See `SessionApi.swift`.

## 5. Show the SDK screens
`OcrView.swift` (document scan from the camera, or a photo from the library via `submitGalleryFront`) and `FaceView.swift` (liveness and face match). Each takes the licence key and the session id and reports a result or a terminal state. `ResultView.swift` shows what came back.

## Run it
1. `cp YesidSample/Config.example.plist YesidSample/Config.plist` and fill it in (git-ignored). `SESSION_URL` from a device on your network is your Mac's address, for example `http://192.168.1.10:8787/session`.
2. In `../backend`: `YESID_API_KEY=... YESID_CONSUMER_USERNAME=... node server.js`.
3. `pod install`, open `YesidSample.xcworkspace`, choose your iPhone, set your signing team, Run.
Without `Config.plist` the app shows a "Configuration missing" screen listing what to fill in.

## Troubleshooting
| Symptom | Cause | Fix |
|---|---|---|
| Simulator: "needs to be updated by the developer to work on this version of iOS" | the SDK's ML Kit dependency has no arm64 simulator slice and current simulators no longer run x86_64 apps | run on a physical iPhone (known limitation, see below) |
| `Unable to find a specification for 'YesidLicensing'` | the Yesid spec source line is missing | add the `source` line (step 1) and run `pod repo update` |
| "Configuration missing" screen | no `Config.plist` or empty values | fill it in and rebuild |
| Session error on the home screen | your backend is not running or not reachable from the phone | check `SESSION_URL` and that the phone and Mac are on the same network |
| "Keep only one card in view" | more than one card in the picture | show one card |
| Link error `Undefined symbols ... MLK...` or `framework 'X' not found` | the OCR and enrollment frameworks are STATIC (they reference ML Kit, they do not contain it), so your app must link the pods. A hard-coded `OTHER_LDFLAGS` without `$(inherited)` drops them | start your target's `OTHER_LDFLAGS` with `$(inherited)` and remove hand-written `-framework` entries for pods |
| Launch crash `unrecognized selector` in GoogleDataTransport / duplicate class warnings | an older SDK (0.7.0) embedded its own copy of Google libraries next to the one from Firebase | use 0.7.1 or later, which contains none |
| `CocoaPods could not find compatible versions for GoogleMLKit/...` | the app's minimum iOS is below what the ML Kit version required by your other pods needs (ML Kit 7+ needs iOS 15.5) | stay on ML Kit 6.x with iOS 15.0, or raise the app minimum to 15.5 |
| Document scan never starts / "document scanner is not set up correctly" | `YesidOCRResources.bundle` (the card-detection model) is not in the app | run `pod install` again; do not exclude pod resource bundles from your target |

### Simulator
The pods are built against ML Kit 6.0.x. Simulator support has not been re-verified with this version; the verified path is a physical iPhone. (On 0.7.0, ML Kit 3.2.0 had no arm64 simulator slice.)

### Using Firebase in the same app
From 0.7.1 the Yesid pods contain no Google libraries (checked on the binaries), so Firebase and Yesid resolve to one copy of GoogleDataTransport, GoogleUtilities and the rest. A sample app with Firebase running on a device is still to be recorded in `docs/VERIFIED.md`. Firebase 10.x resolves with ML Kit 6.x (iOS 15.0). Firebase 12 needs ML Kit 7 or later, and so iOS 15.5.

### Firebase added through Swift Package Manager (SPM) while Yesid and ML Kit come from CocoaPods
This mix can still crash at launch with `unrecognized selector` in `GDTCCTUploadOperation`, even on 0.7.1. The Yesid frameworks contain no Google code, but ML Kit's pods (`GoogleDataTransport`, `GoogleUtilities`, `FBLPromises`, `nanopb`) are then linked next to Firebase's SPM copies of the same libraries. Two copies, same crash.

A workaround reported by an integrator who ships this setup (we have not run it ourselves): in the Podfile, remove those four frameworks from the app's linker flags so ML Kit links against Firebase's SPM copies.

```ruby
post_integrate do |installer|
  spm_provided = %w[GoogleDataTransport GoogleUtilities FBLPromises nanopb]
  installer.aggregate_targets.each do |aggregate|
    aggregate.user_build_configurations.each_key do |config_name|
      path = aggregate.xcconfig_path(config_name)
      next unless File.exist?(path)
      contents = File.read(path)
      spm_provided.each { |name| contents.gsub!(" -framework \"#{name}\"", '') }
      File.write(path, contents)
    end
  end
end
```
Things to check if you use it: it edits the generated `Pods-*.xcconfig` files, so it must run after every `pod install` (the hook does that); confirm in the built app that `Frameworks/` holds only one copy of each library and that the console shows no duplicate-class warnings; keep the Firebase and ML Kit versions compatible (see the table above). Native SPM support for the Yesid SDK is being scoped; there is no date yet.
