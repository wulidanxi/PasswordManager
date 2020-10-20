package com.wulidanxi.mcenter.util

import android.app.Activity
import android.content.Context
import android.util.DisplayMetrics
import androidx.recyclerview.widget.RecyclerView

object Utilities{
    fun dp2px(context: Context,value: Int) : Int{
        val scale = context.resources.displayMetrics.density
        return (value * scale + 0.5f).toInt()
    }
    fun getScreenHeight(activity: Activity) : Int{
        val windowManager = activity.windowManager
        val outMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getRealMetrics(outMetrics)
        return outMetrics.heightPixels
    }
    fun getScreenWidth(activity: Activity) : Int{
        val windowManager = activity.windowManager
        val outMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getRealMetrics(outMetrics)
        return outMetrics.widthPixels
    }
    fun getScrollDistance(recyclerView: RecyclerView) : Int{
        //仅适合item高度一致的场景
        return recyclerView.computeVerticalScrollOffset()
    }
}