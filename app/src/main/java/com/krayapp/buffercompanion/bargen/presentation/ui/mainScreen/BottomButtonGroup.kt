package com.krayapp.buffercompanion.bargen.presentation.screens.mainScreen

import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SplitButtonDefaults
import androidx.compose.material3.SplitButtonLayout
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.krayapp.buffercompanion.bargen.R
import com.krayapp.buffercompanion.bargen.theme.defaultAnimationDuration
import com.krayapp.buffercompanion.bargen.theme.mSize
import com.krayapp.buffercompanion.bargen.theme.sSize
import com.krayapp.buffercompanion.bargen.presentation.utils.Space

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun BottomButtonGroup(
    modifier: Modifier = Modifier,
    hideState: Boolean = false,
    onScanClicked: () -> Unit = {},
    onCreateClicked: () -> Unit = {},
    onTagsClicked: () -> Unit = {},
) {

    val offsetState = animateIntAsState(
        if (hideState) 200 else 0,
        animationSpec = tween(defaultAnimationDuration)
    )
    Row(
        horizontalArrangement = Arrangement.Center,
        modifier = modifier
            .wrapContentWidth()
            .offset {
                IntOffset(y = offsetState.value, x = 0)
            }
            .padding(bottom = mSize)
    ) {

        SplitButtonLayout(leadingButton = {
            SplitButtonDefaults.LeadingButton(onClick = onCreateClicked) {
                Icon(
                    ImageVector.vectorResource(R.drawable.ic_plus),
                    modifier = Modifier.size(SplitButtonDefaults.LeadingIconSize),
                    contentDescription = "Add new",
                )
                Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                Text(stringResource(R.string.create))
            }
        }, trailingButton = {
            SplitButtonDefaults.TrailingButton(onClick = onScanClicked) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.ic_camera),
                    modifier =
                        Modifier.size(SplitButtonDefaults.TrailingIconSize),
                    contentDescription = "Localized description",
                )
            }
        })

        Space(width = sSize)
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
                    imageVector = ImageVector.vectorResource(R.drawable.ic_label),
                    contentDescription = stringResource(R.string.tags),
                )
                Text(
                    text = stringResource(R.string.tags),
                )
            }
        }
    }
}