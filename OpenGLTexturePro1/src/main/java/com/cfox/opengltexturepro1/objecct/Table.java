package com.cfox.opengltexturepro1.objecct;

import android.opengl.GLES20;

import com.cfox.opengltexturepro1.Constants;
import com.cfox.opengltexturepro1.data.VertexArray;
import com.cfox.opengltexturepro1.program.TextureShaderProgram;

public class Table {

    private static final int POSITION_COMPONENT_COUNT = 2;
    private static final int TEXTURE_COORDINATES_COMPONENT_COUNT = 2;
    private static final int STRIDE = (POSITION_COMPONENT_COUNT + TEXTURE_COORDINATES_COMPONENT_COUNT) * Constants.BYTES_PER_FLOAT;

    private static final float[] VERTEX_DATA = {
            // order of coordinates X , Y , S , T
            /* 纹理坐标
             * t (0, 1)        (1, 1)
             *
             *
             *   (0, 0)        (1, 0)
             *                      s
            */

            // Triangle Fan
               0f,      0f,     0.5f,     0.5f,// 中间点
            -0.5f,   -0.8f,       0f,     1f,
             0.5f,   -0.8f,       1f,     1f,
             0.5f,    0.8f,       1f,     0.0f,
            -0.5f,    0.8f,       0f,     0.0f,
            -0.5f,    -0.8f,       0f,     1f,
    };

    private final VertexArray vertexArray;

    public Table() {
        vertexArray = new VertexArray(VERTEX_DATA);
    }


    public void bindData(TextureShaderProgram textureShaderProgram) {

        // 设置顶点坐标
        vertexArray.setVertexAttribuPointer(0,
                textureShaderProgram.getPositionAttributeLocation(),
                POSITION_COMPONENT_COUNT,
                STRIDE);

        // 设置纹理坐标
        vertexArray.setVertexAttribuPointer(POSITION_COMPONENT_COUNT,
                textureShaderProgram.getTextureCoordinatesAttributeLocation(),
                TEXTURE_COORDINATES_COMPONENT_COUNT,
                STRIDE);
    }

    public void  draw() {
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, 6);
    }

}
