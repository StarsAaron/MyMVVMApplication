package com.aaron.basemvvmlibrary2.net

import androidx.annotation.Keep

@Keep
data class ApiResponse<T>(
    var data: T?,
    var errorCode: Int,
    var errorMsg: String
)