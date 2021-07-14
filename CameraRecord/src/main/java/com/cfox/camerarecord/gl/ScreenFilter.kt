package com.cfox.camerarecord.gl

import android.content.Context
import android.opengl.GLES20
import android.opengl.Matrix
import com.cfox.camera.log.EsLog
import com.cfox.camerarecord.R
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

class ScreenFilter(context: Context) {

    var VERTEX = floatArrayOf(
        -1.0f, -1.0f,
        1.0f, -1.0f,
        -1.0f, 1.0f,
        1.0f, 1.0f
    )

    var TEXTURE = floatArrayOf(
        0.0f, 0.0f,
        1.0f, 0.0f,
        0.0f, 1.0f,
        1.0f, 1.0f
    )

    private val vertexBuffer : FloatBuffer
    private val textureBuffer: FloatBuffer

    private val program : Int
    private val vPosition : Int
    private val vCoord : Int
    private val vMatrix : Int
    private val vTexture : Int

    private var width : Int = 0
    private var height : Int = 0
    private var mtx : FloatArray = FloatArray(0)

    init {

        // 设置定点缓冲区
        vertexBuffer = ByteBuffer.allocateDirect(4 * 4 * 2).order(ByteOrder.nativeOrder()).asFloatBuffer()
        vertexBuffer.clear()
        vertexBuffer.put(VERTEX)

        // 设置纹理坐标缓冲区
        textureBuffer = ByteBuffer.allocateDirect(4 * 4 * 2).order(ByteOrder.nativeOrder()).asFloatBuffer()
        textureBuffer.clear()
        textureBuffer.put(TEXTURE)

        val vertexShader = GLFileUtils.readRawTextFile(context, R.raw.camera_vert)
        val fragmentShader = GLFileUtils.readRawTextFile(context, R.raw.camera_frag1)

        EsLog.d("vertex shader : $vertexShader")
        EsLog.d("fragment shader : $fragmentShader")

        program = GLCameraUtils.loadProgram(vertexShader,fragmentShader)

        // 获取GLSL 中参数GPU 操作id
        vPosition = GLES20.glGetAttribLocation(program, "vPosition")
        vCoord = GLES20.glGetAttribLocation(program, "vCoord")
        vMatrix = GLES20.glGetUniformLocation(program, "vMatrix")
        vTexture = GLES20.glGetUniformLocation(program, "vTexture")
    }

    fun setSize(width:Int, height:Int) {
        this.width = width
        this.height = height
        // 设置画布大小
        GLES20.glViewport(0,0, width ,height)
    }

    fun setTransformMatrix(matrix: FloatArray) {
        mtx = matrix
    }

    fun onDraw(texture : Int) {
        // 使用 GL 程序
        GLES20.glUseProgram(program)

        // 设置索引位置
        vertexBuffer.position(0)
        // index   指定要修改的通用顶点属性的索引。
        // size  指定每个通用顶点属性的组件数。
        // type  指定数组中每个组件的数据类型。
        // 接受符号常量GL_FLOAT  GL_BYTE，GL_UNSIGNED_BYTE，GL_SHORT，GL_UNSIGNED_SHORT或GL_FIXED。 初始值为GL_FLOAT。
        // normalized    指定在访问定点数据值时是应将其标准化（GL_TRUE）还是直接转换为定点值（GL_FALSE）。
        GLES20.glVertexAttribPointer(vPosition, 2, GLES20.GL_FLOAT, false, 0, vertexBuffer)
        // 设置生效
        GLES20.glEnableVertexAttribArray(vPosition)

        textureBuffer.position(0)
        GLES20.glVertexAttribPointer(vCoord, 2, GLES20.GL_FLOAT, false, 0, textureBuffer)
        GLES20.glEnableVertexAttribArray(vCoord)

        GLES20.glActiveTexture(texture)

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture)
        GLES20.glUniform1i(vTexture, 0)
        GLES20.glUniformMatrix4fv(vMatrix, 1, false, mtx, 0)

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4)



    }
}