package com.aaron.basemvvmlibrary2.net

import androidx.lifecycle.LiveData
import retrofit2.CallAdapter
import retrofit2.Retrofit
import java.lang.reflect.Type
import retrofit2.CallAdapter.Factory
import java.lang.reflect.ParameterizedType

class LiveDataCallAdapterFactory : Factory() {
    override fun get(returnType: Type, annotations: Array<Annotation>, retrofit: Retrofit): CallAdapter<*, *>? {
        if (getRawType(returnType) != LiveData::class.java) return null
        //获取第一个泛型类型
        val observableType = getParameterUpperBound(0, returnType as ParameterizedType)
        val rawType = getRawType(observableType)
        if (rawType != ApiResponse::class.java) {
            throw IllegalArgumentException("type must be ApiResponse")
        }
        if (observableType !is ParameterizedType) {
            throw IllegalArgumentException("resource must be parameterized")
        }
        return LiveDataCallAdapter<Any>(observableType)
    }
}