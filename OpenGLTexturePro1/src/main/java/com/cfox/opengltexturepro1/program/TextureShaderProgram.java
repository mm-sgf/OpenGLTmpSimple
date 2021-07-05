package com.cfox.opengltexturepro1.program;

import android.content.Context;
import android.opengl.GLES20;

import com.cfox.opengltexturepro1.R;

public class TextureShaderProgram extends ShaderProgram {
    //Uniform locations
    private final int uMatrixLocation;
    private final int uTextureUnitLocation;

    //attribute locations
    private final int aPositionLocation;
    private final int aTextureCoordinatesLocation;
    public TextureShaderProgram(Context context) {
        super(context, R.raw.texture_vertex_shader, R.raw.texture_fragemnt_shader);
        uMatrixLocation = GLES20.glGetUniformLocation(program, U_MATRIX);
        uTextureUnitLocation = GLES20.glGetUniformLocation(program, U_TEXTURE_UNIT);

        aPositionLocation = GLES20.glGetAttribLocation(program, A_POSITION);
        aTextureCoordinatesLocation = GLES20.glGetAttribLocation(program, A_TEXTURE_COORDINATES);
    }

    public void setUniforms(float[] matrix, int textureId) {
        // pass the matrix into the shader program
        GLES20.glUniformMatrix4fv(uMatrixLocation, 1, false, matrix, 0);

        // set the active texture unit to texture unit 0
        // 把活动的纹理单元设置为纹理单元0
        GLES20.glActiveTexture(textureId);

        // Bind the texture to this unit
        // 绑定纹理单元
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);

        // tell the texture uniform sampler to use this texture in the shader by telling it to read form texture unit 0
        // 把选定的纹理单元传递给片段着色器中的 u_TextureUnit
        GLES20.glUniform1i(uTextureUnitLocation, 0);
    }

    public int getPositionAttributeLocation() {
        return aPositionLocation;
    }

    public int getTextureCoordinatesAttributeLocation() {
        return aTextureCoordinatesLocation;
    }
}
