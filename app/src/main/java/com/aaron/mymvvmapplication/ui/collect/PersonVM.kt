package com.aaron.mymvvmapplication.ui.collect

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.aaron.basemvvmlibrary2.base_mvvm.viewmodel.BaseViewModel
import com.aaron.basemvvmlibrary2.bean.NoStickyLiveData

class PersonVM(application : Application) : BaseViewModel(application) {
    val name = NoStickyLiveData<String>()

    init {
        name.value = "Aaron"
    }
}