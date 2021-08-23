package com.tanguymichardiere.sad_cat_project.admin

import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable

@Composable
fun AdminTopAppBar(close: () -> Unit) {
    TopAppBar({ Text("Admin") }, navigationIcon = {
        IconButton(onClick = close) {
            Icon(Icons.Filled.ArrowBack, "back")
        }
    })
}
