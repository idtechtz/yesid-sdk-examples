# Yesid SDK sample: iOS

A small SwiftUI app that scans an ID document and checks the face against its portrait, using the Yesid SDK. Copy the steps below into your own app.

## What you need
| Item | Version / note |
|---|---|
| Xcode | 15 or newer |
| iOS | 15.6 or newer |
| CocoaPods | 1.15 or newer (`brew install cocoapods`) |
| XcodeGen | only to regenerate the project from `project.yml` (`brew install xcodegen`); the generated project is included |
| **A physical iPhone** | see the simulator note below |
| From Yesid | publishable key, consumer username, OCR and face licence keys, and a secret API key for your backend |

## 1. Add the pods (`Podfile`)
```ruby
source 'https://github.com/idtechtz/yesid-ios-pods-frameworks.git'   # the Yesid pods
source 'https://cdn.cocoapods.org/'

platform :ios, '15.6'
use_frameworks!

target 'YourApp' do
  pod 'YesidLicensing',          '0.7.0'
  pod 'YesidOCRCamera',          '0.7.0'
  pod 'YesidFaceAuthentication', '0.7.0'
end
```
Use the version in `../../docs/VERSION_MATRIX.md`. Then `pod install` and open the `.xcworkspace`. The `post_install` block in this sample's Podfile excludes arm64 for the simulator (ML Kit has no arm64 simulator slice).

## 2. Info.plist
`NSCameraUsageDescription` (scanner and liveness) and `NSPhotoLibraryUsageDescription` (choose a photo). The sample also sets `NSAllowsLocalNetworking` so it can reach the reference backend on your Mac over http; remove that in your app and use https.

## 3. Configure the SDK once (`YesidSampleApp.swift`)
```swift
SessionManager.shared.setup(apiKey: "", publishableKey: "...", consumerUsername: "...", environment: .sandbox)
```
The secret API key is not needed in the app (leave it empty); your backend uses it.

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

### Known limitation: no iOS simulator on current Xcode
The Yesid pods bundle ML Kit 3.2.0, which ships no arm64 simulator slice, and recent iOS simulator runtimes on Apple-silicon Macs no longer run x86_64 apps. Until the SDK moves to a newer ML Kit, test on a physical iPhone.
