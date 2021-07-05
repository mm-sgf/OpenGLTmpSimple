package com.cfox.openglpro1.utils;

import android.opengl.GLES20;

import com.cfox.openglpro1.log.GLog;

public class ShaderHelper {

    public static int compileVertexShader(String shaderCode) {
        return compileShader(GLES20.GL_VERTEX_SHADER, shaderCode);
    }

    public static int compileFragmentShader(String shaderCode) {
        return compileShader(GLES20.GL_FRAGMENT_SHADER, shaderCode);
    }


    private static int compileShader(int type, String shaderCode) {

        // 创建一个着色器
        int shaderObjectId = GLES20.glCreateShader(type);

        if (shaderObjectId == 0) {
            GLog.e("create shader fail : type :" + type);
        } else {
            // 设置着色器
            GLES20.glShaderSource(shaderObjectId, shaderCode);
            // 编译着色器
            GLES20.glCompileShader(shaderObjectId);
            // 获取编译着色器状态，如果为0 表示编译失败
            final int[] compileStatus = new int[1];
            GLES20.glGetShaderiv(shaderObjectId, GLES20.GL_COMPILE_STATUS, compileStatus, 0);

            GLog.i("compile shader status :" + compileStatus[0]);
            GLog.i("gl shader info log:" + GLES20.glGetShaderInfoLog(shaderObjectId));

            if (compileStatus[0] == 0) {
                // 如果着色器编译失败，删除创建的着色器
                GLES20.glDeleteShader(shaderObjectId);
                shaderObjectId = 0;
            }
        }

        return shaderObjectId;
    }

    public static int linkProgram(int vertexShaderId, int fragmentShaderId) {
        // 创建应用程序
        int programObjectId = GLES20.glCreateProgram();
        if (programObjectId == 0) {
            GLog.e("create program fail:::" + programObjectId);
        } else {
            // 将着色器附到应用程序上
            GLES20.glAttachShader(programObjectId, vertexShaderId);
            GLES20.glAttachShader(programObjectId, fragmentShaderId);

            // 连接着色器
            GLES20.glLinkProgram(programObjectId);

            int[] linkStatus = new int[1];
            GLES20.glGetProgramiv(programObjectId, GLES20.GL_LINK_STATUS, linkStatus, 0);
            GLog.i("link status:" + linkStatus[0]);
            GLog.i("link info log:" + GLES20.glGetProgramInfoLog(programObjectId));

            if (linkStatus[0] == 0) {
                GLES20.glDeleteProgram(programObjectId);
                programObjectId = 0;
            }

        }

        return programObjectId;
    }

    /**
     * 验证OpenGL 对象， 开发中使用，生产要关闭
     * @param programObjectId
     * @return
     */
    public static boolean validateProgram(int programObjectId) {
        GLES20.glValidateProgram(programObjectId);

        int[] validateStatus = new int[1];
        GLES20.glGetProgramiv(programObjectId, GLES20.GL_VALIDATE_STATUS, validateStatus, 0);

        GLog.i("validate status :" + validateStatus[0]);

        return validateStatus[0] != 0;

    }
}
