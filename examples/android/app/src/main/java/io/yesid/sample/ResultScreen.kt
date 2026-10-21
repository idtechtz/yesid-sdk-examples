package io.yesid.sample

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.clickable
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp

/** Shows what the SDK returned. Field names are the SDK's; render the ones your product needs. */
@Composable
internal fun ResultScreen(
    title: String,
    fields: Map<String, String>,
    image: Bitmap?,
    primary: Pair<String, () -> Unit>,
    secondary: Pair<String, () -> Unit>,
) {
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(24.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(title, style = MaterialTheme.typography.headlineSmall)
        image?.let { Image(it.asImageBitmap(), contentDescription = "Portrait", modifier = Modifier.height(180.dp)) }
        val entries = fields.entries.toList()
        val extras = entries.drop(HEADLINE_COUNT)
        if (extras.isNotEmpty()) MetaDataCard(extras)
        entries.take(HEADLINE_COUNT).forEach { FieldRow(it.key, it.value) }
        Button(onClick = primary.second, modifier = Modifier.fillMaxWidth()) { Text(primary.first) }
        OutlinedButton(onClick = secondary.second, modifier = Modifier.fillMaxWidth()) { Text(secondary.first) }
    }
}

private const val HEADLINE_COUNT = 5

@Composable
private fun FieldRow(k: String, v: String) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(k, style = MaterialTheme.typography.bodyMedium)
        Text(v, style = MaterialTheme.typography.bodyMedium)
    }
}

/** The Meta Data button: shown above the fields and again below them. Each one expands on its own. */
@Composable
private fun MetaDataCard(extras: List<Map.Entry<String, String>>) {
    var expanded by remember { mutableStateOf(false) }
    Card(Modifier.fillMaxWidth().clickable { expanded = !expanded }) {
        Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("See Meta Data", style = MaterialTheme.typography.titleSmall)
                Text(if (expanded) "▲" else "▼")
            }
            if (expanded) extras.forEach { FieldRow(it.key, it.value) }
        }
    }
}
