package com.cfox.cameragl

import android.graphics.SurfaceTexture
import android.os.Handler
import android.util.Size
import android.view.Surface
import com.cfox.camera.surface.SurfaceProvider

class GLSurfaceProvider(private val surfaceTexture: SurfaceTexture) : SurfaceProvider(TYPE.PREVIEW) {
    override fun createSurface(previewSize: Size, captureSize: Size, handler: Handler): Surface {
        surfaceTexture.setDefaultBufferSize(previewSize.width, previewSize.height)
        return Surface(surfaceTexture)
    }

    override fun release() {

    }
}