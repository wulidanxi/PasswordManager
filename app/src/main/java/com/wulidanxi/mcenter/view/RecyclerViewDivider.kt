package com.wulidanxi.mcenter.view

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class RecyclerViewDivider(context: Context, orientation: Int) : RecyclerView.ItemDecoration() {
    lateinit var mPaint: Paint
    var mDivider: Drawable
    private var mDividerHeight: Int = 2
    private var mOrientation: Int = orientation
    private val mATTRS: IntArray = intArrayOf(android.R.attr.listDivider)

    init {
        if (orientation != LinearLayoutManager.VERTICAL && orientation != LinearLayoutManager.HORIZONTAL) {
            throw IllegalArgumentException("请输入正确的参数")
        }
        val a: TypedArray = context.obtainStyledAttributes(mATTRS)
        mDivider = a.getDrawable(0)!!
        a.recycle()
    }

    /**
     * Custom divider
     */
    constructor(context: Context, orientation: Int, height: Int, color: Int) : this(
        context,
        orientation
    ) {
        mDividerHeight = height
        mPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mPaint.color = color
        mPaint.style = Paint.Style.FILL
    }

    /**
     * Draw vertical list split line
     */

    private fun drawVertical(canvas: Canvas, parent: RecyclerView) {
        val left = parent.paddingLeft
        val right = parent.measuredWidth - parent.paddingRight
        val childSize = parent.childCount
        for (index in 0 until childSize) {
            val child = parent.getChildAt(index)
            val params: RecyclerView.LayoutParams = child.layoutParams as RecyclerView.LayoutParams
            val top = child.bottom + params.bottomMargin
            val bottom = top + mDividerHeight
            mDivider.setBounds(left, top, right, bottom)
            mDivider.draw(canvas)
            canvas.drawRect(
                left.toFloat(),
                top.toFloat(),
                right.toFloat(),
                bottom.toFloat(),
                mPaint
            )
        }
    }

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)
        if (mOrientation == LinearLayoutManager.VERTICAL) {
            outRect.set(0, 0, 0, mDividerHeight)
        } else {
            outRect.set(0, 0, mDividerHeight, 0)
        }

    }

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDraw(c, parent, state)
        if (mOrientation == LinearLayoutManager.VERTICAL) {
            drawVertical(c, parent)
        }
    }
}

