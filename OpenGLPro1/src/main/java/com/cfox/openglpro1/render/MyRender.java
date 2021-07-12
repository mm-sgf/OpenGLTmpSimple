package com.cfox.openglpro1.render;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

import com.cfox.openglpro1.R;
import com.cfox.openglpro1.log.GLog;
import com.cfox.openglpro1.utils.ShaderHelper;
import com.cfox.openglpro1.utils.TextResourceReader;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * 自定义渲染器（Renderer）
 */
public class MyRender implements GLSurfaceView.Renderer {
    private static final int POSITION_COMPONENT_COUNT = 2;

    /**
     * 在openGl 中只能绘制点， 线， 三角形，
     * 所以， 长方形要拆分成两个三角形进行绘制
     *
     * 下面定义两个三角形的顶点， 且坐标是逆时针的，称为卷曲顺序
     */
    private float[] tableVerticesWithTriangles = {
            //triangles 1
            -0.5f, -0.5f,
             0.5f,  0.5f,
            -0.5f,  0.5f,

            //triangles 2
            -0.5f, -0.5f,
             0.5f, -0.5f,
             0.5f,  0.5f,

            //triangles 3
            -0.45f, -0.45f,
             0.45f,  0.45f,
            -0.45f,  0.45f,

            //triangles 4
            -0.45f, -0.45f,
             0.45f, -0.45f,
             0.45f,  0.45f,

            // line
            -0.5f, 0f,
             0.5f, 0f,

            // mallets
            0.0f,  0.25f,
            0.0f, -0.25f

    };

    private static final int BYTES_PER_FLOAT = 4;
    private final FloatBuffer vertexData;

    private static final String U_COLOR = "u_Color";
    private int uColorLocation;

    private static final String A_POSITION = "a_Position";
    private int aPositionLocation;

    private Context mContext;

    private int program;

    public MyRender(Context context) {
        this.mContext = context;
        // 初始化本地内存
        vertexData = ByteBuffer.allocateDirect(tableVerticesWithTriangles.length * BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();

        vertexData.put(tableVerticesWithTriangles);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        GLog.d("onSurfaceCreated ----->> ");
        // 清除颜色
        GLES20.glClearColor(0.0f, 0f, 0f, 0.0f);

        // 读取文件内容
        String vertexShaderSource = TextResourceReader.readTextFileFromResource(mContext, R.raw.simple_vertex_shader);
        String fragmentSaderRource = TextResourceReader.readTextFileFromResource(mContext, R.raw.simple_fragment_shader);

        GLog.d("vertexShader Source:" + vertexShaderSource  + "  \n fragmentShader Source:" + fragmentSaderRource);

        int vertexShader = ShaderHelper.compileVertexShader(vertexShaderSource);
        int fragmentShader = ShaderHelper.compileFragmentShader(fragmentSaderRource);

        GLog.d("vertex shader code:" + vertexShader  + "   fragment shader code:" + fragmentShader);

        program = ShaderHelper.linkProgram(vertexShader, fragmentShader);

        ShaderHelper.validateProgram(program);

        GLog.d("program code:" + program);

        // 获取uniform 位置
        uColorLocation = GLES20.glGetUniformLocation(program, U_COLOR);

        // 获取属性位置
        aPositionLocation = GLES20.glGetAttribLocation(program, A_POSITION);

        GLog.d("color location:" + uColorLocation  + "   position location :" + aPositionLocation);

        // 设置指向第一个位置
        vertexData.position(0);
        GLES20.glVertexAttribPointer(aPositionLocation, POSITION_COMPONENT_COUNT, GLES20.GL_FLOAT, false, 0, vertexData);
        GLES20.glEnableVertexAttribArray(aPositionLocation);

        GLES20.glUseProgram(program);

        GLog.d("onSurfaceCreated ----end->> ");

    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLog.d("onSurfaceChanged -----> height:" + height + "  width:" + width);
        GLES20.glViewport(0, 0, width , height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLog.d("onDrawFrame -----> ");
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        // 画正方形
        // 设置颜色
        GLES20.glUniform4f(uColorLocation, 0.0f, 1.0f, 0.0f, 1.0f);
        // 绘制，设置绘制定点buffer 中的几个点， 设置 GL_TRIANGLES 绘制三角
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 6);


        GLES20.glUniform4f(uColorLocation, 1.0f, 1.0f, 1.0f, 1.0f);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 6, 12);

        // 画线
        GLES20.glUniform4f(uColorLocation, 1.0f, 0.0f, 0.0f, 1.0f);
        // GL_LINES 绘制线
        GLES20.glDrawArrays(GLES20.GL_LINES, 12, 2);

        // 画点
        GLES20.glUniform4f(uColorLocation, 0.0f, 0.0f, 1.0f, 1.0f);
        // GL_POINTS 绘制点
        GLES20.glDrawArrays(GLES20.GL_POINTS, 14, 1);
//
//        // 画点
        GLES20.glUniform4f(uColorLocation, 1.0f, 0.0f, 0.0f, 1.0f);
        GLES20.glDrawArrays(GLES20.GL_POINTS, 15, 1);
    }
}
