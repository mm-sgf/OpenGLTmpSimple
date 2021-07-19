package com.cfox.camerarecord.gl

import android.content.Context
import android.graphics.SurfaceTexture
import android.opengl.GLSurfaceView
import android.util.AttributeSet

class GLCameraView : GLSurfaceView {

    constructor(context: Context):super(context) {initParams()}
    constructor(context: Context, attrs: AttributeSet):super(context, attrs) {initParams()}

    private val render :CameraRender
    init {
        setEGLContextClientVersion(2)

        render = CameraRender(this)
        setRenderer(render)
        /**
         * 刷新方式：
         *     RENDERMODE_WHEN_DIRTY 手动刷新，調用requestRender();
         *     RENDERMODE_CONTINUOUSLY 自動刷新，大概16ms自動回調一次onDrawFrame方法
         */
        //注意必须在setRenderer 后面。
        renderMode = RENDERMODE_WHEN_DIRTY
    }

    private fun initParams() {

    }

    fun getSurfaceTexture(): SurfaceTexture {
        return render.getSurfaceTexture()
    }

    fun startRecorder(){
        render.startRecorder()
    }

    fun stopRecorder() {
        render.stopRecorder()
    }
}