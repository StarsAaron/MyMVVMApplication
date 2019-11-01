package com.aaron.basemvvmlibrary2.extension

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager

/**
 * 隐藏软键盘
 */
fun View.hideKeyboard() {
    val inputMethodManager =
        context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(windowToken, 0)

}