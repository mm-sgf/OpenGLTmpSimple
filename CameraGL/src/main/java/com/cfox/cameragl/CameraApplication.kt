package com.cfox.cameragl

import android.app.Application
import com.cfox.camera.EsCamera
import com.cfox.camera.log.EsLog

class CameraApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        EsLog.setPrintTag("YUV-Camera")
        EsCamera.init(this)
    }
}