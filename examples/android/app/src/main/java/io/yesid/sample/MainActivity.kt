package io.yesid.sample

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import io.yesid.licensing.domain.YesidSessionManager
import kotlinx.coroutines.launch

/** Where the sample is: a tiny state machine so the integration code stays easy to read. */
private sealed interface Step {
    data object Home : Step
    data object Setup : Step
    data class Ocr(val upload: Boolean) : Step
    data class OcrDone(val text: Map<String, String>, val portrait: Bitmap?) : Step
    data class Face(val portrait: Bitmap?) : Step
    data class FaceDone(val summary: Map<String, String>, val portrait: Bitmap?) : Step
    data class Error(val message: String) : Step
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 1. Tell the SDK who you are. Idempotent: safe to call again.
        if (Config.missing().isEmpty()) {
            YesidSessionManager.configure(
                publishableKey = Config.publishableKey,
                environment = Config.environment,
                consumerUsername = Config.consumerUsername,
            )
        }
        setContent { MaterialTheme { Surface(Modifier.fillMaxSize()) { SampleApp() } } }
    }
}

@Composable
private fun SampleApp() {
    val missing = remember { Config.missing() }
    if (missing.isNotEmpty()) {
        Centered {
            Text("Configuration missing", style = MaterialTheme.typography.headlineSmall)
            Text("Copy Config.example to Config.local and fill in: ${missing.joinToString()}. Then rebuild.")
        }
        return
    }

    var step: Step by remember { mutableStateOf(Step.Home) }
    var sessionId by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    val context = androidx.compose.ui.platform.LocalContext.current
    var cameraGranted by remember { mutableStateOf(ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) }
    val askCamera = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { cameraGranted = it }

    // 2. A session per run, created by YOUR backend.
    fun startWithNewSession(next: Step) {
        scope.launch {
            when (val r = SessionApi.create()) {
                is SessionApi.Result.Ok -> { sessionId = r.sessionId; step = next }
                is SessionApi.Result.Failed -> step = Step.Error(r.message)
            }
        }
    }

    when (val s = step) {
        Step.Home -> Centered {
            Text("Yesid SDK sample", style = MaterialTheme.typography.headlineSmall)
            Text(BuildConfig.ENVIRONMENT.replaceFirstChar { it.uppercase() }, style = MaterialTheme.typography.labelLarge)
            if (!cameraGranted) {
                Text("The camera permission is needed for scanning and liveness.")
                Button(onClick = { askCamera.launch(Manifest.permission.CAMERA) }) { Text("Allow camera") }
            }
            Button(enabled = cameraGranted, onClick = { startWithNewSession(Step.Ocr(upload = false)) }) { Text("Full verification (recommended)") }
            Button(onClick = { startWithNewSession(Step.Ocr(upload = true)) }) { Text("Use a photo from the gallery") }
            Button(onClick = { step = Step.Setup }) { Text("Setup check") }
        }
        Step.Setup -> SetupCheckScreen(onBack = { step = Step.Home })
        is Step.Ocr -> Column {
            StepHeader(1, "Document", "SDK call: YesidOCRCamera.OCRCamera(...) in OcrScreen.kt")
            OcrScreen(
                sessionId = sessionId,
                upload = s.upload,
                onDone = { text, portrait -> step = Step.OcrDone(text, portrait) },
                onNeedNewSession = { startWithNewSession(Step.Ocr(s.upload)) },
            )
        }
        is Step.OcrDone -> Column {
            StepHeader(2, "Review", "Fields: response.documentText. Portrait: response.documentImages[\"portrait\"] (OcrScreen.kt)")
            ResultScreen(
                title = "Document scanned",
                fields = s.text,
                image = s.portrait,
                primary = "Continue to face check" to { startWithNewSession(Step.Face(s.portrait)) },
                secondary = "Start over" to { step = Step.Home },
            )
        }
        is Step.Face -> Column {
            StepHeader(3, "Face check", "SDK call: FaceAuthenticationCamera.FaceAuthCamera(...) in FaceScreen.kt")
            FaceScreen(
                sessionId = sessionId,
                portrait = s.portrait,
                onDone = { summary -> step = Step.FaceDone(summary, s.portrait) },
                onNeedNewSession = { startWithNewSession(Step.Face(s.portrait)) },
            )
        }
        is Step.FaceDone -> Column {
            StepHeader(3, "Result", "faceSimilarity comes from onMatchResultsFirstImage (FaceScreen.kt)")
            ResultScreen(
                title = "Verification complete",
                fields = s.summary,
                image = s.portrait,
                primary = "Start a new verification" to { startWithNewSession(Step.Ocr(upload = false)) },
                secondary = "Home" to { step = Step.Home },
            )
        }
        is Step.Error -> Centered {
            Text("Something went wrong", style = MaterialTheme.typography.headlineSmall)
            Text(s.message)
            Button(onClick = { step = Step.Home }) { Text("Back") }
        }
    }
}

@Composable
internal fun Centered(content: @Composable () -> Unit) {
    Column(Modifier.fillMaxSize().padding(24.dp), verticalArrangement = Arrangement.spacedBy(12.dp, androidx.compose.ui.Alignment.CenterVertically)) { content() }
}
