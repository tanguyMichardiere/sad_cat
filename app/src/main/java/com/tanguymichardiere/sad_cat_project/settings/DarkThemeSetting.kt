package com.tanguymichardiere.sad_cat_project.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.edit
import com.tanguymichardiere.sad_cat_project.DARK_THEME
import com.tanguymichardiere.sad_cat_project.R
import com.tanguymichardiere.sad_cat_project.dataStore
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

@Composable
fun DarkThemeSetting() {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val darkTheme = context.dataStore.data.map { preferences ->
        preferences[DARK_THEME]
    }.collectAsState(initial = null).value

    var dropDownMenuExpanded by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { dropDownMenuExpanded = true }
            .padding(16.dp)
    ) {
        Column {
            Text(stringResource(R.string.theme), fontWeight = FontWeight.Bold)

            Text(
                when (darkTheme) {
                    null -> stringResource(R.string.system)
                    true -> stringResource(R.string.dark)
                    false -> stringResource(R.string.light)
                }, modifier = Modifier.alpha(0.8F)
            )
        }

        DropdownMenu(
            expanded = dropDownMenuExpanded,
            onDismissRequest = { dropDownMenuExpanded = false }
        ) {
            DropdownMenuItem(onClick = {
                dropDownMenuExpanded = false
                coroutineScope.launch {
                    context.dataStore.edit { preferences ->
                        preferences.remove(DARK_THEME)
                    }
                }
            }) {
                Text(stringResource(R.string.system))
            }

            DropdownMenuItem(onClick = {
                dropDownMenuExpanded = false
                coroutineScope.launch {
                    context.dataStore.edit { preferences ->
                        preferences[DARK_THEME] = true
                    }
                }
            }) {
                Text(stringResource(R.string.dark))
            }

            DropdownMenuItem(onClick = {
                dropDownMenuExpanded = false
                coroutineScope.launch {
                    context.dataStore.edit { preferences ->
                        preferences[DARK_THEME] = false
                    }
                }
            }) {
                Text(stringResource(R.string.light))
            }
        }
    }
}
