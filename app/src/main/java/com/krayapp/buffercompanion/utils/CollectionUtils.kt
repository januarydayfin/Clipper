package com.krayman.toolbox

inline fun <E: Any, T: Collection<E>> T?.withNotNullOrEmpty(func: T.() -> Unit) {
    if (!this.isNullOrEmpty()) {
        with (this) { func() }
    }
}

inline fun  <E: Any, T: Collection<E>> T?.ifNotNullOrEmpty(func: (T) -> Unit) {
    if (!this.isNullOrEmpty()) {
        func(this)
    }
}