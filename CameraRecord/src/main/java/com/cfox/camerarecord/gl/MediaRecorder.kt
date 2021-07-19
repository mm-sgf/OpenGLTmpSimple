package com.cfox.camerarecord.gl

import android.media.MediaCodec
import android.media.MediaCodecInfo
import android.media.MediaFormat
import android.media.MediaMuxer
import android.view.Surface
import com.cfox.camera.log.EsLog
import java.nio.ByteBuffer

class MediaRecorder(path: String, width: Int, height: Int) {

    private val mediaCodec = MediaCodec.createEncoderByType(MediaFormat.MIMETYPE_VIDEO_AVC)
    private val muxer = MediaMuxer(path, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4)
    private val surface: Surface

    private var started = false

    init {
        val format = MediaFormat.createVideoFormat(MediaFormat.MIMETYPE_VIDEO_AVC, width, height)
        format.setInteger(
            MediaFormat.KEY_COLOR_FORMAT,
            MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface
        )
        format.setInteger(MediaFormat.KEY_BIT_RATE, (width * height * 2.5).toInt())
        format.setInteger(MediaFormat.KEY_FRAME_RATE, 30)
        format.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 10)
        mediaCodec.configure(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE)
        surface = mediaCodec.createInputSurface()
        mediaCodec.start()
    }

    fun getSurface(): Surface {
        return surface
    }

    fun start() {
        if (started) return

        started = true


        var videoTrack = 0
        var lastTimeStamp = 0L
        var frameCount = 0
        val offsetTime = 1000_000 / 30L

        var firstTime = -1L


        Thread {
            while (started) {
                val bufferInfo = MediaCodec.BufferInfo()
                val outIndex = mediaCodec.dequeueOutputBuffer(bufferInfo, 10_000)
                if (outIndex == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                    val videoFormat = mediaCodec.outputFormat
                    videoTrack = muxer.addTrack(videoFormat)
                    muxer.start()
                } else if (outIndex == MediaCodec.INFO_TRY_AGAIN_LATER
                    || outIndex == MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED
                ) {

                } else {
                    //调整时间戳
                    //有时候会出现异常 ： timestampUs xxx < lastTimestampUs yyy for Video track
                    //有时候会出现异常 ： timestampUs xxx < lastTimestampUs yyy for Video track
//                    if (bufferInfo.presentationTimeUs <= lastTimeStamp) {
//                        bufferInfo.presentationTimeUs = lastTimeStamp + 1000000 / 25
//                    }
//                    lastTimeStamp = bufferInfo.presentationTimeUs


                    //正常则 index 获得缓冲区下标
                        if (firstTime <= 0) {
                            firstTime = bufferInfo.presentationTimeUs
                        }

                    if (bufferInfo.flags != MediaCodec.BUFFER_FLAG_CODEC_CONFIG) {
                        //正常则 index 获得缓冲区下标
                        bufferInfo.presentationTimeUs = frameCount * offsetTime
                        frameCount++

                        EsLog.d("first time $firstTime  \n pts :${bufferInfo.presentationTimeUs}  flag:${bufferInfo.flags}")
                    }

                    val encodedData: ByteBuffer = mediaCodec.getOutputBuffer(outIndex)!!
                    //如果当前的buffer是配置信息，不管它 不用写出去
                    //如果当前的buffer是配置信息，不管它 不用写出去
//                    if (bufferInfo.flags and MediaCodec.BUFFER_FLAG_CODEC_CONFIG != 0) {
//                        bufferInfo.size = 0
//                    }
                    if (bufferInfo.size != 0) {
                        //设置从哪里开始读数据(读出来就是编码后的数据)
                        encodedData.position(bufferInfo.offset)
                        //设置能读数据的总长度
                        encodedData.limit(bufferInfo.offset + bufferInfo.size)
                        //写出为mp4
                        muxer.writeSampleData(videoTrack, encodedData, bufferInfo)
                    }
                    // 释放这个缓冲区，后续可以存放新的编码后的数据啦
                    // 释放这个缓冲区，后续可以存放新的编码后的数据啦
                    mediaCodec.releaseOutputBuffer(outIndex, false)
                }

            }

            if (!started) {
                mediaCodec.release()
                muxer.release()
            }
        }.start()
    }


    fun stop() {
        started = false
    }
}