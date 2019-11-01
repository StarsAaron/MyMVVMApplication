package com.aaron.basemvvmlibrary2.base_mvvm.view

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import androidx.annotation.IdRes
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity

import com.aaron.basemvvmlibrary2.R
import com.aaron.nicedialoglibrary.NiceDialog

import java.util.Objects
import java.util.Random

/*
Dialog相关方法 》》》
f_showProgressDialog() 展示进度框
f_showDialogWithTitle(String title) 显示只有标题对话框
f_showDialogWithAction(String title, View.OnClickListener okListener) 显示带按钮的对话框
f_dismissDialog() 取消信息对话框

startActivity相关方法 》》》
f_startActivity(Class<?> clz)
f_startActivity(Class<?> clz, Bundle bundle)
f_startActivityFinish(Class<? extends Activity> cls)
f_startActivityFinish(Intent intent)
f_finish()

启动容器Activity相关方法 》》》
f_startContainerActivity(String canonicalName)
f_startContainerActivity(String canonicalName, Bundle bundle)

startActivityForResult 相关方法 》》》
f_startActivityForResult(Class<? extends Activity> cls, ActivityCallback callback)
f_startActivityForResult(Intent intent, ActivityCallback callback)
f_startActivityForResult(Intent intent, @Nullable Bundle options, ActivityCallback callback)

获取控件相关 》》》
f_findViewById(@IdRes int id)
f_findActivityViewById(@IdRes int id)

f_getSystemService(@NonNull Class<T> serviceClass)  获取系统服务
f_getAttachActivity() 获取绑定的 Activity，防止出现 getActivity 为空
f_getLayoutId() 引入布局
f_isBackPressed() ContainerActivity 中用于返回按钮的逻辑
f_onKeyDown(int keyCode, KeyEvent event) Fragment 返回键被按下时回调
 */

/**
 * 作者：Aaron
 * 时间：2019/10/22:14:36
 * 邮箱：
 * 说明：
 */
abstract class BaseFragment : Fragment() {
    private var mActivity: FragmentActivity? = null
    private var mActivityCallback: ActivityCallback? = null
    private var mActivityRequestCode: Int = 0
    private var dialog: NiceDialog? = null
    private var mRootView: View? = null

    /**
     * 获得全局的 Activity
     */
    override fun onAttach(context: Context) {
        super.onAttach(context)
        mActivity = requireActivity()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mRootView = createView(inflater, container, savedInstanceState)
        if (mRootView == null && f_getLayoutId() > 0) {
            mRootView = inflater.inflate(f_getLayoutId(), null)
        }
        val parent = mRootView?.parent as ViewGroup
        parent.removeView(mRootView)
        return mRootView
    }

    /**
     * 可重写该方法返回一个View视图，否则默认使用f_getLayoutId方法返回的视图
     */
    protected open fun createView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return null
    }

    /**
     * 获取绑定的 Activity，防止出现 getActivity 为空
     */
    fun f_getAttachActivity(): FragmentActivity? {
        return mActivity
    }

    /**
     * f_startActivity 方法优化
     */
    fun f_startActivity(cls: Class<out Activity>) {
        startActivity(Intent(mActivity, cls))
    }

    /**
     * 跳转页面
     *
     * @param clz    所跳转的目的Activity类
     * @param bundle 跳转所携带的信息
     */
    fun f_startActivity(clz: Class<*>, bundle: Bundle?) {
        val intent = Intent(mActivity, clz)
        if (bundle != null) {
            intent.putExtras(bundle)
        }
        startActivity(intent)
    }

    /**
     * 跳转之后关闭当前页面
     *
     * @param cls
     */
    fun f_startActivityFinish(cls: Class<out Activity>) {
        f_startActivityFinish(Intent(mActivity, cls))
    }

    /**
     * 跳转之后关闭当前页面
     *
     * @param intent
     */
    fun f_startActivityFinish(intent: Intent) {
        startActivity(intent)
        f_finish()
    }

    /**
     * 跳转容器页面
     *
     * @param canonicalName 规范名 : Fragment.class.getCanonicalName()
     * @param bundle        跳转所携带的信息
     */
    fun f_startContainerActivity(canonicalName: String, bundle: Bundle? = null) {
        val intent = Intent(mActivity, ContainerActivity::class.java)
        intent.putExtra(ContainerActivity.FRAGMENT, canonicalName)
        if (bundle != null) {
            intent.putExtra(ContainerActivity.BUNDLE, bundle)
        }
        startActivity(intent)
    }

    /**
     * f_startActivityForResult 方法优化
     */
    fun f_startActivityForResult(cls: Class<out Activity>, callback: ActivityCallback) {
        f_startActivityForResult(Intent(mActivity, cls), null, callback)
    }

    fun f_startActivityForResult(intent: Intent, callback: ActivityCallback) {
        f_startActivityForResult(intent, null, callback)
    }

    fun f_startActivityForResult(intent: Intent, options: Bundle?, callback: ActivityCallback) {
        // 回调还没有结束，所以不能再次调用此方法，这个方法只适合一对一回调，其他需求请使用原生的方法实现
        if (mActivityCallback == null) {
            mActivityCallback = callback
            // 随机生成请求码，这个请求码在 0 - 255 之间
            mActivityRequestCode = Random().nextInt(255)
            startActivityForResult(intent, mActivityRequestCode, options)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (mActivityCallback != null && mActivityRequestCode == requestCode) {
            mActivityCallback!!.onActivityResult(resultCode, data)
            mActivityCallback = null
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // 销毁对话框资源
        f_dismissDialog()
    }

    /**
     * 展示进度框
     */
    fun f_showProcessDialog() {
        f_dismissDialog()
        dialog = NiceDialog(activity)
            .setLayoutId(R.layout.dialog_progress)
            .setWidth(58)
            .setHeight(58)
            .setOutCancel(false)
            .show(Objects.requireNonNull<FragmentActivity>(activity).supportFragmentManager)
    }

    /**
     * 显示只有标题对话框
     */
    fun f_showDialogWithTitle(title: String) {
        f_dismissDialog()
        dialog = NiceDialog.createDialogWithConfirmButton(
            activity,
            Objects.requireNonNull<FragmentActivity>(activity).supportFragmentManager,
            title
        ) {f_dismissDialog() }
    }

    /**
     * 显示带按钮的对话框
     *
     * @param title
     * @param okListener
     */
    fun f_showDialogWithAction(title: String, okListener: View.OnClickListener) {
        f_dismissDialog()
        dialog = NiceDialog.createDialogWithConfirmButton(
            activity,
            Objects.requireNonNull<FragmentActivity>(activity).supportFragmentManager,
            title,
            okListener
        )
    }

    /**
     * 取消Dialog显示
     */
    fun f_dismissDialog() {
        if (dialog != null) {
            dialog!!.dismiss()
            dialog = null
        }
    }

    /**
     * 根据资源 id 获取一个 View 对象
     */
    protected fun <V : View> f_findViewById(@IdRes id: Int): V? {
        return mRootView?.findViewById(id)
    }

    protected fun <V : View> f_findActivityViewById(@IdRes id: Int): V? {
        return mActivity?.findViewById(id)
    }

    /**
     * 销毁当前 Fragment 所在的 Activity
     */
    fun f_finish() {
        mActivity?.finish()
        mActivity = null
    }

    /**
     * 获取系统服务
     */
    fun <T> f_getSystemService(serviceClass: Class<T>): T? {
        return ContextCompat.getSystemService(mActivity!!, serviceClass)
    }

    /**
     * 引入布局
     */
    protected abstract fun f_getLayoutId(): Int

    /**
     * ContainerActivity 中用于返回按钮的逻辑
     * @return
     */
    open fun f_isBackPressed(): Boolean {
        return false
    }

    /**
     * Fragment 返回键被按下时回调
     */
    open fun f_onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        // 默认不拦截按键事件，回传给 Activity
        return false
    }

}
