package com.aaron.mymvvmapplication.ui.collect

import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.aaron.basemvvmlibrary2.base_mvvm.view.MultiBaseMVVMActivity
import com.aaron.mymvvmapplication.BR
import com.aaron.mymvvmapplication.R
import com.aaron.mymvvmapplication.adapter.ArticleVO
import com.aaron.mymvvmapplication.adapter.CollectArticleAdapter
import com.aaron.mymvvmapplication.databinding.ActivityCollectListBinding
import com.aaron.mymvvmapplication.ui.AppViewModelFactory

class CollectListActivity : MultiBaseMVVMActivity<ActivityCollectListBinding>() {
    val adapter = CollectArticleAdapter()

    override fun f_isRequestFullScreen(): Boolean {
        return true
    }

    override fun f_initStatusBarColor(): Int {
        return R.color.colorPrimary
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        f_registorUIBinding(BR.vm, f_getViewModel(CollectVM::class.java))
        f_registorUIBinding(BR.pvm, f_getViewModel(PersonVM::class.java))

        initObserver()

        binding.recyclerview.also {
            it.adapter = adapter
            it.layoutManager = LinearLayoutManager(this)
        }

        val dd = ArticleVO()
        dd.title = "3333"
        dd.name = "4444"
        val list = ArrayList<ArticleVO>()
        list.add(dd)
        list.add(dd)
        list.add(dd)
        adapter.addAll(list, false)
    }

    override fun f_getViewModelProviders(): ViewModelProvider? {
        //使用自定义的ViewModelFactory来创建ViewModel,f_getViewModel调用的时候就会使用该ViewModelProviders
        val factory: AppViewModelFactory? = AppViewModelFactory.getInstance(application)
        return ViewModelProviders.of(this, factory)
    }

    /**
     * 获取布局 ID
     *
     * @return 布局layout的id
     */
    override fun f_getLayoutId(): Int {
        return R.layout.activity_collect_list
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