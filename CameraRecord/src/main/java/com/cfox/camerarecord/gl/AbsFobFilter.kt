package com.cfox.camerarecord.gl

import android.content.Context
import android.opengl.GLES20

open class AbsFobFilter(context : Context, vertexShaderId: Int, fragmentShaderId:Int)
    : AbsFilter(context, vertexShaderId, fragmentShaderId) {


    private val frameBuffer = IntArray(1){-1}
    private val frameTextures = IntArray(1){-1}

    override fun setSize(width: Int, height: Int) {
        super.setSize(width, height)

        release()
        // 创建一个帧缓冲区
        GLES20.glGenFramebuffers(1, frameBuffer, 0)

        // 创建一个纹理
        GLES20.glGenTextures(1, frameTextures, 0)

        // 绑定纹理参数
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, frameTextures[0]) //开始
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST) // 放大
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR) // 缩小
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0) // 结束

        // 将纹理和fbo 帧缓冲进行绑定
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, frameTextures[0])

        /**
         * 指定一个二维的纹理图片
         * level
         *     指定细节级别，0级表示基本图像，n级则表示Mipmap缩小n级之后的图像（缩小2^n）
         * internalformat
         *     指定纹理内部格式，必须是下列符号常量之一：GL_ALPHA，GL_LUMINANCE，GL_LUMINANCE_ALPHA，GL_RGB，GL_RGBA。
         * width height
         *     指定纹理图像的宽高，所有实现都支持宽高至少为64 纹素的2D纹理图像和宽高至少为16 纹素的立方体贴图纹理图像 。
         * border
         *     指定边框的宽度。必须为0。
         * format
         *     指定纹理数据的格式。必须匹配internalformat。下面的符号值被接受：GL_ALPHA，GL_RGB，GL_RGBA，GL_LUMINANCE，和GL_LUMINANCE_ALPHA。
         * type
         *     指定纹理数据的数据类型。下面的符号值被接受：GL_UNSIGNED_BYTE，GL_UNSIGNED_SHORT_5_6_5，GL_UNSIGNED_SHORT_4_4_4_4，和GL_UNSIGNED_SHORT_5_5_5_1。
         * pixels
         *     指定一个指向内存中图像数据的指针。
         */

        GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, width, height, 0, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, null)

        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, frameBuffer[0]) // 绑定fbo

        //纹理和fbo 发生绑定
        GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0, GLES20.GL_TEXTURE_2D, frameBuffer[0], 0)

        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0)

    }

    private fun release() {
        if (frameTextures[0] >=0) {
            GLES20.glDeleteTextures(1, frameTextures, 0)
        }

        if (frameBuffer[0] >= 0) {
            GLES20.glDeleteFramebuffers(1, frameBuffer, 0)
        }
    }

    override fun onDraw(texture: Int) : Int {
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, frameBuffer[0])
        super.onDraw(frameTextures[0])
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0)
        return frameTextures[0]
    }
}