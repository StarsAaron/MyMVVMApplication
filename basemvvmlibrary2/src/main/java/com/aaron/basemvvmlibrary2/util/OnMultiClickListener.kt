package com.aaron.basemvvmlibrary2.util

import android.view.View

/**
 * 作者：Aaron
 * 时间：2019/10/30:16:23
 * 邮箱：
 * 说明：
 */
/*
btn.setOnClickListener(new OnMultiClickListener() {
    @Override
    public void onMultiClick(View v) {
        // 进行点击事件后的逻辑操作
    }
});
 */
abstract class OnMultiClickListener : View.OnClickListener {

    abstract fun onMultiClick(v: View)

    override fun onClick(v: View) {
        val curClickTime = System.currentTimeMillis()
        if (curClickTime - lastClickTime >= MIN_CLICK_DELAY_TIME) {
            // 超过点击间隔后再将lastClickTime重置为当前点击时间
            lastClickTime = curClickTime
            onMultiClick(v)
        }
    }

    companion object {
        // 两次点击按钮之间的点击间隔不能少于1000毫秒
        private val MIN_CLICK_DELAY_TIME = 1000
        private var lastClickTime: Long = 0
    }
}