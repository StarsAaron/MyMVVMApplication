package com.aaron.basemvvmlibrary2.base_mvvm.view

import android.content.Intent

/**
 * 作者：Aaron
 * 时间：2019/10/22:16:39
 * 邮箱：
 * 说明：Activity 回调接口
 */
interface ActivityCallback {
    /**
     * 结果回调
     *
     * @param resultCode        结果码
     * @param data              数据
     */
    fun onActivityResult(resultCode: Int, data: Intent?)
}
