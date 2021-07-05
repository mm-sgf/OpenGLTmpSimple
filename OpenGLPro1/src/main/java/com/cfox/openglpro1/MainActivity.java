package com.cfox.openglpro1;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.opengl.GLSurfaceView;
import android.os.Bundle;

import com.cfox.openglpro1.log.GLog;
import com.cfox.openglpro1.render.MyRender;

public class MainActivity extends AppCompatActivity {

    private GLSurfaceView mGlSurfaceView;
    private boolean mSupportEs2;
    private boolean mIsRendererSet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mGlSurfaceView = new GLSurfaceView(this);
        setContentView(mGlSurfaceView);
        mSupportEs2 = checkSupportEs2();
        if (mSupportEs2) {
            configGl();
        }
    }

    private void configGl() {
        mGlSurfaceView.setEGLContextClientVersion(2);
        mGlSurfaceView.setEGLConfigChooser(8, 8, 8, 8, 16, 0);
        mGlSurfaceView.setRenderer(new MyRender(this));
        mIsRendererSet = true;
    }

    private boolean checkSupportEs2() {
        final ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        assert am != null;
        final ConfigurationInfo info = am.getDeviceConfigurationInfo();

        if (info != null) {
            GLog.d("req es Version:" + info.reqGlEsVersion + " get es version:" + info.getGlEsVersion());
        } else {
            GLog.e("info is null ======>>>>>");
        }

        return info != null && info.reqGlEsVersion >= 0x20000;
    }

    @Override
    protected void onResume() {
        super.onResume();
        mGlSurfaceView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mGlSurfaceView.onPause();
    }
}
