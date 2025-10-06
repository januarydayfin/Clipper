package com.krayapp.buffercompanion.bargen.presentation.ui.bottomsheets.mainBottomSheet

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import com.krayapp.buffercompanion.bargen.R
import com.krayapp.buffercompanion.bargen.presentation.models.TagUiModel
import com.krayapp.buffercompanion.bargen.presentation.utils.BargenChip
import com.krayapp.buffercompanion.bargen.presentation.viewmodels.BargenViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun TagBlock(
    initialValue: List<TagUiModel> = emptyList(),
    viewModel: BargenViewModel,
    onScrollToBottom: () -> Unit = {},
    onTagsAdded: (List<TagUiModel>) -> Unit
) {
    val tagsTextFieldState =
        rememberTextFieldState(initialText = initialValue.joinToString { it.name })
    val scope = rememberCoroutineScope()
    val newTags = mutableListOf<TagUiModel>().toMutableStateList()
    val previewTags = mutableListOf<TagUiModel>().toMutableStateList()

    val nameList = tagsTextFieldState.text.splitRawTagsForNames()

    suspend fun foundTagsInDb(name: String) = viewModel.findTagWithName(name)

    SideEffect {
        scope.launch(Dispatchers.IO) {
            nameList.forEach { stringName ->
                if (stringName.isNotEmpty()) {
                    val foundTags = foundTagsInDb(stringName)
                    val exactTag =
                        runCatching { foundTags.first { stringName == it.name } }.getOrNull()
                    newTags.add(exactTag ?: TagUiModel(name = stringName))
                    previewTags.addAll(foundTags.filter { it !in newTags })
                    onTagsAdded(newTags.toList())
                    onScrollToBottom()
                }
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.Start
    ) {
        OutlinedTextField(
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            modifier = Modifier.fillMaxWidth(),
            state = tagsTextFieldState,
            label = {
                Text(text = stringResource(R.string.tags))
            })

        //добавляемые теги
        FlowRow(
            horizontalArrangement = Arrangement.Start,
            modifier = Modifier.minimumInteractiveComponentSize()
        ) {
            newTags.toList().forEach {
                BargenChip(it)
            }
        }

        Text(
            text = stringResource(R.string.found_tags),
            style = MaterialTheme.typography.labelMedium
        )
        //превью
        FlowRow(
            modifier = Modifier.minimumInteractiveComponentSize(),
            horizontalArrangement = Arrangement.Start,
        ) {
            previewTags.toList().distinct().forEach {
                BargenChip(onClick = {
                    val prevName = it.name
                    val substringed =
                        tagsTextFieldState.text.toString().removeSuffix(nameList.last())
                    tagsTextFieldState.setTextAndPlaceCursorAtEnd(substringed + prevName)
                }, model = it)
            }
        }
    }
}
private fun CharSequence.splitRawTagsForNames() = this.toString().split(",").map { it.trim() }
