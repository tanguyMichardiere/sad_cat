package com.tanguymichardiere.sad_cat_project.main

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Scaffold
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.google.firebase.storage.StorageReference
import com.tanguymichardiere.sad_cat_project.LocalViewModel

@ExperimentalMaterialApi
@ExperimentalFoundationApi
@Composable
fun SadCatScaffold(navController: NavController, preview: (StorageReference) -> Unit) {
    val viewModel = LocalViewModel.current

    val scaffoldState = rememberScaffoldState()

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            SadCatTopAppBar(
                navController = navController,
                pendingSize = viewModel.pendingStorageReferences?.size
            )
        },
        floatingActionButton = { SadCatFloatingActionButton(scaffoldState = scaffoldState) }
    ) {
        SadCatGrid(storageReferences = viewModel.storageReferences, preview = preview)
    }
}
