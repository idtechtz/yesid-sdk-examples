package io.yesid.sample

import android.graphics.Bitmap
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.yesid.face.FaceAuthentication.FaceAuthenticationCamera
import com.yesid.face.FaceAuthentication.presentation.FaceAuthConfigurationBuilder

/**
 * Face liveness and match against the portrait from the document. The SDK shows the camera and the head-turn prompts; you
 * receive the match score(s) and, shortly after, an advisory capture assessment.
 */
@Composable
internal fun FaceScreen(
    sessionId: String,
    portrait: Bitmap?,
    onDone: (summary: Map<String, String>) -> Unit,
    onNeedNewSession: () -> Unit,
) {
    val configuration = remember(sessionId) {
        FaceAuthConfigurationBuilder
            .setUserLicense(Config.faceLicense)
            .setSessionID(sessionId)
    }
    var similarity by remember { mutableStateOf<Double?>(null) }
    var assessment by remember { mutableStateOf<String?>(null) }

    fun finish() {
        val s = similarity ?: return
        onDone(buildMap {
            put("faceSimilarity", "%.1f %%".format(s))
            assessment?.let { put("captureAssessment", it) }
        })
    }

    Box(Modifier.fillMaxSize()) {
        FaceAuthenticationCamera.FaceAuthCamera(
            configurationBuilder = configuration,
            firstImage = portrait,
            secondImage = portrait,
            onMatchResultsFirstImage = { result ->
                similarity = result.similarity
                finish()
            },
            onCaptureAssessment = { a ->
                assessment = a?.toString() ?: "no opinion"
                finish()
            },
            onRecaptureRequested = onNeedNewSession,
        )
    }
}
