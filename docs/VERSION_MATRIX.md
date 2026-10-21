# Version matrix

A row is added only after the clean-room run for that version passed (see VERIFIED.md).

| SDK version | Android sample | iOS sample | Verified on | Notes |
|---|---|---|---|---|
| 0.7.1 (iOS only) | 1.0 | 1.0 (not yet verified: iOS pods 0.7.1 are the 0.7.0 pods with minimum iOS 15.0 and `configure(publishableKey:consumerUsername:)`; Android stays on 0.7.0) | pending | iOS pods `YesidLicensing`, `YesidOCRCamera`, `YesidFaceAuthentication`, `YesidFaceEnrollment` 0.7.1 |
| 0.7.0 | 1.0 | 1.0 (builds; not yet run on a device) | 2026-10-15 | Android modules `licensing`, `forensics`, `card_detection`, `face` live on maven.yesid.io; iOS pods `YesidLicensing`, `YesidOCRCamera`, `YesidFaceAuthentication` 0.7.0 on the idtechtz/yesid-ios-pods-frameworks spec repo |
