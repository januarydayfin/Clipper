package com.krayapp.buffercompanion.bargen.presentation.screens.mainScreen

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.krayapp.buffercompanion.bargen.R
import com.krayapp.buffercompanion.bargen.presentation.BargenChip
import com.krayapp.buffercompanion.bargen.presentation.uiModels.TagUiModel
import com.krayapp.buffercompanion.bargen.presentation.uiModels.setChecked
import com.krayapp.buffercompanion.bargen.presentation.viewmodels.TagsViewModel
import com.krayapp.buffercompanion.bargen.theme.mSize
import com.krayapp.buffercompanion.bargen.theme.sSize
import com.krayapp.buffercompanion.bargen.utils.Space
import com.krayapp.buffercompanion.bargen.utils.io


@Composable
fun SelectedFilterTags() {
    val viewModel: TagsViewModel = viewModel()
    val tagFilterState = viewModel.tagSelector.tagsFilterFlow.collectAsState()
    val scope = rememberCoroutineScope()
    val tagsUi = remember { mutableStateListOf<TagUiModel>() }

    viewModel.loadTagsUiModelsByIds(tagFilterState.value) { input ->
        tagsUi.clear()
        tagsUi.addAll(input)
    }

    if (tagsUi.isNotEmpty())
        Space(height = mSize)

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = sSize)
            .clip(RoundedCornerShape(mSize)),
        color = MaterialTheme.colorScheme.surfaceContainer,
    ) {
        Column {
            if (tagsUi.isNotEmpty())
                Text(
                    modifier = Modifier.padding(start = sSize, top = sSize),
                    text = stringResource(R.string.filter),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
            FlowRow(
                modifier = Modifier
                    .padding(horizontal = mSize)
                    .animateContentSize(tween(100)) { _, _ -> }) {
                tagsUi.forEach { model ->
                    BargenChip(model = model.setChecked(), onClick = {
                        scope.io {
                            viewModel.tagSelector.checkTag(model.id)
                        }
                    })
                }
            }
        }
    }
    if (tagsUi.isNotEmpty())
        Space(height = mSize)


}
