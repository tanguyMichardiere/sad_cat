package com.tanguymichardiere.sad_cat_project.admin

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import com.google.firebase.storage.StorageReference
import com.tanguymichardiere.sad_cat_project.LocalViewModel
import com.tanguymichardiere.sad_cat_project.main.SadCatGrid

@ExperimentalFoundationApi
@Composable
fun AdminScaffold(close: () -> Unit, preview: (StorageReference) -> Unit) {
    val viewModel = LocalViewModel.current

    Scaffold(
        topBar = { AdminTopAppBar(close = close) }
    ) {
        SadCatGrid(storageReferences = viewModel.pendingStorageReferences, preview = preview)
    }
}
