package com.kanush_productions.drawingapp

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import android.view.ViewGroup
import com.kanush_productions.drawingapp.MainActivity.Companion.isBackgroundFilled
import com.kanush_productions.drawingapp.MainActivity.Companion.paintBrush
import com.kanush_productions.drawingapp.MainActivity.Companion.path
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sqrt


class DrawingClass : View {

    private val TOUCH_TOLERANCE = 8f
    private var params: ViewGroup.LayoutParams? = null
    private var mX = 0f
    private var mY = 0f
    var mCanvasBitmap: Bitmap? = null
    private var canvas: Canvas? = null

    // Detector for scaling gestures (i.e. pinching or double tapping
    private var detector: ScaleGestureDetector = ScaleGestureDetector(context, ScaleListener())
    private var scaleFactor: Float = 1f // Zoom level (initial value is 1x)
    private val MIN_ZOOM: Float = 0.1f
    private val MAX_ZOOM: Float = 10f

    private var initX: Float = 0f // See onTouchEvent
    private var initY: Float = 0f // See onTouchEvent

    private var displayWidth: Float = 0f // (Supposed to be) width of entire canvas
    private var displayHeight: Float = 0f // (Supposed to be) height of entire canvas

    private var canvasX: Float = 0f // x-coord of canvas center
    private var canvasY: Float = 0f // y-coord of canvas center







    private var bgColor: Int = Color.WHITE

    constructor(context: Context) : this(context, null) {
        init()
    }
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0){
        init()
    }
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr){
        init()
    }



    private fun init() {
        paintBrush.isAntiAlias = true
        paintBrush.isDither = true
        paintBrush.style = Paint.Style.STROKE
        paintBrush.strokeJoin = Paint.Join.ROUND
        paintBrush.alpha = 0xff
        paintBrush.strokeCap = Paint.Cap.ROUND
        paintBrush.color = currentBrush
        paintBrush.style = Paint.Style.STROKE
        paintBrush.strokeJoin = Paint.Join.ROUND
        paintBrush.strokeWidth = MainActivity.strokeWidthMain
        params = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mCanvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        canvas = Canvas(mCanvasBitmap!!)
    }



    @SuppressLint("SuspiciousIndentation")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        detector.onTouchEvent(event)
        val xPos = event.x
        val yPos = event.y

            when(event.actionMasked) {
                MotionEvent.ACTION_DOWN -> {
                    path = Path()
                    pathList.add(path)
                    strokeWidthList.add(MainActivity.strokeWidthMain)
                    colorList.add(currentBrush)
                    path.moveTo(xPos, yPos)
                    mX = event.x
                    mY = event.y
                    initX = xPos
                    initY = yPos
                    invalidate()
                }
                MotionEvent.ACTION_MOVE -> {
                    if (event.pointerCount == 1) {
                        touchMove(xPos, yPos)
                        invalidate()
                    } else {
                        val dx: Float = xPos - initX
                        val dy: Float = yPos - initY

                        canvasX += dx/scaleFactor
                        canvasY += dy/scaleFactor
                        invalidate()
                        initX = xPos
                        initY = yPos
                    }
                }
                MotionEvent.ACTION_UP -> {
                    if (event.pointerCount == 1){
                        //path.lineTo(mX, mY)
                        //invalidate()
                        //initX = xPos
                        //initY = yPos
                    } else {
                        //initX = xPos
                        //initY = yPos
                    }

                }
            }
        return true
    }
    private fun touchMove(x: Float, y: Float) {
        val dx = Math.abs(x - mX)
        val dy = Math.abs(y - mY)
        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            path.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2)
            mX = x
            mY = y
        }
    }

    override fun onDraw(canvas: Canvas) {
        if (isBackgroundFilled) {
            canvas.drawColor(bgColor)
        }

        canvas.drawBitmap(mCanvasBitmap!!, 0f, 0f, paintBrush)
        //canvas.save()
        //canvas.scale(scaleFactor, scaleFactor)
        //canvas.translate(canvasX, canvasY)
        for (i in pathList.indices) {
            paintBrush.color = colorList[i]
            paintBrush.strokeWidth = strokeWidthList[i]
            canvas.drawPath(pathList[i], paintBrush)
            invalidate()
        }
        //canvas.restore()

    }

    fun clearCanvas(){
        pathList.clear()
        colorList.clear()
        strokeWidthList.clear()
        //canvas?.drawBitmap(mCanvasBitmap!!, 0f, 0f, paintBrush)
        path.reset()
        //isBackgroundFilled = false
    }
    fun clearAll(){
        pathList.clear()
        colorList.clear()
        strokeWidthList.clear()
        canvas?.drawBitmap(mCanvasBitmap!!, 0f, 0f, paintBrush)
        path.reset()
        isBackgroundFilled = false
    }

    companion object{
        var pathList = ArrayList<Path>()
        var colorList = ArrayList<Int>()
        var currentBrush = Color.BLACK
        var strokeWidthList = ArrayList<Float>()
    }

    fun fillPaint(color: Int) {
        bgColor = color
    }
    fun undo() {
        if (pathList.isNotEmpty()) {
            pathList.removeAt(pathList.size - 1)
            colorList.removeAt(colorList.size - 1)
            strokeWidthList.removeAt(strokeWidthList.size - 1)
            invalidate()
        }
    }
        fun getDrawingBitmap(width: Int, height: Int): Bitmap {
            //val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            val bitmap = mCanvasBitmap
            mCanvasBitmap?.eraseColor(Color.WHITE)
            val canvas = Canvas(mCanvasBitmap!!)
            draw(canvas)
            return bitmap!!
        }
    private inner class ScaleListener : ScaleGestureDetector.SimpleOnScaleGestureListener() {
        override fun onScale(detector: ScaleGestureDetector): Boolean {
            // Self-explanatory
            scaleFactor *= detector.scaleFactor
            // If scaleFactor is less than 0.5x, default to 0.5x as a minimum. Likewise, if
            //  scaleFactor is greater than 10x, default to 10x zoom as a maximum.
            scaleFactor = Math.max(MIN_ZOOM, Math.min(scaleFactor, MAX_ZOOM))

            invalidate() // Re-draw the canvas

            return true
        }
    }

    }
