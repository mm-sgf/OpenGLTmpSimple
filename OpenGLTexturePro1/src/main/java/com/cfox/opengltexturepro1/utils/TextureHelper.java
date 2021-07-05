package com.cfox.opengltexturepro1.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;

import com.cfox.opengltexturepro1.log.GLog;

public class TextureHelper {

    public static int loadTexture(Context context, int resourceId) {


        final int[] textureObjectIds = new int[1];
        // 创建一个纹理对象
        GLES20.glGenTextures(1, textureObjectIds, 0);

        // 如果纹理对象ID 是 0 ，则创建失败
        if (textureObjectIds[0] == 0) {
            GLog.d("create texture fail");
            return 0;
        }

        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;// 禁止缩放
        // 获取原始图片大小
        final Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(),  resourceId, options);

        if (bitmap == null) {
            GLog.d("create bitmap fail");
            // 如果获取bitmap 失败， 删掉创建的纹理对象
            GLES20.glDeleteTextures(1, textureObjectIds, 0);
            return 0;
        }

        // 告诉OpenGL 后面的纹理调用应该应用纹理对象
        // 第一个参数告诉OpenGL 要作为二维纹理对待， 第二个参数告诉OpenGL 要绑定对应的ID
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureObjectIds[0]);

        // 设置默认的纹理过滤参数
        // 缩小的情况使用 MIP 三线性过滤
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR_MIPMAP_LINEAR);
        // 放大的时候，使用 双线性过滤
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);

        //加载位图 bitmap 到 OpenGL, 告诉 OpenGL 读入bitmap 定义数据， 并把它复制到当前绑定的纹理对象中
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);

        //生产MIP贴图
        GLES20.glGenerateMipmap(GLES20.GL_TEXTURE_2D);

        bitmap.recycle();

        // 既然完成了纹理的加载，解除与这个纹理的绑定，就不会用其他纹理方法调用意外地改变这个纹理了
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);

        GLog.d("gl texture load success ....." + textureObjectIds[0]);
        return textureObjectIds[0];


    }
}
