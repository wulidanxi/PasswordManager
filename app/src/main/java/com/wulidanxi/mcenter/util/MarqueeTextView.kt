package com.wulidanxi.mcenter.util

import android.content.Context
import android.util.AttributeSet

class MarqueeTextView : androidx.appcompat.widget.AppCompatTextView {
    //构造函数必须这么写？
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, style: Int) : super(context, attrs, style)

    override fun isFocused(): Boolean {
        return true
    }


}