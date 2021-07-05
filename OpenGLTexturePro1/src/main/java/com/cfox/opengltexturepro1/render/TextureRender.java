package com.cfox.opengltexturepro1.render;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import com.cfox.opengltexturepro1.R;
import com.cfox.opengltexturepro1.objecct.Mallet;
import com.cfox.opengltexturepro1.objecct.Table;
import com.cfox.opengltexturepro1.program.ColorShaderProgram;
import com.cfox.opengltexturepro1.program.TextureShaderProgram;
import com.cfox.opengltexturepro1.utils.MatrixHelper;
import com.cfox.opengltexturepro1.utils.TextureHelper;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class TextureRender implements GLSurfaceView.Renderer {

    private Context context;

    private final float[] projectionMatrix = new float[16];
    private final float[] modelMatrix = new float[16];

    private Table table;
    private Mallet mallet;

    private TextureShaderProgram textureShaderProgram;
    private ColorShaderProgram colorShaderProgram;

    private int texture;

    public TextureRender(Context context) {
        this.context = context;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        GLES20.glClearColor(0f, 0f, 0f, 0f);

        table = new Table();
        mallet = new Mallet();

        textureShaderProgram = new TextureShaderProgram(context);
        colorShaderProgram = new ColorShaderProgram(context);

        texture = TextureHelper.loadTexture(context, R.mipmap.c_bg);

    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0, 0, width , height);
        // 使用投影矩阵进行转换
//        Matrix.perspectiveM(projectionMatrix, 0,70,(float) width / (float) height, 1f, 10f);
        MatrixHelper.perspectiveM(projectionMatrix, 45,(float) width / (float) height, 1f, 10f);

        // 移动，沿着z 轴平移 -2 ，移动2 个单位
        Matrix.setIdentityM(modelMatrix, 0);
        //因为进行了投影矩阵转换的时候，最近点事 1 所以要向z 轴移动大于 -1 个单位才能显示出来
        Matrix.translateM(modelMatrix, 0, 0f,0, -2.5f);
        // 旋转
        Matrix.rotateM(modelMatrix, 0 , 0f, 1, 0, 0);

        final float[] tmp = new float[16];
        // 将 projectionMatrix * modelMatrix 后放到 tmp 中
        Matrix.multiplyMM(tmp, 0, projectionMatrix, 0 , modelMatrix, 0);
        // 将tmp 中数据， copy 到 projectionMatrix 中
        System.arraycopy(tmp, 0 , projectionMatrix, 0 , tmp.length);
    }

    @Override
    public void onDrawFrame(GL10 gl) {

        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

        // draw table
        textureShaderProgram.useProgram();
        textureShaderProgram.setUniforms(projectionMatrix, texture);
        table.bindData(textureShaderProgram);
        table.draw();

        // draw mallets
        colorShaderProgram.useProgram();
        colorShaderProgram.setUniforms(projectionMatrix);
        mallet.bindData(colorShaderProgram);
        mallet.draw();

    }
}
