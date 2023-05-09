package com.example.graduationproject.utils

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.hardware.Camera
import android.util.AttributeSet
import android.view.SurfaceHolder
import android.view.SurfaceView
import java.io.IOException

class CircleCameraPreview(context: Context, attrs: AttributeSet?) : SurfaceView(context, attrs), SurfaceHolder.Callback {

    private lateinit var camera: Camera

    init {
        holder.addCallback(this)
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        camera = Camera.open()
        camera.setDisplayOrientation(90)
        try {
            camera.setPreviewDisplay(holder)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        val parameters = camera.parameters
        val previewSize = parameters.supportedPreviewSizes[0]
        parameters.setPreviewSize(previewSize.width, previewSize.height)
        camera.parameters = parameters
        camera.startPreview()
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        camera.stopPreview()
        camera.release()
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {}

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val size = MeasureSpec.getSize(widthMeasureSpec)
        setMeasuredDimension(size, size)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val centerX = (measuredWidth / 2).toFloat()
        val centerY = (measuredHeight / 2).toFloat()
        val radius = (measuredWidth / 2).toFloat()
        val paint = Paint()
        paint.color = Color.WHITE
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 10f
        canvas.drawCircle(centerX, centerY, radius, paint)
    }
}