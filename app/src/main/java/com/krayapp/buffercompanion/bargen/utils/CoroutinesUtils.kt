package com.krayapp.buffercompanion.bargen.utils

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


suspend fun <T> withIO(block: suspend () -> T): T {
    return withContext(Dispatchers.IO) {
        block()
    }
}

suspend fun <T> withMain(block: () -> T): T {
    return withContext(Dispatchers.Main) {
        block()
    }
}

fun ViewModel.launchInIO(block: suspend () -> Unit) {
    viewModelScope.launch(Dispatchers.IO) { block() }
}

fun CoroutineScope.io(block: suspend () -> Unit) {
    launch(Dispatchers.IO) { block() }
}