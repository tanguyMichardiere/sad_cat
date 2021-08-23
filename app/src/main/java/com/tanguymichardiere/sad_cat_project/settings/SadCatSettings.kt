package com.tanguymichardiere.sad_cat_project.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.tanguymichardiere.sad_cat_project.R

@Composable
fun SadCatSettings(close: () -> Unit) {
    val systemUiController = rememberSystemUiController()
    val colors = MaterialTheme.colors

    LaunchedEffect(colors.isLight) {
        if (!colors.isLight) {
            systemUiController.setStatusBarColor(Color.Black)
        } else {
            systemUiController.setStatusBarColor(colors.primaryVariant)
        }
    }

    Scaffold(topBar = {
        TopAppBar({ Text(stringResource(R.string.settings)) }, navigationIcon = {
            IconButton(onClick = close) {
                Icon(Icons.Filled.ArrowBack, "back")
            }
        })
    }) {
        Column {
            DarkThemeSetting()

            Divider()

            OrderSetting()

            Divider()
        }
    }
}
