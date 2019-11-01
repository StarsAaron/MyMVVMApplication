package com.aaron.basemvvmlibrary2.base_mvvm.viewmodel

import android.app.Application
import android.os.Bundle
import android.view.View

import androidx.annotation.MainThread
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner

import com.aaron.basemvvmlibrary2.base_mvvm.IBaseViewModel
import com.aaron.basemvvmlibrary2.bean.BaseUILiveDataEvent
import com.aaron.basemvvmlibrary2.bean.DialogData

import java.util.HashMap

/**
 * 基础的ViewModel，继承AndroidViewModel
 * 实现的功能：
 * （1）提供显示对话框，取消对话框，Activity跳转，返回按钮事件，结束页面
 * 事件的LiveData，调用相关方法就可以
 * （2）在所有订阅中调用Observable的doOnSubscribe(CurrentViewModel.this)目的用于
 * 获取Disposable对象，统一管理所有订阅
 *
 *
 * 实现Consumer<Disposable>接口，调用Observable的doOnSubscribe(CurrentViewModel.this)目的用
 * 于获取Disposable对象，统一管理所有订阅，在ViewModel销毁调用onCleared()的时候清除所有订阅。
 * model.loadMore()
 * .doOnSubscribe(CurrentViewModel.this) //请求与ViewModel周期同步
 * .subscribe()
</Disposable> *
 *
 * （3）ViewModel 实现IBaseViewModel 接口监听生命周期，在Activity或Fragment中getLifecycle().addObserver(viewModel);注册
 */
open class BaseViewModel(application: Application) : AndroidViewModel(application), IBaseViewModel {
    //管理RxJava，主要针对RxJava异步操作造成的内存泄漏
    private var baseUILiveDataEvent: BaseUILiveDataEvent? = null

    /**
     * 获取界面事件相关处理类
     *
     * @return
     */
    fun getBaseUILiveDataEvent(): BaseUILiveDataEvent {
        if (baseUILiveDataEvent == null) {
            baseUILiveDataEvent = BaseUILiveDataEvent()
        }
        return baseUILiveDataEvent as BaseUILiveDataEvent
    }

    override fun onAny(owner: LifecycleOwner, event: Lifecycle.Event) {

    }

    override fun onCreate() {

    }

    override fun onDestroy() {

    }

    override fun onStart() {

    }

    override fun onStop() {

    }

    override fun onResume() {

    }

    override fun onPause() {

    }

    /**
     * 注册RxBus,订阅事件
     * 在Activity的onCreate方法中调用
     */
    override fun registerRxBus() {
        // 订阅事件
    }

    /**
     * 移除RxBus订阅
     */
    override fun removeRxBus() {

    }

    /**
     * 展示进度框
     */
    fun showProgressDialog() {
        val dialogData = DialogData()
        dialogData.isProcessDialog = 0
        baseUILiveDataEvent?.getShowDialogEvent()?.postValue(dialogData)
    }

    /**
     * 显示只有标题对话框
     */
    fun showDialogWithTitle(title: String) {
        val dialogData = DialogData()
        dialogData.title = title
        dialogData.isProcessDialog = 1
        baseUILiveDataEvent?.getShowDialogEvent()?.postValue(dialogData)
    }

    /**
     * 显示带按钮的对话框
     *
     * @param title
     * @param okListener
     */
    fun showDialogWithAction(title: String, okListener: View.OnClickListener) {
        val dialogData = DialogData()
        dialogData.title = title
        dialogData.isProcessDialog = 2
        dialogData.okListener = okListener
        baseUILiveDataEvent?.getShowDialogEvent()?.postValue(dialogData)
    }

    /**
     * 取消Dialog显示
     */
    fun dismissDialog() {
        baseUILiveDataEvent?.getDismissDialogEvent()?.postValue(null)
    }

    /**
     * 退出应用
     */
    fun exitApp() {
        baseUILiveDataEvent?.getExitAppEvent()?.postValue(true)
    }

    /**
     * 跳转页面
     *
     * @param clz    所跳转的目的Activity类
     * @param bundle 跳转所携带的信息
     */
    fun startActivity(clz: Class<*>, bundle: Bundle? = null) {
        val params = HashMap<String, Any>()
        params[ParameterField.CLASS] = clz
        if (bundle != null) {
            params[ParameterField.BUNDLE] = bundle
        }
        baseUILiveDataEvent?.getStartActivityEvent()?.postValue(params)
    }

    /**
     * 跳转容器页面
     *
     * @param canonicalName 规范名 : Fragment.class.getCanonicalName()
     * @param bundle        跳转所携带的信息
     */
    fun startContainerActivity(canonicalName: String, bundle: Bundle? = null) {
        val params = HashMap<String, Any>()
        params[ParameterField.CANONICAL_NAME] = canonicalName
        if (bundle != null) {
            params[ParameterField.BUNDLE] = bundle
        }
        baseUILiveDataEvent?.getStartContainerActivityEvent()?.postValue(params)
    }

    /**
     * 关闭界面
     */
    fun finish() {
        baseUILiveDataEvent?.getFinishEvent()?.postValue(null)
    }

    /**
     * 返回上一层
     */
    fun onBackPressed() {
        baseUILiveDataEvent?.getOnBackPressedEvent()?.postValue(null)
    }

    object ParameterField {
        var CLASS = "CLASS"
        var CANONICAL_NAME = "CANONICAL_NAME"
        var BUNDLE = "BUNDLE"
    }
}
