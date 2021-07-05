package com.cfox.opengltexturepro1.program;

import android.content.Context;
import android.opengl.GLES20;

import com.cfox.opengltexturepro1.utils.ShaderHelper;
import com.cfox.opengltexturepro1.utils.TextResourceReader;

public class ShaderProgram {

    //Uniform constants
    protected static final String U_MATRIX = "u_Matrix";
    protected static final String U_TEXTURE_UNIT = "u_TextureUnit";

    //attribute constants
    protected static final String A_POSITION = "a_Position";
    protected static final String A_COLOR = "a_Color";
    protected static final String A_TEXTURE_COORDINATES = "a_TextureCoordinates";

    protected final int program;

    protected ShaderProgram(Context context, int vertexShaderResourceId, int fragmentShaderResourceId) {
        String vertexShader = TextResourceReader.readTextFileFromResource(context, vertexShaderResourceId);
        String fragmentShader = TextResourceReader.readTextFileFromResource(context, fragmentShaderResourceId);
        program = ShaderHelper.buildProgram(vertexShader, fragmentShader);
        ShaderHelper.validateProgram(program);
    }

    public void useProgram() {
        GLES20.glUseProgram(program);
    }
}
