package com.tanguymichardiere.sad_cat_project.common

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@ExperimentalFoundationApi
@Composable
fun StaggeredVerticalGrid(
    cells: GridCells,
    modifier: Modifier = Modifier,
    gap: Gap = Gap(0.dp),
    content: @Composable () -> Unit
) {
    when (cells) {
        is GridCells.Fixed ->
            FixedStaggeredVerticalGrid(
                nColumns = cells.count,
                modifier = modifier.verticalScroll(rememberScrollState()),
                gap = gap,
                content = content
            )
        is GridCells.Adaptive ->
            BoxWithConstraints(
                modifier = modifier.verticalScroll(rememberScrollState())
            ) {
                val nColumns = maxOf((maxWidth / cells.minSize).toInt(), 1)
                FixedStaggeredVerticalGrid(
                    nColumns = nColumns,
                    gap = gap,
                    content = content
                )
            }
    }
}

data class Gap(val vertical: Dp, val horizontal: Dp) {
    constructor(both: Dp) : this(both, both)
}

@Composable
private fun FixedStaggeredVerticalGrid(
    nColumns: Int,
    modifier: Modifier = Modifier,
    gap: Gap,
    content: @Composable () -> Unit
) {
    check(nColumns > 0) {
        "nColumns must be > 0"
    }
    val verticalGap = with(LocalDensity.current) { gap.vertical.roundToPx() }
    val horizontalGap = with(LocalDensity.current) { gap.horizontal.roundToPx() }
    Layout(
        content = content,
        modifier = modifier
    ) { measurables, constraints ->
        check(constraints.hasBoundedWidth) {
            "Unbounded width not supported"
        }
        val columnWidth =
            (constraints.maxWidth - horizontalGap * (nColumns - 1)) / nColumns
        val itemConstraints = constraints.copy(maxWidth = columnWidth)
        val colHeights = IntArray(nColumns) { 0 }
        val placeables = measurables.map { measurable ->
//            colHeights.size > 0 (see check above)
            val column = colHeights.withIndex().minByOrNull { (_, colHeight) -> colHeight }!!.index
            val placeable = measurable.measure(itemConstraints)
            colHeights[column] += placeable.height + verticalGap
            placeable
        }
        for (i in colHeights.indices) {
            colHeights[i] -= verticalGap
        }
//        colHeights.size > 0 (see check above)
        val height = colHeights.maxOrNull()!!.coerceIn(constraints.minHeight, constraints.maxHeight)
        layout(
            width = constraints.maxWidth,
            height = height
        ) {
            val colX = IntArray(nColumns) {
                (columnWidth + horizontalGap) * it
            }
            val colY = IntArray(nColumns) { 0 }
            placeables.forEach { placeable ->
//                colY.size > 0 (see check above)
                val column = colY.withIndex().minByOrNull { (_, colHeight) -> colHeight }!!.index
                placeable.place(
                    x = colX[column],
                    y = colY[column]
                )
                colY[column] += placeable.height + verticalGap
            }
        }
    }
}
