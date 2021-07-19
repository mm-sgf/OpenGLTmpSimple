package com.cfox.camerarecord.gl

import android.opengl.EGL14
import android.opengl.EGLSurface
import android.os.Handler
import android.os.HandlerThread
import android.view.Surface

class GLThread {


    private val handlerThread = HandlerThread("gl-thread")

    private val handler : Handler
    private val eglContext = EGL14.eglGetCurrentContext()
    private val glEnv  = GLEnv(eglContext)
    private var eglSurface: EGLSurface ? = null

    init {
        handlerThread.start()
        handler = Handler(handlerThread.looper)
    }

    fun attachGLThread(surface: Surface) {
        eglSurface = glEnv.createEglSurface(surface)
        handler.post {
            eglSurface?.let {
                glEnv.eglMakeCurrent(it)
            }
        }
    }


    fun getGLHandler(): Handler {
        return handler
    }

    fun glDraw(timestamp: Long) {
        glEnv.draw(eglSurface!!, timestamp)
    }


    fun release() {
        handlerThread.quitSafely()

    }
}