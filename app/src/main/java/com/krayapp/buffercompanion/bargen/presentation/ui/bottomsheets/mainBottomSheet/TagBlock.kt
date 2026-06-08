package com.krayapp.buffercompanion.bargen.presentation.ui.bottomsheets.mainBottomSheet

import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.krayapp.buffercompanion.bargen.R
import com.krayapp.buffercompanion.bargen.presentation.models.TagUiModel
import com.krayapp.buffercompanion.bargen.presentation.utils.BargenChip
import com.krayapp.buffercompanion.bargen.presentation.utils.TagFounder
import org.koin.compose.koinInject

@Composable
fun TagBlock(
    initialValue: List<TagUiModel> = emptyList(),
    onTagsAdded: (List<TagUiModel>) -> Unit
) {
    val tagsFounder: TagFounder = koinInject()
    val tagsTextFieldState =
        rememberTextFieldState(initialText = initialValue.joinToString { it.name })
    val applyingTagsState by tagsFounder.applyingTags.collectAsState(emptyList())
    val foundTagsState by tagsFounder.existTags.collectAsState(emptyList())


    LaunchedEffect(tagsTextFieldState.text) {
        tagsFounder.findTags(tagsTextFieldState.text.toString())
        onTagsAdded(applyingTagsState)
    }

    DisposableEffect(Unit) {
        onDispose {
            tagsFounder.onDispose()
        }
    }
    Column(
        modifier = Modifier
            .fillMaxWidth(),
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
            modifier = Modifier
                .minimumInteractiveComponentSize()
                .heightIn(0.dp, 200.dp)
                .verticalScroll(rememberScrollState())
        ) {
            applyingTagsState.forEach {
                BargenChip(it)
            }
        }

        if (foundTagsState.isNotEmpty()) {
            Text(
                text = stringResource(R.string.similar_tags),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            //превью
            FlowRow(
                modifier = Modifier
                    .minimumInteractiveComponentSize()
                    .heightIn(0.dp, 200.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.Start,
            ) {
                foundTagsState.forEach {
                    BargenChip(onClick = {
                        val newString = buildString {
                            val allText = tagsTextFieldState.text.toString()
                            val forRemove = allText.substringAfterLast(',')
                            val removedText = allText.removeSuffix(forRemove)
                            append(removedText)
                            append(it.name)
                        }
                        tagsTextFieldState.setTextAndPlaceCursorAtEnd(newString)
                    }, model = it)
                }
            }
        }
    }
}

