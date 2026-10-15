# Yesid SDK examples

Sample apps that show how to add the Yesid SDK (document scan, face liveness and face match) to your own app.

| Platform | Status | Folder |
|---|---|---|
| Android | ready, verified against SDK 0.7.0 | [`examples/android`](examples/android) |
| iOS | in preparation | `examples/ios` |

## Start here (Android)
1. Read [`examples/android/README.md`](examples/android/README.md): requirements, the five integration steps, troubleshooting.
2. Get your keys from Yesid (publishable key, consumer username, OCR and face licence keys, and a secret API key for your backend).
3. Run the reference backend in [`examples/backend`](examples/backend) so your app can get a session.
4. Copy `examples/android/Config.example` to `Config.local`, fill it in, and run the app.

The SDK is public on Yesid's Maven repository, no credentials needed:
```
https://maven.yesid.io/releases
io.yesid:licensing:0.7.0   io.yesid:card_detection:0.7.0   io.yesid:face:0.7.0
```
Two more repositories are required by the SDK's dependencies (Regula); the Android README lists all of them.

## What is verified
See [`docs/VERIFIED.md`](docs/VERIFIED.md) and [`docs/VERSION_MATRIX.md`](docs/VERSION_MATRIX.md). Not yet covered: the face check on a real device, a release (R8) build, iOS.

## Fixtures
`fixtures/` holds synthetic specimen cards (no real person, no real number) for testing the upload flow. The server is not expected to accept a specimen; you should see a clear error, not a crash.

## Support
Contact your Yesid account manager.
