package io.yesid.sample

import android.Manifest
import android.content.pm.PackageManager
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.Column
import androidx.core.content.ContextCompat

private data class Check(val title: String, val ok: Boolean, val detail: String)

/** Confirms the three things that break a first run: configuration, your session endpoint, camera permission. */
@Composable
internal fun SetupCheckScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val rows = remember { mutableStateListOf<Check>() }
    var running by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        val missing = Config.missing()
        rows += Check("Configuration", missing.isEmpty(), if (missing.isEmpty()) "All keys are set." else "Fill in: ${missing.joinToString()}.")
        rows += when (val r = SessionApi.create()) {
            is SessionApi.Result.Ok -> Check("Session endpoint", true, "Your backend returned a session id.")
            is SessionApi.Result.Failed -> Check("Session endpoint", false, r.message)
        }
        val cam = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        rows += Check("Camera permission", cam, if (cam) "Granted." else "Tap Allow camera on the home screen.")
        running = false
    }

    Centered {
        Text("Setup check", style = MaterialTheme.typography.headlineSmall)
        rows.forEach {
            Row {
                Text(if (it.ok) "✓" else "✗", color = if (it.ok) Color(0xFF2E7D32) else Color(0xFFC62828), modifier = Modifier.padding(end = 10.dp))
                Column { Text(it.title, style = MaterialTheme.typography.titleSmall); Text(it.detail, style = MaterialTheme.typography.bodySmall) }
            }
        }
        if (running) CircularProgressIndicator()
        Button(onClick = onBack) { Text("Back") }
    }
}
