package com.cfox.camerarecord.gl

import android.opengl.*
import android.view.Surface
import java.sql.Timestamp

class GLEnv (glContext: EGLContext){

    private val eglDisplay: EGLDisplay
    private val eglConfig : EGLConfig
    private val eglContext : EGLContext
    private var eglSurface : EGLSurface ? = null

    init {

        // 获取默认的egl Display
        eglDisplay = EGL14.eglGetDisplay(EGL14.EGL_DEFAULT_DISPLAY)

        if (eglDisplay == EGL14.EGL_NO_DISPLAY) {
            throw RuntimeException("get display fail")
        }

        // 初始化显示窗口
        val version = IntArray(2)
        if (!EGL14.eglInitialize(eglDisplay, version, 0,version, 1)) {
            throw RuntimeException("init fail")
        }

        val configAttributes =  intArrayOf(
            EGL14.EGL_RED_SIZE, 8, //颜色缓冲区中红色位数
            EGL14.EGL_GREEN_SIZE, 8,//颜色缓冲区中绿色位数
            EGL14.EGL_BLUE_SIZE, 8, //
            EGL14.EGL_ALPHA_SIZE, 8,//
            EGL14.EGL_RENDERABLE_TYPE, EGL14.EGL_OPENGL_ES2_BIT, //opengl es 2.0
            EGL14.EGL_NONE
        )
        val numConfigs = IntArray(1)
        val configs = arrayOfNulls<EGLConfig>(1)
        if (!EGL14.eglChooseConfig(
                eglDisplay,
                configAttributes,0,
                configs,0, configs.size,
                numConfigs, 0)) {
            throw RuntimeException("eglChooseConfig fail")
        }

        eglConfig = configs[0]!!

        val contextAttribList = intArrayOf(
            EGL14.EGL_RENDERABLE_TYPE, EGL14.EGL_OPENGL_ES2_BIT, //opengl es 2.0
            EGL14.EGL_NONE
        )


        eglContext = EGL14.eglCreateContext(eglDisplay, eglConfig, glContext, contextAttribList, 0)

        if (eglContext == EGL14.EGL_NO_CONTEXT) {
            throw RuntimeException("eglContext fail")
        }
    }

    fun createEglSurface(surface: Surface): EGLSurface {
        val surfaceAttribList = intArrayOf(
            EGL14.EGL_NONE
        )

        if (eglSurface != null) {
            EGL14.eglDestroySurface(eglDisplay, eglSurface)
        }

        eglSurface = EGL14.eglCreateWindowSurface(eglDisplay, eglConfig, surface, surfaceAttribList, 0)
        return eglSurface!!
    }

    fun eglMakeCurrent(eglSurface: EGLSurface) {
        if (EGL14.eglMakeCurrent(eglDisplay, eglSurface, eglSurface, eglContext)) {
            throw RuntimeException("eglMakeCurrent fail")
        }
    }

    fun draw(eglSurface: EGLSurface, timestamp: Long) {
        EGLExt.eglPresentationTimeANDROID(eglDisplay, eglSurface, timestamp)
        // 把 surface 中 color buffer 的内容显示出来
        EGL14.eglSwapBuffers(eglDisplay, eglSurface)
    }

    fun release() {
        if (eglSurface != null) {
            EGL14.eglDestroySurface(eglDisplay, eglSurface)
        }
        EGL14.eglMakeCurrent(eglDisplay, EGL14.EGL_NO_SURFACE, EGL14.EGL_NO_SURFACE, EGL14.EGL_NO_CONTEXT)
        EGL14.eglDestroyContext(eglDisplay, eglContext)
        EGL14.eglReleaseThread()
        EGL14.eglTerminate(eglDisplay)
    }
}