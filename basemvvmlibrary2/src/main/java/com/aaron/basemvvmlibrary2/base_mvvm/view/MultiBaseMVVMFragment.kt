package com.aaron.basemvvmlibrary2.base_mvvm.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
 * 基础Fragment
 *
 * 用法跟BaseActivity相同
 * 一个Fragment绑定多个ViewModel
 */
/*
class CollectListFragment:MultiBaseMVVMFragment<ActivityCollectListBinding>() {
    override fun f_getLayoutId(): Int {
        return R.layout.activity_collect_list
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        f_registorUIBinding(BR.vm, f_getViewModel(CollectVM::class.java))
        f_registorUIBinding(BR.vm, f_getViewModel(PersonVM::class.java))
        initObserver()
    }

    override fun f_getViewModelProviders(): ViewModelProvider? {
        //使用自定义的ViewModelFactory来创建ViewModel,f_getViewModel调用的时候就会使用该ViewModelProviders
        val factory: AppViewModelFactory? = AppViewModelFactory.getInstance(activity!!.application)
        return ViewModelProviders.of(this, factory)
    }

    private fun initObserver() {
        val vm = f_getViewModel(CollectVM::class.java)
        vm.refreshTrigger.observe(this, Observer {
            Log.i("CollectVM", it.toString())
        })
        val pervm = f_getViewModel(PersonVM::class.java)
        pervm.name.observe(this, Observer {
            Log.i("PersonVM", it)
        })
    }
}
 */
abstract class MultiBaseMVVMFragment<V : ViewDataBinding> : BaseFragment() {
    protected lateinit var binding: V
    private val xmlViewModelList = ArrayList<BaseViewModel>()

    override fun createView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, f_getLayoutId(), container, false)
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        //解除Messenger注册
        for (viewModel in xmlViewModelList) {
            Messenger.getDefault().unregister(viewModel)
            viewModel.removeRxBus()
        }
        binding.unbind()
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
            getShowDialogEvent().observe(this@MultiBaseMVVMFragment, Observer {
                when {
                    it.isProcessDialog == 0 -> f_showProcessDialog()
                    it.isProcessDialog == 1 -> f_showDialogWithTitle(it.title!!)
                    it.isProcessDialog == 2 -> f_showDialogWithAction(it.title!!, it.okListener!!)
                }
            })
            //加载对话框消失
            getDismissDialogEvent().observe(
                this@MultiBaseMVVMFragment,
                Observer { f_dismissDialog() })
            //跳入新页面
            getStartActivityEvent().observe(this@MultiBaseMVVMFragment, Observer { params ->
                val clz = params[BaseViewModel.ParameterField.CLASS] as Class<*>
                val bundle = params[BaseViewModel.ParameterField.BUNDLE] as Bundle
                f_startActivity(clz, bundle)
            })
            //跳入ContainerActivity
            getStartContainerActivityEvent()
                .observe(this@MultiBaseMVVMFragment, Observer { params ->
                    val canonicalName =
                        params.get(BaseViewModel.ParameterField.CANONICAL_NAME) as String
                    val bundle = params[BaseViewModel.ParameterField.BUNDLE] as Bundle
                    f_startContainerActivity(canonicalName, bundle)
                })
            //关闭界面
            getFinishEvent().observe(this@MultiBaseMVVMFragment, Observer { f_finish() })
            //关闭上一层
            getOnBackPressedEvent()
                .observe(this@MultiBaseMVVMFragment, Observer { activity?.onBackPressed() })
            // 退出应用
            getExitAppEvent().observe(this@MultiBaseMVVMFragment, Observer {
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
    fun <T : ViewModel> f_getViewModel(viewModelClass: Class<T>?): T {
        val viewModelProvider = f_getViewModelProviders()
        return viewModelProvider?.get(viewModelClass!!) ?: ViewModelProviders.of(this).get(
            viewModelClass!!
        )
    }

    /**
     * 可重写该方法提供自定义的ViewModelProvider
     *
     * @return
     */
    open fun f_getViewModelProviders(): ViewModelProvider? {
        return null
    }
}