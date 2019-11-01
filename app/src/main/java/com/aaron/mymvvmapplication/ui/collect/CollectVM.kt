package com.aaron.mymvvmapplication.ui.collect

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.aaron.basemvvmlibrary2.base_mvvm.viewmodel.BaseViewModel

class CollectVM(application : Application) : BaseViewModel(application) {
    val refreshTrigger = MutableLiveData<Boolean>()

    init {
        refreshTrigger.value = true
    }
}