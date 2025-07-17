package com.krayapp.buffercompanion.utils

import kotlin.reflect.full.memberProperties

inline fun <reified T : Any> T.toPropertiesString(): String {
    return this::class.memberProperties.joinToString(
        prefix = "${this::class.simpleName}(",
        postfix = ")"
    ) { property ->
        val value = property.getter.call(this)
        "${property.name}=$value"
    }
}

fun Collection<Any>?.toReadableString(): String {
    if (this == null)
        return ""
    val sb = StringBuilder()
    sb.append("[")
    forEach {
        sb.append("$it, ")
        sb.append("\n")
    }
    sb.append("]")
    return sb.toString()
}