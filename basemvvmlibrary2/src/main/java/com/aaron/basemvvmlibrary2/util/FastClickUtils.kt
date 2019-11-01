package com.aaron.basemvvmlibrary2.util

/**
 * 作者：Aaron
 * 时间：2019/10/30:16:22
 * 邮箱：
 * 说明：判断是否点击过快
 */
/*
btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utils.isFastClick()) {
                    // 进行点击事件后的逻辑操作
                }
            }
 */
class FastClickUtils {
    companion object{
        // 两次点击按钮之间的点击间隔不能少于1000毫秒
        private val MIN_CLICK_DELAY_TIME = 1000
        private var lastClickTime: Long = 0

        /**
         * true 点击过快
         * false 符合点击频率
         */
        fun isFastClick(): Boolean {
            var flag = false
            val curClickTime = System.currentTimeMillis()
            if (curClickTime - lastClickTime < MIN_CLICK_DELAY_TIME) {
                flag = true
            }
            lastClickTime = curClickTime
            return flag
        }
    }
}