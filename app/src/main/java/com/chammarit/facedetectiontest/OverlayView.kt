package com.chammarit.facedetectiontest

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View

class OverlayView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) :
    View(context, attrs, defStyle) {

    var bitmap: Bitmap? = null
        private set
    private val mCanvas: Canvas
    private var mPaint: Paint? = null
    var rect: Rect? = null
        set(rect) {
            field = rect
            this.clear()
        }

    var rectColor: Int = Color.RED
        set(color) {
            field = color
            mPaint!!.color = color
            this.clear()
        }
    private var mTouchTolerance: Int = 0

    init {
        mCanvas = Canvas()
        initPaint()
    }

    fun clear() {
        bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        bitmap!!.eraseColor(BACKGROUND)
        mCanvas.setBitmap(bitmap)
        invalidate()
    }

    override fun onSizeChanged(width: Int, height: Int, oldWidth: Int, oldHeight: Int) {
        super.onSizeChanged(width, height, oldWidth, oldHeight)
        clear()

    }

    override fun onDraw(canvas: Canvas) {
        canvas.drawColor(BACKGROUND)
        canvas.drawBitmap(bitmap!!, 0f, 0f, null)
        mPaint!!.color = rectColor
        if (null != rect) {
            canvas.drawRect(rect!!, mPaint!!)
        }
    }

    /**
     * Sets up paint attributes.
     */
    private fun initPaint() {
        mPaint = Paint()
        mPaint!!.isAntiAlias = true
        mPaint!!.isDither = true
        mPaint!!.color = Color.BLACK
        mPaint!!.style = Paint.Style.STROKE
        mPaint!!.strokeJoin = Paint.Join.ROUND
        mPaint!!.strokeCap = Paint.Cap.ROUND
        mPaint!!.strokeWidth = 12f
        mTouchTolerance = dp2px(TOUCH_TOLERANCE_DP)
    }

    /**
     * Converts dpi units to px
     *
     * @param dp
     * @return
     */
    private fun dp2px(dp: Int): Int {
        val r = context.resources
        val px =
            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp.toFloat(), r.displayMetrics)
        return px.toInt()
    }

    fun setPaint(paint: Paint) {
        this.mPaint = paint
    }

    companion object {
        private val TOUCH_TOLERANCE_DP = 24
        private val BACKGROUND = Color.TRANSPARENT
    }
}
