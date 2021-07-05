package com.cfox.opengltexturepro1.program;

import android.content.Context;
import android.opengl.GLES20;

import com.cfox.opengltexturepro1.R;
import com.cfox.opengltexturepro1.log.GLog;

public class ColorShaderProgram extends ShaderProgram {
    //Uniform locations
    private final int uMatrixLocation;

    // attribute locations
    private final int aPositionLocation;
    private final int aColorLocation;

    public ColorShaderProgram(Context context) {
        super(context, R.raw.orth_fan_simple_vertex_shader, R.raw.orth_fan_simple_fragment_shader);

        uMatrixLocation = GLES20.glGetUniformLocation(program, U_MATRIX);

        aPositionLocation = GLES20.glGetAttribLocation(program, A_POSITION);
        aColorLocation = GLES20.glGetAttribLocation(program, A_COLOR);

        GLog.d("aPositionLocation::" + aPositionLocation  + "   aColorLocation::" + aColorLocation);
    }

    public void setUniforms(float[] matrix) {
        GLES20.glUniformMatrix4fv(uMatrixLocation, 1, false, matrix, 0);
    }

    public int getColorAttributeLocation() {
        return aColorLocation;
    }

    public int getPositionAttributeLocation() {
        return aPositionLocation;
    }
}
