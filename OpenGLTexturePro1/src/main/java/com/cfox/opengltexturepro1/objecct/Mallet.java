package com.cfox.opengltexturepro1.objecct;

import android.opengl.GLES20;

import com.cfox.opengltexturepro1.Constants;
import com.cfox.opengltexturepro1.data.VertexArray;
import com.cfox.opengltexturepro1.program.ColorShaderProgram;

public class Mallet {

    private static final int POSITION_COMPONENT_COUNT = 2;
    private static final int COLOR_COMPONENT_COUNT = 3;
    private static final int STRIDE = (POSITION_COMPONENT_COUNT + COLOR_COMPONENT_COUNT) * Constants.BYTES_PER_FLOAT;

    private static final float[] VERTEX_DATA = {
            // order of coordinates X , Y , R , G, B

            // Triangle Fan
            0f,   -0.4f,       0f,     0f,    1f,
            0f,    0.4f,       1f,     0f,    0f,
    };

    private final VertexArray vertexArray;

    public Mallet() {
        vertexArray = new VertexArray(VERTEX_DATA);
    }


    public void bindData(ColorShaderProgram textureShaderProgram) {

        vertexArray.setVertexAttribuPointer(0,
                textureShaderProgram.getPositionAttributeLocation(),
                POSITION_COMPONENT_COUNT,
                STRIDE);

        vertexArray.setVertexAttribuPointer(POSITION_COMPONENT_COUNT,
                textureShaderProgram.getColorAttributeLocation(),
                COLOR_COMPONENT_COUNT,
                STRIDE);
    }

    public void  draw() {
        GLES20.glDrawArrays(GLES20.GL_POINTS, 0, 2);
    }

}
