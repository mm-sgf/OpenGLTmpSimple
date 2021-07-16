package com.cfox.camerarecord.gl

import android.graphics.SurfaceTexture
import android.opengl.GLSurfaceView
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class CameraRender(val glCameraView: GLCameraView) : GLSurfaceView.Renderer, SurfaceTexture.OnFrameAvailableListener {


    private val mtx = FloatArray(16)
    // 创建一个surfaceTexture , 并获取surfaceTexture 中的纹理id =》textures
    private val textures : IntArray = IntArray(1)
    private val surfaceTexture : SurfaceTexture = SurfaceTexture(textures[0])

    private var cameraFilter : CameraFilter ? = null
    private var previewFilter : PreviewFilter ? = null

    fun getSurfaceTexture() :SurfaceTexture {
        return surfaceTexture
    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        surfaceTexture.setOnFrameAvailableListener(this)
        cameraFilter = CameraFilter(glCameraView.context)
        previewFilter = PreviewFilter(glCameraView.context)

    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        cameraFilter?.setSize(width, height)
    }

    override fun onDrawFrame(gl: GL10?) {

        // 将一帧数据更新到GL 可以操作的纹理缓冲区中
        surfaceTexture.updateTexImage()
        // 获取图像的矩阵
        surfaceTexture.getTransformMatrix(mtx)

        cameraFilter?.setTransformMatrix(mtx)
        // 将camera 中的画面绘制到fbo 的 texture 上
        var textureId = cameraFilter?.onDraw(textures[0])
        // 将 camera 中的texture 绘制到GLSurfaceView
        textureId = textureId?.let { previewFilter?.onDraw(it) }


    }

    override fun onFrameAvailable(surfaceTexture: SurfaceTexture?) {
        // 发起GL绘制
        glCameraView.requestRender()
    }
}