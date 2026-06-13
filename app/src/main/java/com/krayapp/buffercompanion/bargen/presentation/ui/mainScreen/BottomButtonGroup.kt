package com.krayapp.buffercompanion.bargen.presentation.ui.mainScreen

import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.SplitButtonDefaults
import androidx.compose.material3.SplitButtonLayout
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.krayapp.buffercompanion.bargen.R
import com.krayapp.buffercompanion.bargen.presentation.ui.composables.SegmentButton
import com.krayapp.buffercompanion.bargen.presentation.ui.composables.SegmentButtonType
import com.krayapp.buffercompanion.bargen.presentation.utils.Space
import com.krayapp.buffercompanion.bargen.theme.defaultAnimationDuration
import com.krayapp.buffercompanion.bargen.theme.sSize

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun BottomButtonGroup(
    modifier: Modifier = Modifier,
    hide : Boolean ,
    onImportFromGallery: () -> Unit,
    onScanClicked: () -> Unit = {},
    onCreateClicked: () -> Unit = {},
    onTagsClicked: () -> Unit = {},
) {
    val offsetState = animateIntAsState(
        targetValue = if (hide) 500 else 0,
        animationSpec = tween(defaultAnimationDuration)
    )
    Row(
        modifier = modifier
            .wrapContentWidth()
            .offset {
                IntOffset(y = offsetState.value, x = 0)
            },
        verticalAlignment = Alignment.CenterVertically) {

        SegmentButton(
            items = listOf(
                SegmentButtonType.FROM_GALLERY, SegmentButtonType.CREATE_RAW,
                SegmentButtonType.SCAN
            )
        ) {
            when(it) {
                SegmentButtonType.CREATE_RAW -> onCreateClicked()
                SegmentButtonType.SCAN -> onScanClicked()
                SegmentButtonType.FROM_GALLERY -> onImportFromGallery()
            }
        }

        Space(sSize)
        Button(
            modifier = Modifier
                .wrapContentWidth()
                .padding(horizontal = 1.dp),
            onClick = {
                onTagsClicked()
            }

        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    modifier = Modifier.padding(end = 4.dp),
                    painter = painterResource(R.drawable.ic_label),
                    contentDescription = stringResource(R.string.tags),
                )
                Text(
                    text = stringResource(R.string.tags),
                )
            }
        }
    }


}