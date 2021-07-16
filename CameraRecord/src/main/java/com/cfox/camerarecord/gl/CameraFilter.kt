package com.cfox.camerarecord.gl

import android.content.Context
import android.opengl.GLES20
import com.cfox.camerarecord.R

class CameraFilter(context : Context) : AbsFobFilter(context, R.raw.camera_vert, R.raw.camera_frag1) {
    private var mtx : FloatArray = FloatArray(0)

    private val vMatrix : Int = GLES20.glGetUniformLocation(program, "vMatrix")

    override fun drawBefore() {
        super.drawBefore()
        GLES20.glUniformMatrix4fv(vMatrix, 1, false, mtx, 0)
    }

    fun setTransformMatrix(matrix: FloatArray) {
        mtx = matrix
    }

}