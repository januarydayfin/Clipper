package com.krayapp.buffercompanion.bargen.presentation.ui.mainScreen

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import com.krayapp.buffercompanion.bargen.R
import com.krayapp.buffercompanion.bargen.domain.selector.tagSelector.TagSelector
import com.krayapp.buffercompanion.bargen.domain.usecase.tags.TagsUsecase
import com.krayapp.buffercompanion.bargen.presentation.mapper.toTagUiModel
import com.krayapp.buffercompanion.bargen.presentation.models.TagUiModel
import com.krayapp.buffercompanion.bargen.presentation.models.setChecked
import com.krayapp.buffercompanion.bargen.presentation.utils.BargenChip
import com.krayapp.buffercompanion.bargen.presentation.utils.Space
import com.krayapp.buffercompanion.bargen.theme.mSize
import com.krayapp.buffercompanion.bargen.theme.sSize
import com.krayapp.buffercompanion.bargen.utils.io
import kotlinx.coroutines.flow.collectLatest
import org.koin.compose.koinInject


@Composable
fun SelectedFilterTags() {
    val tagsUsecase: TagsUsecase = koinInject()
    val tagSelector: TagSelector = koinInject()
    val scope = rememberCoroutineScope()
    val tagsUi = remember { mutableStateListOf<TagUiModel>() }

    LaunchedEffect(Unit) {
        scope.io {
            tagSelector.tagsFilterFlow.collectLatest { filter ->
                val tagsWithId = tagsUsecase.loadTagsUiModelsByIds(filter)
                tagsUi.clear()
                tagsUi.addAll(tagsWithId.map { it.toTagUiModel() })
            }
        }
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
                Row {
                    Text(
                        modifier = Modifier
                            .padding(start = sSize, top = sSize)
                            .weight(1f),
                        text = stringResource(R.string.filter),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Icon(
                        painter = painterResource(R.drawable.outline_cancel_24),
                        contentDescription = null,
                        modifier = Modifier
                            .padding(all = sSize)
                            .clip(CircleShape)
                            .clickable {
                                scope.io {
                                    tagSelector.cleanSelection()
                                }
                            }
                    )

                }

            FlowRow(
                modifier = Modifier
                    .padding(horizontal = mSize)
                    .animateContentSize(tween(100)) { _, _ -> }) {
                tagsUi.forEach { model ->
                    BargenChip(model = model.setChecked(), onClick = {
                        scope.io {
                            tagSelector.checkTag(model.id)
                        }
                    })
                }
            }
        }


    }
    if (tagsUi.isNotEmpty())
        Space(height = mSize)


}
