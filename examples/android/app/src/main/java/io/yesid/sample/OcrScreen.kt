package io.yesid.sample

import android.graphics.Bitmap
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.yesid.card_detection.ocr.YesidOCRCamera
import com.yesid.card_detection.ocr.presentation.OCRConfigurationBuilder
import com.yesid.card_detection.ocr.presentation.OCR_MODE

/**
 * Document scan. The SDK shows its own camera UI (camera, upload and manual modes, guidance, errors).
 * You give it the licence key and the session id and receive the result or a terminal state.
 */
@Composable
internal fun OcrScreen(
    sessionId: String,
    upload: Boolean,
    onDone: (text: Map<String, String>, portrait: Bitmap?) -> Unit,
    onNeedNewSession: () -> Unit,
) {
    // Rebuild the configuration only when the session changes (the builder is a singleton; re-applying it every recomposition
    // with an empty id would wipe a good one).
    val configuration = remember(sessionId, upload) {
        OCRConfigurationBuilder
            .setUserLicense(Config.ocrLicense)
            .setSessionID(sessionId)
            .setUseDetectCardOnly(true)
            // camera = live scan, upload = pick a photo from the gallery
            .setOCRMode(if (upload) OCR_MODE.upload else OCR_MODE.camera)
    }
    Box(Modifier.fillMaxSize()) {
        YesidOCRCamera.OCRCamera(
            configurationBuilder = configuration,
            onResults = { response -> onDone(response.documentText, response.documentImages["portrait"]) },
            // The SDK ended this session without a result and says a NEW one is needed (completed, attempts used, expired).
            onTerminal = { terminal -> if (!terminal.sessionReusable) onNeedNewSession() },
        )
    }
}
