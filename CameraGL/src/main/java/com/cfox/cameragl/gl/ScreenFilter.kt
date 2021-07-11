package com.cfox.cameragl.gl

import android.content.Context
import android.opengl.GLES20
import android.opengl.Matrix
import com.cfox.camera.log.EsLog
import com.cfox.cameragl.R
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

        vertexBuffer = ByteBuffer.allocateDirect(4 * 4 * 2).order(ByteOrder.nativeOrder()).asFloatBuffer()
        vertexBuffer.clear()
        vertexBuffer.put(VERTEX)

        textureBuffer = ByteBuffer.allocateDirect(4 * 4 * 2).order(ByteOrder.nativeOrder()).asFloatBuffer()
        textureBuffer.clear()
        textureBuffer.put(TEXTURE)

        val vertexShader = GLFileUtils.readRawTextFile(context, R.raw.camera_vert)
        val fragmentShader = GLFileUtils.readRawTextFile(context, R.raw.camera_frag)

        EsLog.d("vertex shader : $vertexShader")
        EsLog.d("fragment shader : $fragmentShader")

        program = GLCameraUtils.loadProgram(vertexShader,fragmentShader)

        vPosition = GLES20.glGetAttribLocation(program, "vPosition")
        vCoord = GLES20.glGetAttribLocation(program, "vCoord")
        vMatrix = GLES20.glGetUniformLocation(program, "vMatrix")
        vTexture = GLES20.glGetUniformLocation(program, "vTexture")
    }

    fun setSize(width:Int, height:Int) {
        this.width = width
        this.height = height
    }

    fun setTransformMatrix(matrix: FloatArray) {
        mtx = matrix
    }

    fun onDraw(texture : Int) {

    }
}