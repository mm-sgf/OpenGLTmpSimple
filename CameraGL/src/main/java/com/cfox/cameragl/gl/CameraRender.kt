package com.cfox.cameragl.gl

import android.graphics.SurfaceTexture
import android.opengl.GLSurfaceView
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class CameraRender(val glCameraView: GLCameraView) : GLSurfaceView.Renderer, SurfaceTexture.OnFrameAvailableListener {


    private var screenFilter:ScreenFilter ? = null
    private val mtx = FloatArray(16)
    // 创建一个surfaceTexture , 并获取surfaceTexture 中的纹理id =》textures
    private val textures : IntArray = IntArray(1)
    private val surfaceTexture : SurfaceTexture = SurfaceTexture(textures[0])

    fun getSurfaceTexture() :SurfaceTexture {
        return surfaceTexture
    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        surfaceTexture.setOnFrameAvailableListener(this)
        screenFilter = ScreenFilter(glCameraView.context)

    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        screenFilter?.setSize(width, height)
    }

    override fun onDrawFrame(gl: GL10?) {

        // 将一帧数据更新到GL 可以操作的纹理缓冲区中
        surfaceTexture.updateTexImage()
        // 获取图像的矩阵
        surfaceTexture.getTransformMatrix(mtx)
        screenFilter?.setTransformMatrix(mtx)
        screenFilter?.onDraw(textures[0])
    }

    override fun onFrameAvailable(surfaceTexture: SurfaceTexture?) {
        // 发起GL绘制
        glCameraView.requestRender()
    }
}