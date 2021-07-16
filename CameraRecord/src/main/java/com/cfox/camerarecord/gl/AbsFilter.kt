package com.cfox.camerarecord.gl

import android.content.Context
import android.opengl.GLES20
import com.cfox.camera.log.EsLog
import java.nio.ByteBuffer
import java.nio.ByteOrder

open class AbsFilter(context : Context, vertexShaderId: Int, fragmentShaderId:Int) {

    private val VERTEX = floatArrayOf(
        -1.0f, -1.0f,
        1.0f, -1.0f,
        -1.0f, 1.0f,
        1.0f, 1.0f
    )

    private val TEXTURE = floatArrayOf(
        0.0f, 0.0f,
        1.0f, 0.0f,
        0.0f, 1.0f,
        1.0f, 1.0f
    )

    private val vertexBuffer = ByteBuffer.allocateDirect(4 * 4 * 2).order(ByteOrder.nativeOrder()).asFloatBuffer()
    private val textureBuffer = ByteBuffer.allocateDirect(4 * 4 * 2).order(ByteOrder.nativeOrder()).asFloatBuffer()

    private val vPosition : Int
    private val vCoord : Int
    private val vTexture : Int
    val program : Int

    init {
        // 设置定点缓冲区
        vertexBuffer.clear()
        vertexBuffer.put(VERTEX)

        // 设置纹理坐标缓冲区
        textureBuffer.clear()
        textureBuffer.put(TEXTURE)

        val vertexShader = GLFileUtils.readRawTextFile(context, vertexShaderId)
        val fragmentShader = GLFileUtils.readRawTextFile(context, fragmentShaderId)

        EsLog.d("vertex shader : $vertexShader")
        EsLog.d("fragment shader : $fragmentShader")

        program = GLUtils.loadProgram(vertexShader,fragmentShader)

        // 获取GLSL 中参数GPU 操作id
        vPosition = GLES20.glGetAttribLocation(program, "vPosition")
        vCoord = GLES20.glGetAttribLocation(program, "vCoord")
        vTexture = GLES20.glGetUniformLocation(program, "vTexture")
    }


    open fun setSize(width:Int, height:Int) {
        // 设置画布大小
        GLES20.glViewport(0,0, width ,height)
    }


    open fun onDraw(texture : Int)  : Int{
        // 使用 GL 程序
        GLES20.glUseProgram(program)
        drawBefore()
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

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4)
        return texture
    }

    open fun drawBefore(){

    }

}