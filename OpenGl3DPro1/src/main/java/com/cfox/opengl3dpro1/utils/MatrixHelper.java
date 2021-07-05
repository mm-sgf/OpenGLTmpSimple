package com.cfox.opengl3dpro1.utils;

/**
 * 自定义透视投影矩阵
 */
public class MatrixHelper {

    /**
     *
     * @param m
     * @param yFovInDegrees : 视野的角度， 必须小于 180
     * @param aspect ： 屏幕的宽高比
     * @param n ： 到远处的距离， 必须是正值且大于到近处平面的距离
     * @param f ： 到近处平面的距离，必须是正值
     */
    public static void perspectiveM(float[] m, float yFovInDegrees, float aspect , float n , float f) {
        final float angleInRadians = (float) (yFovInDegrees * Math.PI / 180.0);
        final float a = (float) (1.0 / Math.tan(angleInRadians / 2.0));


        /*
         *  a/aspect     0             0                  0
         *  0            a             0                  0
         *  0            0    -((f + n) / (f -n))    -(2fn) / (f - n)
         *  0            0            -1                   0
         */
        m[0] = a / aspect;
        m[1] = 0f;
        m[2] = 0f;
        m[3] = 0f;

        m[4] = 0f;
        m[5] = a;
        m[6] = 0f;
        m[7] = 0f;

        m[8] = 0f;
        m[9] = 0f;
        m[10] = -((f + n) / (f - n));
        m[11] = -1f;

        m[12] = 0f;
        m[13] = 0f;
        m[14] = -((2f * f * n) / (f - n));
        m[15] = 0f;
    }
}
