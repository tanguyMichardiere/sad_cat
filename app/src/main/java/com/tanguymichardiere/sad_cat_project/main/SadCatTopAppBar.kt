package com.tanguymichardiere.sad_cat_project.main

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.edit
import androidx.navigation.NavController
import com.tanguymichardiere.sad_cat_project.BuildConfig
import com.tanguymichardiere.sad_cat_project.R
import com.tanguymichardiere.sad_cat_project.REVERSE_ORDER
import com.tanguymichardiere.sad_cat_project.dataStore
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

@ExperimentalMaterialApi
@Composable
fun SadCatTopAppBar(navController: NavController, pendingSize: Int? = null) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val reverseOrder = context.dataStore.data.map { preferences ->
        preferences[REVERSE_ORDER] ?: false
    }.collectAsState(initial = false).value

    var dropdownMenuExpanded by remember { mutableStateOf(false) }
    var displayAboutDialog by remember { mutableStateOf(false) }

    if (displayAboutDialog) {
        AlertDialog(
            onDismissRequest = { displayAboutDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(R.mipmap.ic_launcher_foreground),
                        contentDescription = "app icon",
                        modifier = Modifier
                            .height(Dp(32.sp.value))
                    )
                    Text("${stringResource(R.string.app_name)} - ${stringResource(R.string.versionName)}")
                }
            },
            text = { Text(stringResource(R.string.copyright)) },
            buttons = {
                Row(
                    modifier = Modifier
                        .padding(all = 8.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(
                        onClick = { displayAboutDialog = false }
                    ) {
                        Text(stringResource(R.string.dismiss))
                    }
                }
            })
    }

    TopAppBar(title = { Text(stringResource(R.string.app_name)) }, actions = {
        IconToggleButton(checked = reverseOrder, onCheckedChange = {
            coroutineScope.launch {
                context.dataStore.edit { preferences ->
                    preferences[REVERSE_ORDER] = it
                }
            }
        }) {
            Icon(
                if (reverseOrder) Icons.Filled.ArrowUpward else Icons.Filled.ArrowDownward,
                "reverse order"
            )
        }

        if (BuildConfig.FLAVOR == "admin") {
            IconButton(onClick = { navController.navigate("admin") }) {
                if (pendingSize != null && pendingSize > 0) {
                    BadgeBox(badgeContent = { Text("$pendingSize", color = Color.White) }) {
                        Icon(Icons.Filled.Settings, "admin")
                    }
                } else {
                    Icon(Icons.Filled.Settings, "admin")
                }
            }
        }

        Box {
            IconButton(onClick = { dropdownMenuExpanded = true }) {
                Icon(Icons.Filled.MoreVert, "menu")
            }

            DropdownMenu(
                expanded = dropdownMenuExpanded,
                onDismissRequest = { dropdownMenuExpanded = false }
            ) {
                DropdownMenuItem(onClick = {
                    dropdownMenuExpanded = false
                    navController.navigate("settings")
                }) {
                    Text(stringResource(R.string.settings))
                }

                DropdownMenuItem(onClick = {
                    dropdownMenuExpanded = false
                    displayAboutDialog = true
                }) {
                    Text(stringResource(R.string.about))
                }
            }
        }
    })
}
