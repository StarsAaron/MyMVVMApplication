package com.aaron.mymvvmapplication.adapter

import android.util.Log
import com.aaron.basemvvmlibrary2.adapter.DataBoundAdapter
import com.aaron.mymvvmapplication.R
import com.aaron.mymvvmapplication.databinding.ItemCollectArticleBinding

class CollectArticleAdapter : DataBoundAdapter<ArticleVO, ItemCollectArticleBinding>() {
    override fun initView(binding: ItemCollectArticleBinding, item: ArticleVO) {
        binding.vo = item
        binding.tvTitle.setOnClickListener {
            Log.i("xx", "click")
        }
    }

    override val layoutId: Int
        get() = R.layout.item_collect_article
}