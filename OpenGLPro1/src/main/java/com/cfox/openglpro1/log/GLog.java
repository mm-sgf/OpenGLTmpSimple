package com.cfox.openglpro1.log;

import android.util.Log;

public class GLog {

    private static String sLogTag = "GL-Log";

    public static void d(String message) {
        Log.d(getClassName(), buildLog(message));
    }

    public static void e(String message) {
        Log.e(getClassName(), buildLog(message));
    }

    public static void i(String message) {
        Log.i(getClassName(), buildLog(message));
    }

    private static String buildLog(String message) {
        String logStr;
        Thread thread = Thread.currentThread();
        if (sLogTag == null) {
            logStr = "[" + thread.getName() + "]" + message;
        } else {
            logStr = "[" + sLogTag + "]"+ "[" + thread.getName() + "]" + message;
        }

        return logStr;
    }

    private static String getClassName() {
        String classname = new Exception().getStackTrace()[1].getFileName();
        classname = classname.substring(0, classname.indexOf("."));
        return "[" + classname + "]";
    }
}
