package com.cfox.opengltexturepro1.render;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import com.cfox.opengltexturepro1.R;
import com.cfox.opengltexturepro1.log.GLog;
import com.cfox.opengltexturepro1.utils.ShaderHelper;
import com.cfox.opengltexturepro1.utils.TextResourceReader;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;


/**
 * 透视投影
 * 使用透视投影
 */
public class tsMatrixRender implements GLSurfaceView.Renderer {


    /**
     * 在openGl 中只能绘制点， 线， 三角形，
     * 所以， 长方形要拆分成两个三角形进行绘制
     *
     * 下面定义两个三角形的顶点， 且坐标是逆时针的，称为卷曲顺序
     *
     * 使用三角形扇进行画，使用4个三角形完成一个矩形
     */
    private float[] tableVerticesWithTriangles = {

            // X , Y , Z , W , R , G , B
/*               0f,    0f,   0f,  1.5f,   1f,   1f,    1f,
            -0.5f, -0.8f,   0f,    1f, 0.7f, 0.7f , 0.7f,
             0.5f, -0.8f,   0f,    1f, 0.7f, 0.7f , 0.7f,
             0.5f,  0.8f,   0f,    2f, 0.7f, 0.7f , 0.7f,
            -0.5f,  0.8f,   0f,    2f, 0.7f, 0.7f , 0.7f,
            -0.5f, -0.8f,   0f,    1f, 0.7f, 0.7f , 0.7f,*/

               0f,    0f,   0f,    1f,   1f,   1f,    1f,
            -0.5f, -0.8f,   0f,    1f, 0.7f, 0.7f , 0.7f,
             0.5f, -0.8f,   0f,    1f, 0.7f, 0.7f , 0.7f,
             0.5f,  0.8f,   0f,    1f, 0.7f, 0.7f , 0.7f,
            -0.5f,  0.8f,   0f,    1f, 0.7f, 0.7f , 0.7f,
            -0.5f, -0.8f,   0f,    1f, 0.7f, 0.7f , 0.7f,


            // fan triangles
            // 五个点， 前两个代表位置， 后三个代表颜色，  R G B
            // X , Y , R , G , B
//             0f,    0f,     1f,   1f,    1f,
//            -0.5f, -0.8f, 0.7f, 0.7f , 0.7f,
//             0.5f, -0.8f, 0.7f, 0.7f , 0.7f,
//             0.5f,  0.8f, 0.7f, 0.7f , 0.7f,
//            -0.5f,  0.8f, 0.7f, 0.7f , 0.7f,
//            -0.5f, -0.8f, 0.7f, 0.7f , 0.7f,

//            0f,    0f,     1f,   1f,    1f,
//            -0.5f, -0.5f, 1f, 0f , 0f,
//            0.5f, -0.5f,  1f, 1f , 0f,
//            0.5f,  0.5f,  1f, 0f , 1f,
//            -0.5f,  0.5f, 1f, 1f , 0f,
//            -0.5f, -0.5f, 1f, 0f , 0f,

            // line
            -0.5f, 0f, 0f,  1f,  1f, 0f , 0f,
             0.5f, 0f, 0f,  1f,  0f, 1f , 0f,

            // mallets
            0.0f,  0.4f, 0f,  1f, 0f, 0f , 1f,
            0.0f, -0.4f, 0f,  1f,  1f, 0f , 0f

    };

    // 使用模型矩阵进行移动
    private final float[] modelMatrix = new float[16];

//    private static final int POSITION_COMPONENT_COUNT = 2;
    private static final int POSITION_COMPONENT_COUNT = 4;//位置分量x y z w
    private static final int COLOR_COMPONENT_COUNT = 3;
    private static final int BYTES_PER_FLOAT = 4;
    private static final int STRIDE = (POSITION_COMPONENT_COUNT + COLOR_COMPONENT_COUNT) * BYTES_PER_FLOAT;


    private final FloatBuffer vertexData;

//    private static final String U_COLOR = "u_Color";
//    private int uColorLocation;

    private static final String A_POSITION = "a_Position";
    private int aPositionLocation;

    private static final String A_COLOR = "a_Color";
    private int aColorLocation;

    private final float[] projectionMatrix = new float[16];
    private static final String U_MATRIX = "u_Matrix";
    private int uMatrixLocation;

    private Context mContext;

    private int program;

    public tsMatrixRender(Context context) {
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
        // 在顶点文件中添加 4 * 4 的矩阵
        String vertexShaderSource = TextResourceReader.readTextFileFromResource(mContext, R.raw.orth_fan_simple_vertex_shader);
        String fragmentSaderRource = TextResourceReader.readTextFileFromResource(mContext, R.raw.orth_fan_simple_fragment_shader);

        GLog.d("vertexShader Source:" + vertexShaderSource  + "  \n fragmentShader Source:" + fragmentSaderRource);

        int vertexShader = ShaderHelper.compileVertexShader(vertexShaderSource);
        int fragmentShader = ShaderHelper.compileFragmentShader(fragmentSaderRource);

        GLog.d("vertex shader code:" + vertexShader  + "   fragment shader code:" + fragmentShader);

        program = ShaderHelper.linkProgram(vertexShader, fragmentShader);

        ShaderHelper.validateProgram(program);

        GLog.d("program code:" + program);

        // 获取uniform 位置
//        uColorLocation = GLES20.glGetUniformLocation(program, U_COLOR);
        aColorLocation = GLES20.glGetAttribLocation(program, A_COLOR);

        // 获取属性位置
        aPositionLocation = GLES20.glGetAttribLocation(program, A_POSITION);

        GLog.d("color location:" + aColorLocation  + "   position location :" + aPositionLocation);

        // 获取矩阵的位置
        uMatrixLocation = GLES20.glGetUniformLocation(program, U_MATRIX);

        // 设置指向第一个位置
//        vertexData.position(0);
//        GLES20.glVertexAttribPointer(aPositionLocation, POSITION_COMPONENT_COUNT, GLES20.GL_FLOAT, false, 0, vertexData);

        // 因为下面的代码是获取顶点位置
        vertexData.position(0);
        //设置使用顶点属性
        GLES20.glVertexAttribPointer(
                aPositionLocation, POSITION_COMPONENT_COUNT, GLES20.GL_FLOAT, false, STRIDE, vertexData);
        GLES20.glEnableVertexAttribArray(aPositionLocation);

        // 当OpenGL 开始读颜色属性时， 要从第一个颜色属性开始，而不是第一个位置属性
        vertexData.position(POSITION_COMPONENT_COUNT);
        // 设置使用颜色属性
        GLES20.glVertexAttribPointer(
                aColorLocation, COLOR_COMPONENT_COUNT, GLES20.GL_FLOAT, false, STRIDE, vertexData);
        GLES20.glEnableVertexAttribArray(aColorLocation);

        GLES20.glUseProgram(program);

        GLog.d("onSurfaceCreated ----end->> ");

    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0, 0, width , height);
       /* final float aspectRatio = width > height ? (float) width / (float)height : (float)height / (float)width;
        GLog.d("onSurfaceChanged -----> height:" + height + "  width:" + width  + "  aspectRatio:"  + aspectRatio);

        // 进行正交矩阵映射
        if (width > height) {
            // landscape
            Matrix.orthoM(projectionMatrix, 0, -aspectRatio, aspectRatio, -1f, 1f, -1f, 1f);
        } else {
            // portrait or square
            Matrix.orthoM(projectionMatrix, 0, -1f, 1f, -aspectRatio, aspectRatio, -1f, 1f);

        }*/

       // 使用投影矩阵进行转换
        Matrix.perspectiveM(projectionMatrix, 0,70,(float) width / (float) height, 1f, 10f);
//        MatrixHelper.perspectiveM(projectionMatrix, 70,(float) width / (float) height, 1f, 10f);

        // 移动，沿着z 轴平移 -2 ，移动2 个单位
        Matrix.setIdentityM(modelMatrix, 0);
        //因为进行了投影矩阵转换的时候，最近点事 1 所以要向z 轴移动大于 -1 个单位才能显示出来
        Matrix.translateM(modelMatrix, 0, 0f,0, -2.5f);
        // 旋转
        Matrix.rotateM(modelMatrix, 0 , 20f, 1, 0, 0);

        final float[] tmp = new float[16];
        // 将 projectionMatrix * modelMatrix 后放到 tmp 中
        Matrix.multiplyMM(tmp, 0, projectionMatrix, 0 , modelMatrix, 0);
        // 将tmp 中数据， copy 到 projectionMatrix 中
        System.arraycopy(tmp, 0 , projectionMatrix, 0 , tmp.length);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLog.d("onDrawFrame -----> ");
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        // 使用正交矩阵
        GLES20.glUniformMatrix4fv(uMatrixLocation, 1, false, projectionMatrix, 0);
//        // 画正方形
//        GLES20.glUniform4f(uColorLocation, 0.0f, 1.0f, 1.0f, 1.0f);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, 6);
//
//        // 画线
//        GLES20.glUniform4f(uColorLocation, 1.0f, 0.0f, 0.0f, 1.0f);
        GLES20.glDrawArrays(GLES20.GL_LINES, 6, 2);
//
//        // 画点
//        GLES20.glUniform4f(uColorLocation, 0.0f, 0.0f, 1.0f, 1.0f);
        GLES20.glDrawArrays(GLES20.GL_POINTS, 8, 1);
////
////        // 画点
//        GLES20.glUniform4f(uColorLocation, 1.0f, 0.0f, 0.0f, 1.0f);
        GLES20.glDrawArrays(GLES20.GL_POINTS, 9, 1);


    }
}
