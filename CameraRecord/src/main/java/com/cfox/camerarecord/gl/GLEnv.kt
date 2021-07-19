package com.cfox.camerarecord.gl

import android.opengl.*
import android.view.Surface
import com.cfox.camera.log.EsLog
import javax.microedition.khronos.egl.EGL10

class GLEnv (glContext: EGLContext){

    private val eglDisplay: EGLDisplay
    private val eglConfig : EGLConfig
    private val eglContext : EGLContext
    private var eglSurface : EGLSurface ? = null
    private var eglPbSurface : EGLSurface ? = null

    init {

        // 获取默认的egl Display
        eglDisplay = EGL14.eglGetDisplay(EGL14.EGL_DEFAULT_DISPLAY)

        if (eglDisplay == EGL14.EGL_NO_DISPLAY) {
            throw RuntimeException("eglContext fail ${EGL14.eglGetError()}")
        }

        // 初始化显示窗口
        val version = IntArray(2)
        if (!EGL14.eglInitialize(eglDisplay, version, 0,version, 1)) {
            throw RuntimeException("eglContext fail ${EGL14.eglGetError()}")
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
            throw RuntimeException("eglContext fail ${EGL14.eglGetError()}")
        }

        eglConfig = configs[0]!!

        val contextAttribList = intArrayOf(
            EGL14.EGL_CONTEXT_CLIENT_VERSION, 2,
            EGL14.EGL_NONE
        )


        eglContext = EGL14.eglCreateContext(eglDisplay, eglConfig, glContext, contextAttribList, 0)

        if (eglContext == EGL14.EGL_NO_CONTEXT) {
            throw RuntimeException("eglContext fail ${EGL14.eglGetError()}")
        }

        val attribList = intArrayOf(
            EGL10.EGL_WIDTH, 1,
            EGL10.EGL_HEIGHT, 1,
            EGL10.EGL_NONE
        )

        eglPbSurface = EGL14.eglCreatePbufferSurface(eglDisplay, eglConfig, attribList,0)
        if (eglPbSurface == null || eglPbSurface === EGL10.EGL_NO_SURFACE) {
            throw RuntimeException("create pbuffer surface failed.${EGL14.eglGetError()}")
        }
    }

    fun attachGLThread() {
        eglPbSurface?.let {
            eglMakeCurrent(it)
            EsLog.d("attach success ......")
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
        EsLog.d("acreateEglSurface window  success ......")
        return eglSurface!!
    }

    fun eglMakeCurrent(eglSurface: EGLSurface) {
        if (!EGL14.eglMakeCurrent(eglDisplay, eglSurface, eglSurface, eglContext)) {
            throw RuntimeException("eglContext fail ${EGL14.eglGetError()}")
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
        if (eglPbSurface != null) {
            EGL14.eglDestroySurface(eglDisplay, eglPbSurface)
        }
        EGL14.eglMakeCurrent(eglDisplay, EGL14.EGL_NO_SURFACE, EGL14.EGL_NO_SURFACE, EGL14.EGL_NO_CONTEXT)
        EGL14.eglDestroyContext(eglDisplay, eglContext)
        EGL14.eglReleaseThread()
        EGL14.eglTerminate(eglDisplay)
    }
}