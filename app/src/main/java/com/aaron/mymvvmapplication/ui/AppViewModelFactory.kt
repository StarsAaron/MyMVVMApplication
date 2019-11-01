package com.aaron.mymvvmapplication.ui

import android.annotation.SuppressLint
import android.app.Application
import androidx.annotation.VisibleForTesting
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.NewInstanceFactory
import com.aaron.mymvvmapplication.ui.collect.CollectVM
import com.aaron.mymvvmapplication.ui.collect.PersonVM

/**
 * 自定义工厂，返回定制的ViewModel
 */
class AppViewModelFactory private constructor(
    private val mApplication: Application
) : NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(CollectVM::class.java) -> CollectVM(mApplication) as T
            modelClass.isAssignableFrom(PersonVM::class.java) -> PersonVM(mApplication) as T
            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }
    }

    companion object {
        @SuppressLint("StaticFieldLeak")
        @Volatile
        private var INSTANCE: AppViewModelFactory? = null

        fun getInstance(application: Application): AppViewModelFactory? {
            if (INSTANCE == null) {
                synchronized(AppViewModelFactory::class.java) {
                    if (INSTANCE == null) {
                        INSTANCE = AppViewModelFactory(application)
                    }
                }
            }
            return INSTANCE
        }

        @VisibleForTesting
        fun destroyInstance() {
            INSTANCE = null
        }
    }

}