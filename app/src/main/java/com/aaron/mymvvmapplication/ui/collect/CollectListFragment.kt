package com.aaron.mymvvmapplication.ui.collect

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.aaron.basemvvmlibrary2.base_mvvm.view.MultiBaseMVVMFragment
import com.aaron.mymvvmapplication.BR
import com.aaron.mymvvmapplication.R
import com.aaron.mymvvmapplication.databinding.ActivityCollectListBinding
import com.aaron.mymvvmapplication.ui.AppViewModelFactory

/**
 * 作者：Aaron
 * 时间：2019/11/1:14:46
 * 邮箱：
 * 说明：
 */
class CollectListFragment:MultiBaseMVVMFragment<ActivityCollectListBinding>() {
    /**
     * 引入布局
     */
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