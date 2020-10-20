package com.wulidanxi.mcenter.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import com.wulidanxi.mcenter.util.Utilities

/*
 *用来显示不规则图片，
 * 上面两个是圆角，下面两个是直角
 * */
class OvalImageView : AppCompatImageView {
    /*圆角的半径，依次为左上角xy半径，右上角，右下角，左下角*/ //此处可根据自己需要修改大小
    private val round = 16
    private val rid = Utilities.dp2px(context,round)
    private val rids = floatArrayOf(
        rid.toFloat(),
        rid.toFloat(),
        rid.toFloat(),
        rid.toFloat(),
        rid.toFloat(),
        rid.toFloat(),
        rid.toFloat(),
        rid.toFloat()
    )

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(
        context,
        attrs
    )

    constructor(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int
    ) : super(context, attrs, defStyleAttr)

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = MeasureSpec.getSize(widthMeasureSpec)
        setMeasuredDimension(width, width)
    }

    /**
     * 画图
     *
     * @param canvas
     */
    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        val path = Path()
        val w = this.width
        val h = this.height
        /*向路径中添加圆角矩形。radii数组定义圆角矩形的四个圆角的x,y半径。radii长度必须为8*/path.addRoundRect(
            RectF(0F, 0F, w.toFloat(), h.toFloat()),
            rids,
            Path.Direction.CW
        )
        //开启抗锯齿
        canvas.drawFilter = PaintFlagsDrawFilter(
            0,
            Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG
        )
        canvas.clipPath(path)
        super.onDraw(canvas)
    }
}