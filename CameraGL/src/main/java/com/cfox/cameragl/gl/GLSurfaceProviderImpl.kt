package com.cfox.cameragl.gl

import android.graphics.SurfaceTexture
import android.util.Size
import android.view.Surface
import android.view.TextureView
import com.cfox.camera.log.EsLog
import com.cfox.camera.surface.SurfaceProvider
import com.cfox.cameragl.AutoFitTextureView

class GLSurfaceProviderImpl(private val surfaceTexture: SurfaceTexture) : SurfaceProvider {
    private val surface: Surface = Surface(surfaceTexture)
    private var previewSize: Size? = null

    override fun getSurface(): Surface {
        if (!surface.isValid) {
            EsLog.e("==>mSurface isValid false ")
        }
        return surface
    }

    override fun isAvailable(): Boolean {
        EsLog.d("subscribe: ..........")
        return surface.isValid
    }

    override fun getPreviewSurfaceClass(): Class<*> {
        return SurfaceTexture::class.java
    }

    override fun setAspectRatio(size: Size) {
        EsLog.d("setAspectRatio: size width:" + size.width + "  height:" + size.height)
        previewSize = size
        if (surface.isValid) {
            previewSize?.let {
                surfaceTexture.setDefaultBufferSize(it.width, it.height)
            }
        }
    }
}