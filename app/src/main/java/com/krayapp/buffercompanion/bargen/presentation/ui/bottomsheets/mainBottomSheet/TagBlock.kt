package com.krayapp.buffercompanion.bargen.presentation.ui.bottomsheets.mainBottomSheet

import android.util.Log
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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
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
            Log.d("FATA", String.format("%s", applyingTagsState))
            applyingTagsState.forEach {
                BargenChip(it)
            }
        }

        if (foundTagsState.isNotEmpty()) {
            Text(
                text = stringResource(R.string.similar_tags),
                style = MaterialTheme.typography.labelMedium
            )
            //превью
            FlowRow(
                modifier = Modifier.minimumInteractiveComponentSize(),
                horizontalArrangement = Arrangement.Start,
            ) {
                foundTagsState.forEach {
                    BargenChip(onClick = {
                        val allText = tagsTextFieldState.text.toString()
                        val forRemove = allText.substringAfterLast(',')
                        Log.d("FATA", String.format("%s", forRemove))
                        val newText = allText.replace(forRemove, it.name)
                        tagsTextFieldState.setTextAndPlaceCursorAtEnd(newText)
                    }, model = it)
                }
            }
        }
    }
}

