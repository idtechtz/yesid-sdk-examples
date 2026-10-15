# Yesid SDK sample: Android

A small Compose app that scans an ID document and checks the face against its portrait, using the Yesid SDK. Copy the steps below into your own app.

## What you need
| Item | Version / note |
|---|---|
| Android Studio | Hedgehog or newer (or the command line tools) |
| **JDK 17 or 21** | Do **not** use JDK 25 for the build: the Kotlin 2.2 build scripts fail with the one-word error `25` |
| Android SDK | compileSdk 36; set `ANDROID_HOME` (Android Studio does it for you via `local.properties`) |
| minSdk | 24 for this sample (the SDK itself supports 23) |
| Kotlin / AGP | 2.2.0 / 8.13.0 (the SDK needs at least Kotlin 2.0.0 and AGP 8.10.1) |
| Device | a real device or an arm64 emulator; the SDK ships arm64-v8a and armeabi-v7a libraries |
| From Yesid | publishable key, consumer username, OCR and face licence keys, and a secret API key for your backend |

## 1. Add the repositories (`settings.gradle.kts`)
```kotlin
google()
mavenCentral()
maven { url = uri("https://maven.yesid.io/releases") }                                  // io.yesid:* (public)
maven { url = uri("https://maven.regulaforensics.com/RegulaDocumentReader") }          // required by the SDK
maven { url = uri("https://maven.regulaforensics.com/RegulaDocumentReaderWebClient") } // required by the SDK
maven { url = uri("https://jitpack.io") }
```
Leaving out the two Regula lines fails dependency resolution with `Could not find com.regula...`.

## 2. Add the dependencies (`gradle/libs.versions.toml` and `app/build.gradle.kts`)
```kotlin
implementation("io.yesid:licensing:<version>")
implementation("io.yesid:card_detection:<version>")
implementation("io.yesid:face:<version>")
```
Use the version in `../../docs/VERSION_MATRIX.md`. Add `packaging { jniLibs { pickFirsts += "lib/**/libc++_shared.so" } }` and Java 11 desugaring (`isCoreLibraryDesugaringEnabled = true` plus `coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.1.5")`) as in `app/build.gradle.kts`.

## 3. Permissions (`AndroidManifest.xml`)
`INTERNET`, `ACCESS_NETWORK_STATE`, `CAMERA`. Ask for the camera at runtime before showing the scanner (see `MainActivity`).

## 4. Configure the SDK once
```kotlin
YesidSessionManager.configure(publishableKey = "...", environment = YesidEnvironment.SANDBOX, consumerUsername = "...")
```

## 5. Get a session from YOUR backend
The app never holds the secret API key. Your backend calls the Yesid API and returns a session id; the app passes it to the SDK. A 50-line reference backend is in `../backend` (`node server.js`).

## 6. Show the SDK screens
`OcrScreen.kt` (document scan, camera or gallery photo) and `FaceScreen.kt` (liveness and face match). Each takes the licence key and the session id and reports a result or a terminal state. `ResultScreen.kt` shows what came back.

## Run it
1. `cp Config.example Config.local` and fill it in (the file is git-ignored). `SESSION_URL` for an emulator is `http://10.0.2.2:8787/session`.
2. In `../backend`: `YESID_API_KEY=... YESID_CONSUMER_USERNAME=... node server.js`.
3. `export JAVA_HOME=<JDK 17 or 21>` and `./gradlew :app:installDebug`.
4. Open the app, allow the camera, tap **Scan an ID document** or **Use a photo from the gallery**.
Without `Config.local` the app shows a "Configuration missing" screen listing what to fill in.

## Troubleshooting
| Symptom | Cause | Fix |
|---|---|---|
| Build error: `What went wrong: 25` | JDK 25 | use JDK 17 or 21 |
| `SDK location not found` | no Android SDK path | set `ANDROID_HOME` or create `local.properties` with `sdk.dir=` |
| `Could not find com.regula...` | missing Regula repositories | add them (step 1) |
| "No internet connection" panel at start | the device or emulator has no validated network | fix the connection and restart the app |
| "Keep only one card in view" | more than one card in the picture | show one card |
| `We couldn't read the document` with the fixture images | the fixtures are specimens, not real documents | expected; use a real document |
| Android shows "This app isn't 16 KB compatible" on a 16 KB device | an old `libc++_shared.so` in the SDK dependencies | known issue, see below |

### Known issue: 16 KB page size
On devices with 16 KB pages (Android 15 and newer on some hardware, and the Pixel 9 emulator image) the system shows a compatibility warning. The only library affected in this build is `libc++_shared.so` (4 KB aligned). The app still runs. Google Play requires 16 KB support for new apps and updates, so this must be fixed in the SDK's native dependencies before store release.
