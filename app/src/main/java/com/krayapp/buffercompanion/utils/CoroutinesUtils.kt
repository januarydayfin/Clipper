package com.krayman.toolbox

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


suspend fun <T> withIO(block: () -> T): T {
    return withContext(Dispatchers.IO) {
        block()
    }
}
suspend fun <T> withMain(block: () -> T): T {
    return withContext(Dispatchers.Main) {
        block()
    }
}

fun ViewModel.launchInIO(block: () -> Unit) {
    viewModelScope.launch(Dispatchers.IO) { block() }
}

