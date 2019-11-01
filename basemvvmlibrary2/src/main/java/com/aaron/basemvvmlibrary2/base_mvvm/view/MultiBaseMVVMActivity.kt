package com.aaron.basemvvmlibrary2.base_mvvm.view

import android.os.Bundle

import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders

import com.aaron.basemvvmlibrary2.base_mvvm.msg.Messenger
import com.aaron.basemvvmlibrary2.base_mvvm.viewmodel.BaseViewModel
import com.aaron.utilslibrary.utils.android.ActivityUtils

import java.util.ArrayList


/**
 * 支持DataBinding框架的基Activity
 * 一个Activity绑定多个ViewModel
 *
 * （1）自带Dialog，目前支持持showDialog() f_dismissDialog()方法，只能设置标题文字
 * （2）监听viewmodel的显示对话框，取消对话框，Activity跳转，返回按钮事件，结束页面事件触发动作
 * （3）提供Messenger，Rxbus统一管理
 * （4）一个页面可以绑定多个Viewmodel,只需调用f_registorUIBinding方法
 */
/*
例子: 生成的Binding文件是：Xml布局文件名+Binding
class CollectListActivity : MultiBaseMVVMActivity<ActivityCollectListBinding>() {
    val adapter = CollectArticleAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 一个页面可以绑定多个Viewmodel,只需调用f_registorUIBinding方法
        f_registorUIBinding(BR.vm,f_getViewModel(CollectVM::class.java))
        f_registorUIBinding(BR.vm,f_getViewModel(OtherVM::class.java))
        initRecyclerView()
    }

    // 可以不重写该方法，会使用默认的ViewModelProvider
    override fun f_getViewModelProviders(): ViewModelProvider? {
        //使用自定义的ViewModelFactory来创建ViewModel,f_getViewModel调用的时候就会使用该ViewModelProviders
        val factory: AppViewModelFactory? = AppViewModelFactory.getInstance(application)
        return ViewModelProviders.of(this, factory)
    }

    override fun f_getLayoutId(): Int {
        return R.layout.activity_collect_list
    }
}
 */
abstract class MultiBaseMVVMActivity<V : ViewDataBinding> : BaseActivity() {
    protected lateinit var binding: V
    private val xmlViewModelList = ArrayList<BaseViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //DataBindingUtil类需要在project的build中配置 dataBinding {enabled true }, 同步后会自动关联android.databinding包
        binding = DataBindingUtil.setContentView(this, f_getLayoutId())
        // 用LiveData取代並加上setLifecycleOwner就讓Data Binding具有lifecycle-aware性質了。
        binding.lifecycleOwner = this// 使用livedata实现双向绑定需要调用
    }

    /**
     * 绑定ViewModel
     *
     * 传入格式为 <BR.variableName,vm extends BaseViewModel>
     */
    fun <T : BaseViewModel> f_registorUIBinding(variableId: Int, viewModel: T) {
        xmlViewModelList.add(viewModel)
        //注册RxBus
        viewModel.registerRxBus()
        //关联ViewModel
        binding.setVariable(variableId, viewModel)
        //让ViewModel拥有View的生命周期感应
        lifecycle.addObserver(viewModel)

        viewModel.getBaseUILiveDataEvent().apply {
            //加载对话框显示
            getShowDialogEvent().observe(this@MultiBaseMVVMActivity, Observer {
                when {
                    it.isProcessDialog == 0 -> f_showProgressDialog()
                    it.isProcessDialog == 1 -> f_showDialogWithTitle(it.title!!)
                    it.isProcessDialog == 2 -> f_showDialogWithAction(it.title!!, it.okListener!!)
                }
            })
            //加载对话框消失
            getDismissDialogEvent().observe(
                this@MultiBaseMVVMActivity,
                Observer { f_dismissDialog() })
            //跳入新页面
            getStartActivityEvent().observe(this@MultiBaseMVVMActivity, Observer { params ->
                val clz = params[BaseViewModel.ParameterField.CLASS] as Class<*>
                val bundle = params[BaseViewModel.ParameterField.BUNDLE] as Bundle
                f_startActivity(clz, bundle)
            })
            //跳入ContainerActivity
            getStartContainerActivityEvent()
                .observe(this@MultiBaseMVVMActivity, Observer { params ->
                    val canonicalName =
                        params.get(BaseViewModel.ParameterField.CANONICAL_NAME) as String
                    val bundle = params[BaseViewModel.ParameterField.BUNDLE] as Bundle
                    f_startContainerActivity(canonicalName, bundle)
                })
            //关闭界面
            getFinishEvent().observe(this@MultiBaseMVVMActivity, Observer { finish() })
            //关闭上一层
            getOnBackPressedEvent()
                .observe(this@MultiBaseMVVMActivity, Observer { onBackPressed() })
            // 退出应用
            getExitAppEvent().observe(this@MultiBaseMVVMActivity, Observer {
                ActivityUtils.removeAllActivity()
            })
        }
    }

    /**
     * 获取ViewModel
     *
     * @param viewModelClass viewmodel的类名
     * @return
     */
    fun <T : ViewModel> f_getViewModel(viewModelClass: Class<T>): T {
        val viewModelProvider = f_getViewModelProviders()
        return viewModelProvider?.get(viewModelClass) ?: ViewModelProviders.of(this).get(
            viewModelClass
        )
    }

    /**
     * 可重写该方法提供自定义的ViewModelProvider
     *
     * 可以不重写该方法，会使用默认的ViewModelProvider
     * @return
     */
    open fun f_getViewModelProviders(): ViewModelProvider? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        //解除Messenger注册
        for (viewModel in xmlViewModelList) {
            Messenger.getDefault().unregister(viewModel)
            viewModel.removeRxBus()
        }

        // 解除绑定
        binding.unbind()
    }
}
