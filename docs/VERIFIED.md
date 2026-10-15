# Clean-room runs

| Date | Platform | SDK | Machine / setup | Steps | Result |
|---|---|---|---|---|---|
| 2026-10-15 | Android | 0.7.0 (published) | macOS, JDK 21, empty Gradle home, no local Maven, arm64 emulator (Pixel 9 Pro image), sandbox session from the reference backend | build from published artifacts (15 min cold); install; two-card fixture; single fixture | build OK; two-card fixture blocked ("Keep only one card in view"); single specimen card answered with the typed error "We couldn't read the document" (expected for a specimen) |

Not covered yet: the face check (needs a real device or a camera that shows a face), a release/R8 build, a second machine, iOS.
