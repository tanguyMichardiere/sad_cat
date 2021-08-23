package com.tanguymichardiere.sad_cat_project.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.edit
import com.tanguymichardiere.sad_cat_project.R
import com.tanguymichardiere.sad_cat_project.REVERSE_ORDER
import com.tanguymichardiere.sad_cat_project.dataStore
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

@Composable
fun OrderSetting() {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val reverseOrder = context.dataStore.data.map { preferences ->
        preferences[REVERSE_ORDER] ?: false
    }.collectAsState(initial = false).value

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                coroutineScope.launch {
                    context.dataStore.edit { preferences ->
                        preferences[REVERSE_ORDER] = !(preferences[REVERSE_ORDER] ?: false)
                    }
                }
            }
            .padding(16.dp)
    ) {
        Column {
            Text(stringResource(R.string.order), fontWeight = FontWeight.Bold)

            Text(
                if (reverseOrder) stringResource(R.string.newest_first) else stringResource(R.string.oldest_first),
                modifier = Modifier.alpha(0.8F)
            )
        }
    }
}
