package com.aaron.basemvvmlibrary2.base_mvvm.view

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager

import androidx.annotation.ColorRes
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity

import com.aaron.basemvvmlibrary2.R
import com.aaron.nicedialoglibrary.NiceDialog
import com.aaron.utilslibrary.utils.UltimateBar

import java.util.Random

/**
 * 作者：Aaron
 * 时间：2019/10/21:11:48
 * 邮箱：
 * 说明：提供基础全屏，标题栏设置
 *
 * 状态栏相关方法 》》》
 * 重写 f_isRequestFullScreen  返回true 全屏
 * f_requestUltimateBar(ColorRes) 沉浸式状态栏
 * f_setPageUpButton(boolean) 是否显示Actionbar的返回按钮，已绑定点击事件
 *
 * Dialog相关方法 》》》
 * f_showProgressDialog() 展示进度框
 * f_showDialogWithTitle(String title) 显示只有标题对话框
 * f_showDialogWithAction(String title, View.OnClickListener okListener) 显示带按钮的对话框
 * f_dismissDialog() 取消信息对话框
 *
 * startActivity相关方法 》》》
 * f_startActivity(Class clz)
 * f_startActivity(Class clz, Bundle bundle)
 *
 * 启动容器Activity相关方法 》》》
 * f_startContainerActivity(String canonicalName)
 * f_startContainerActivity(String canonicalName, Bundle bundle)
 *
 * startActivityForResult 相关方法 》》》
 * f_startActivityForResult(Class cls, ActivityCallback callback)
 * f_startActivityForResult(Intent intent, ActivityCallback callback)
 * f_startActivityForResult(Intent intent, @Nullable Bundle options, ActivityCallback callback)
 *
 * 延迟执行相关方法 》》》
 * f_post(Runnable r)
 * f_postDelayed(Runnable r, long delayMillis)
 * f_postAtTime(Runnable r, long uptimeMillis)
 */
abstract class BaseActivity : AppCompatActivity() {
    private val mHandlerToken = hashCode()
    private var dialog: NiceDialog? = null
    private var mActivityCallback: ActivityCallback? = null
    private var mActivityRequestCode: Int = 0
    private var mStartActivityTag: String? = null
    private var mStartActivityTime: Long = 0

    @SuppressLint("ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 是否全屏
        if (f_isRequestFullScreen()) {
            supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
            // 全屏
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        // 沉浸式状态栏和导航栏
        if (f_initStatusBarColor() != -1) {
            f_requestUltimateBar(f_initStatusBarColor())
        }
        // 优化输入键盘
        if (f_getLayoutId() > 0) {
            f_initSoftKeyboard()
        }
//        setContentView(f_getLayoutId())
    }

    /**
     * 是否全屏
     *
     * @return
     */
    open fun f_isRequestFullScreen(): Boolean {
        return false
    }

    /**
     * 沉浸式状态栏和导航栏
     *
     * @param colorResource 状态栏颜色
     */
    private fun f_requestUltimateBar(@ColorRes colorResource: Int) {
        // 沉浸式状态栏和导航栏
        UltimateBar.newColorBuilder()
            .statusColor(resources.getColor(colorResource,null))
            .build(this)
            .apply()
    }

    /**
     * 返回沉浸式状态栏的颜色
     *
     * @return
     */
    @ColorRes
    open fun f_initStatusBarColor(): Int {
        return -1
    }

    /**
     * 是否显示返回按钮
     * 使用默认的SupportActionBar
     *
     * @param enable
     */
    fun f_setPageUpButton(enable: Boolean?) {
        supportActionBar!!.setDisplayHomeAsUpEnabled(enable!!)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    /**
     * 展示进度框
     */
    fun f_showProgressDialog() {
        f_dismissDialog()
        dialog = NiceDialog(this)
            .setLayoutId(R.layout.dialog_progress)
            .setWidth(58)
            .setHeight(58)
            .setOutCancel(false)
            .show(supportFragmentManager)
    }

    /**
     * 显示只有标题对话框
     */
    fun f_showDialogWithTitle(title: String) {
        f_dismissDialog()
        dialog = NiceDialog.createDialogWithConfirmButton(
            this,
            supportFragmentManager,
            title
        ) { f_dismissDialog() }
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
            this,
            supportFragmentManager,
            title,
            okListener
        )
    }

    /**
     * 取消信息对话框
     */
    fun f_dismissDialog() {
        if (dialog != null) {
            dialog!!.dismiss()
            dialog = null
        }
    }

    /**
     * 跳转页面
     *
     * @param clz 所跳转的目的Activity类
     */
    fun f_startActivity(clz: Class<*>) {
        startActivity(Intent(this, clz))
    }

    /**
     * 跳转页面
     *
     * @param clz    所跳转的目的Activity类
     * @param bundle 跳转所携带的信息
     */
    fun f_startActivity(clz: Class<*>, bundle: Bundle?) {
        val intent = Intent(this, clz)
        if (bundle != null) {
            intent.putExtras(bundle)
        }
        startActivity(intent)
    }

    /**
     * 跳转之后关闭当前页面
     * @param clz 所跳转的目的Activity类
     */
    fun f_startActivityFinish(clz: Class<*>) {
        f_startActivity(clz)
        finish()
    }

    /**
     * 跳转之后关闭当前页面
     * @param clz 所跳转的目的Activity类
     */
    fun f_startActivityFinish(clz: Class<*>, bundle: Bundle) {
        f_startActivity(clz, bundle)
        finish()
    }

    /**
     * 跳转容器页面
     *
     * @param canonicalName 规范名 : Fragment.class.getCanonicalName()
     * @param bundle        跳转所携带的信息
     */
    @JvmOverloads
    fun f_startContainerActivity(canonicalName: String, bundle: Bundle? = null) {
        val intent = Intent(this, ContainerActivity::class.java)
        intent.putExtra(ContainerActivity.FRAGMENT, canonicalName)
        if (bundle != null) {
            intent.putExtra(ContainerActivity.BUNDLE, bundle)
        }
        startActivity(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        // 销毁对话框资源
        f_dismissDialog()
        // 移除和这个 Activity 相关的消息回调
        HANDLER.removeCallbacksAndMessages(mHandlerToken)
    }

    /**
     * f_startActivityForResult 方法优化
     */
    fun f_startActivityForResult(cls: Class<out Activity>, callback: ActivityCallback) {
        f_startActivityForResult(Intent(this, cls), null, callback)
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

    /**
     * 处理 Activity 多重跳转：https://www.jianshu.com/p/579f1f118161
     */
    override fun startActivityForResult(intent: Intent, requestCode: Int, options: Bundle?) {
        if (f_startActivitySelfCheck(intent)) {
            f_hideSoftKeyboard()
            // 查看源码得知 f_startActivity 最终也会调用 f_startActivityForResult
            super.startActivityForResult(intent, requestCode, options)
        }
    }

    /**
     * 检查当前 Activity 是否重复跳转了，不需要检查则重写此方法并返回 true 即可
     *
     * @param intent          用于跳转的 Intent 对象
     * @return                检查通过返回true, 检查不通过返回false
     */
    protected fun f_startActivitySelfCheck(intent: Intent): Boolean {
        // 默认检查通过
        var result = true
        // 标记对象
        val tag: String?
        if (intent.component != null) {
            // 显式跳转
            tag = intent.component!!.className
        } else if (intent.action != null) {
            // 隐式跳转
            tag = intent.action
        } else {
            // 其他方式
            return true
        }

        if (tag == mStartActivityTag && mStartActivityTime >= SystemClock.uptimeMillis() - 500) {
            // 检查不通过
            result = false
        }

        // 记录启动标记和时间
        mStartActivityTag = tag
        mStartActivityTime = SystemClock.uptimeMillis()
        return result
    }

    /**
     * 初始化软键盘
     */
    protected fun f_initSoftKeyboard() {
        // 点击外部隐藏软键盘，提升用户体验
        f_getContentView().setOnClickListener { f_hideSoftKeyboard() }
    }

    /**
     * 和 setContentView 对应的方法
     */
    fun f_getContentView(): ViewGroup {
        return findViewById(Window.ID_ANDROID_CONTENT)
    }

    /**
     * 隐藏软键盘
     */
    protected fun f_hideSoftKeyboard() {
        // 隐藏软键盘，避免软键盘引发的内存泄露
        val view = currentFocus
        if (view != null) {
            val manager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            manager.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    override fun finish() {
        f_hideSoftKeyboard()
        super.finish()
    }

    /**
     * 延迟执行
     */
    fun f_post(r: Runnable): Boolean {
        return f_postDelayed(r, 0)
    }

    /**
     * 延迟一段时间执行
     */
    fun f_postDelayed(r: Runnable, delayMillis: Long): Boolean {
        var delayMillis = delayMillis
        if (delayMillis < 0) {
            delayMillis = 0
        }
        return f_postAtTime(r, SystemClock.uptimeMillis() + delayMillis)
    }

    /**
     * 在指定的时间执行
     */
    fun f_postAtTime(r: Runnable, uptimeMillis: Long): Boolean {
        // 发送和这个 Activity 相关的消息回调
        return HANDLER.postAtTime(r, mHandlerToken, uptimeMillis)
    }

    /**
     * 如果当前的 Activity（singleTop 启动模式） 被复用时会回调
     */
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        // 设置为当前的 Intent，避免 Activity 被杀死后重启 Intent 还是最原先的那个
        setIntent(intent)
    }

    /**
     * 获取布局 ID
     *
     * @return 布局layout的id
     */
    @LayoutRes
    protected abstract fun f_getLayoutId(): Int

    companion object {
        private val HANDLER = Handler(Looper.getMainLooper())
    }
}
