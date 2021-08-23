package com.tanguymichardiere.sad_cat_project.main

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.firebase.storage.StorageReference
import com.tanguymichardiere.sad_cat_project.REVERSE_ORDER
import com.tanguymichardiere.sad_cat_project.common.Gap
import com.tanguymichardiere.sad_cat_project.common.StaggeredVerticalGrid
import com.tanguymichardiere.sad_cat_project.dataStore
import kotlinx.coroutines.flow.map

@ExperimentalFoundationApi
@Composable
fun SadCatGrid(storageReferences: List<StorageReference>?, preview: (StorageReference) -> Unit) {
    val context = LocalContext.current

    val reverseOrder = context.dataStore.data.map { preferences ->
        preferences[REVERSE_ORDER] ?: false
    }.collectAsState(initial = false).value

    if (storageReferences != null) {
//        Non-staggered implementation
//        LazyVerticalGrid(cells = GridCells.Adaptive(192.dp)) {
//            items((if (reverseOrder) viewModel.storageReferences?.reversed() else viewModel.storageReferences) ?: listOf()) { storageReference ->
//                Cat(
//                    storageReference = storageReference,
//                    preview = preview
//                )
//            }
//        }
//        Future lazy implementation
//        LazyStaggeredGrid(GridCells.Adaptive(192.dp), gap = Gap(2.dp)) {
//            items((if (reverseOrder) viewModel.storageReferences?.reversed() else viewModel.storageReferences) ?: listOf()) { storageReference ->
//                Cat(
//                    storageReference = storageReference,
//                    preview = preview
//                )
//            }
//        }
//        Non-lazy implementation
        StaggeredVerticalGrid(GridCells.Adaptive(192.dp), gap = Gap(2.dp)) {
            (if (reverseOrder) storageReferences.reversed() else storageReferences).forEach {
                SadCatCell(storageReference = it, preview = { preview(it) })
            }
        }
    } else {
        Box(modifier = Modifier.fillMaxSize()) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
    }
}
