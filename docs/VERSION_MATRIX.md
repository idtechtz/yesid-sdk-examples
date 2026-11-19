# Version matrix

A row is added only after the clean-room run for that version passed (see VERIFIED.md).

| SDK version | Android sample | iOS sample | Verified on | Notes |
|---|---|---|---|---|
| 0.7.2 (iOS only) | 1.0 (not re-run for 0.7.2; sample edit uncompiled) | 1.1 (built from the published pods and run end to end on an iPhone 15 Pro Max: document, face check, server similarity) | 2026-11-19 | iOS pods `YesidLicensing`, `YesidOCRCamera`, `YesidFaceAuthentication`, `YesidFaceEnrollment` 0.7.2. Face match comes from the Yes ID server; document and face check must share one session |
| 0.7.1 (iOS only) | 1.0 | 1.0 (builds from the published pods, with and without Firebase; not yet run with Firebase on a device) | 2026-10-21 | iOS pods `YesidLicensing`, `YesidOCRCamera`, `YesidFaceAuthentication`, `YesidFaceEnrollment` 0.7.1 |
| 0.7.0 | 1.0 | 1.0 (builds; not yet run on a device) | 2026-10-15 | Android modules `licensing`, `forensics`, `card_detection`, `face` live on maven.yesid.io; iOS pods `YesidLicensing`, `YesidOCRCamera`, `YesidFaceAuthentication` 0.7.0 on the idtechtz/yesid-ios-pods-frameworks spec repo |
