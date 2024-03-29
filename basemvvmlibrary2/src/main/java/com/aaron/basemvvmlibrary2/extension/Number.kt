package com.aaron.basemvvmlibrary2.extension

import android.content.Context

fun Float.dp2IntPx(context: Context): Int {
    return (this.dp2FloatPx(context) + 0.5f).toInt()
}

fun Float.dp2FloatPx(context: Context): Float {
    val scale = context.resources.displayMetrics.density
    return scale * this
}

fun Int.dp2IntPx(context: Context): Int {
    return this.toFloat().dp2IntPx(context)
}