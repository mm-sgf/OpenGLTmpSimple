// 纹理顶点着色器
uniform mat4 u_Matrix;// 创建一个 4 * 4 的矩阵

attribute vec4 a_Position;// 添加一个位置属性， 4 个字节
attribute vec2 a_TextureCoordinates;// 添加一个纹理坐标属性， 2 个字节  // 纹理坐标分量 ， S 和 T

varying vec2 v_TextureCoordinates;// 添加一个接收纹理坐标的变量

void main() {
    v_TextureCoordinates = a_TextureCoordinates;
    gl_Position = u_Matrix * a_Position;
}
