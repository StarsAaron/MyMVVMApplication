package com.aaron.basemvvmlibrary2.bean

import android.view.View

/**
 * 作者：Aaron
 * 时间：2019/9/26:22:34
 * 邮箱：
 * 说明：界面事件传递的封装类，用于显示定制的Dialog
 */
class DialogData {
    var title: String? = null
    var isProcessDialog: Int = 0// 0 进度 1 带标题 2 带按钮监听
    var okListener: View.OnClickListener? = null
}
