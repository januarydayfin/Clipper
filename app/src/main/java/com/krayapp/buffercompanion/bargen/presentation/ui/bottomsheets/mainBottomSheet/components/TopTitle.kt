package com.krayapp.buffercompanion.bargen.presentation.ui.bottomsheets.mainBottomSheet.components

import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun TopTitle(modifier: Modifier = Modifier, initialText: String, onTextChange: (String) -> Unit) {

    var textFieldValue by remember {
        mutableStateOf(TextFieldValue(initialText))
    }
    var isFocused by remember { mutableStateOf(false) }

    LaunchedEffect(isFocused) {
        if (isFocused) {
            textFieldValue = textFieldValue.copy(
                selection = TextRange(0, textFieldValue.text.length)
            )
        }
    }

    BasicTextField(
        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
        modifier = modifier
            .width(250.dp)
            .onFocusChanged { focus -> isFocused = focus.isFocused }
            .basicMarquee(),
        textStyle = typography.titleLarge.copy(
            fontWeight = FontWeight.Bold,
            color = colorScheme.onSurface,
            textAlign = TextAlign.Center
        ),
        value = textFieldValue,
        onValueChange = { newValue ->
            textFieldValue = newValue
            onTextChange(newValue.text)
        },
        cursorBrush = SolidColor(colorScheme.onSurface)
    )
}


@Preview(showBackground = true)
@Composable
private fun TopTitle_Preview() {
    MaterialTheme {
        TopTitle(modifier = Modifier, initialText = "Name", {})
    }
}