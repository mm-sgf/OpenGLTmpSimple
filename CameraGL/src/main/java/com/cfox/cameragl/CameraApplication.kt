package com.cfox.cameragl

import android.app.Application
import com.cfox.camera.EsCamera
import com.cfox.camera.log.EsLog

class CameraApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        EsLog.setPrintTag("GL-Camera")
        EsCamera.init(this)
    }
}