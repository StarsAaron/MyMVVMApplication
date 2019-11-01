package com.aaron.basemvvmlibrary2.bean

import androidx.lifecycle.MutableLiveData

/**
 * 作者：Aaron
 * 时间：2019/9/28:18:22
 * 邮箱：
 * 说明：ViewModel跟View之间界面处理相关的LiveData
 */
class BaseUILiveDataEvent {
    // 一些界面相关的观察者
    private var showDialogEvent: MutableLiveData<DialogData>? = null
    private var dismissDialogEvent: MutableLiveData<Void>? = null
    private var startActivityEvent: MutableLiveData<Map<String, Any>>? = null
    private var startContainerActivityEvent: MutableLiveData<Map<String, Any>>? = null
    private var finishEvent: MutableLiveData<Void>? = null
    private var onBackPressedEvent: MutableLiveData<Void>? = null
    private var exitAppEvent: MutableLiveData<Boolean>? = null

    fun getShowDialogEvent(): MutableLiveData<DialogData> {
        if (showDialogEvent == null) {
            showDialogEvent = MutableLiveData()
        }
        return showDialogEvent as MutableLiveData<DialogData>
    }

    fun getDismissDialogEvent(): MutableLiveData<Void> {
        if (dismissDialogEvent == null) {
            dismissDialogEvent = MutableLiveData()
        }
        return dismissDialogEvent as MutableLiveData<Void>
    }

    fun getStartActivityEvent(): MutableLiveData<Map<String, Any>> {
        if (startActivityEvent == null) {
            startActivityEvent = MutableLiveData()
        }
        return startActivityEvent as MutableLiveData<Map<String, Any>>
    }

    fun getStartContainerActivityEvent(): MutableLiveData<Map<String, Any>> {
        if (startContainerActivityEvent == null) {
            startContainerActivityEvent = MutableLiveData()
        }
        return startContainerActivityEvent as MutableLiveData<Map<String, Any>>
    }

    fun getFinishEvent(): MutableLiveData<Void> {
        if (finishEvent == null) {
            finishEvent = MutableLiveData()
        }
        return finishEvent as MutableLiveData<Void>
    }

    fun getOnBackPressedEvent(): MutableLiveData<Void> {
        if (onBackPressedEvent == null) {
            onBackPressedEvent = MutableLiveData()
        }
        return onBackPressedEvent as MutableLiveData<Void>
    }

    fun getExitAppEvent(): MutableLiveData<Boolean> {
        if (exitAppEvent == null) {
            exitAppEvent = MutableLiveData()
        }
        return exitAppEvent as MutableLiveData<Boolean>
    }
}
