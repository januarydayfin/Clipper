package com.krayman.toolbox

import android.os.Build
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import java.io.Serializable

inline fun <reified ArgType : Serializable> Fragment.getArgs(): ArgType? {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
        arguments?.getSerializable(
            ArgType::class.java.simpleName,
            ArgType::class.java
        )
    else
        arguments?.get(ArgType::class.java.simpleName) as ArgType?
}

fun Fragment.runOnUi(block: suspend () -> Unit) {
    lifecycleScope.launch { block() }
}