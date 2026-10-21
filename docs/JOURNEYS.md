# Sample app journeys

Both samples (Android, iOS) follow the same screens and the same copy.

## Home
- **Full verification (recommended)**: document, review, face check, result.
- **Use a photo**: the same journey, starting from a photo instead of the camera.
- **Setup check**: confirms the configuration, the session endpoint and the camera permission.
- A badge shows the environment (Sandbox or Production).

## Setup check
Three rows, each with a tick or a cross and a one-line fix:
1. Configuration: every key is filled in.
2. Session endpoint: your backend answers and returns a session id.
3. Camera permission: granted.

## Main journey
1. Document (camera or photo).
2. Review: portrait and fields; "See Meta Data" holds the extra fields.
3. Face check.
4. Result.

## Progress and hints
Document, Review and Face check/Result show a "Step n of 3" strip and one line naming the SDK call the screen uses and the file it lives in.

## Not built yet
Error playground, developer panel.
