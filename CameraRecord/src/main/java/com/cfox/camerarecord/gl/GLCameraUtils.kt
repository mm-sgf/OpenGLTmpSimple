package com.cfox.camerarecord.gl

import android.opengl.GLES20
import com.cfox.camera.log.EsLog
import java.lang.RuntimeException

object GLCameraUtils {

    fun loadProgram(vertexShader:String, fragmentShader:String) : Int {
        // ==== 加载顶点着色器
        // 创建顶点着色器
        val vShader = GLES20.glCreateShader(GLES20.GL_VERTEX_SHADER)
        // 加载程序
        GLES20.glShaderSource(vShader, vertexShader)
        // 编译
        GLES20.glCompileShader(vShader)

        // 查看状态
        val status = IntArray(1)
        GLES20.glGetShaderiv(vShader, GLES20.GL_COMPILE_STATUS, status, 0)
        if (status[0] != GLES20.GL_TRUE) {
            EsLog.e("gl shader info log:${GLES20.glGetShaderInfoLog(vShader)}" )
            throw RuntimeException("compile vertex shader fail.....${status[0]}")
        }

        // ===== 加载片源着色器
        // 创建片源着色器
        val fShader = GLES20.glCreateShader(GLES20.GL_FRAGMENT_SHADER)
        // 加载
        GLES20.glShaderSource(fShader, fragmentShader)
        // 编译
        GLES20.glCompileShader(fShader)

        // 检查
        val fStatus = IntArray(1)
        GLES20.glGetShaderiv(fShader, GLES20.GL_COMPILE_STATUS, fStatus, 0)
        if (fStatus[0] != GLES20.GL_TRUE) {
            throw RuntimeException("compile fragment shader fail.....")
        }

        // 创建程序
        val program = GLES20.glCreateProgram()
        // 挂载定点和片源
        GLES20.glAttachShader(program, vShader)
        GLES20.glAttachShader(program, fShader)

        GLES20.glLinkProgram(program)

        val programStatus = IntArray(1)
        GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS, programStatus, 0)
        if (programStatus[0] != GLES20.GL_TRUE) {
            throw RuntimeException("program link  fail.....")
        }

        GLES20.glDeleteShader(vShader)
        GLES20.glDeleteShader(fShader)
        return program
    }
}