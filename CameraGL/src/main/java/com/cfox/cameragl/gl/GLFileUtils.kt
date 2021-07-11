package com.cfox.cameragl.gl

import android.content.Context
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

object GLFileUtils {

    fun readRawTextFile(context: Context, rawId: Int): String {
        val input = context.resources.openRawResource(rawId)
        val br = BufferedReader(InputStreamReader(input))
        var line: String?
        val sb = StringBuilder()
        try {
            while (br.readLine().also { line = it } != null) {
                sb.append(line)
                sb.append("\n")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        try {
            br.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return sb.toString()
    }
}