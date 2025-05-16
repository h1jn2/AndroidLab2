package com.example.ch5_outer

import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.os.Message
import android.os.Messenger
import android.util.Log

// 원천 업무를 제공하는 앱의 서비스
// 외부 앱이 bindService() 로 이용하는 서비스
class MyMessengerService : Service() {
    // 이 앱에서 만든 메신저, 외부 앱이 이 메신저를 이용해 데이터 전달
    lateinit var messenger: Messenger
    // 외부 앱이 만든 메신저, 이 메신저르 외부앱에 데이터 전달
    lateinit var replyMessenger: Messenger
    // 음원 및 영상 play
    lateinit var player: MediaPlayer

    override fun onCreate() {
        super.onCreate()
        player = MediaPlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        player.release()    // 해제
    }

    // 외부에서 데이터를 전달할 때 실행될 함수
    // 어디선가 메시지를 전달하면 메시지 큐에 쌓이게 되고 looper(감시자)가 큐를 감지하고 있다가
    // 큐에서 메시지를 뽑아 실행시켜 줌
    inner class IncomingHandler(context: Context): Handler(Looper.getMainLooper()) {
        // 외부에서 send 하는 순간 실행 매개변수는 외부에서 전달한 데이터
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                10 -> {
                    // 음원 play 요청이라고 가정
                    // 외부에서 전달한 데이터 중에 결과 데이터를 받을 messenger 까지도 넘어옴
                    replyMessenger = msg.replyTo
                    if (!player.isPlaying) {
                        // 현재 음악이 play 되지 않고 있다면
                        player = MediaPlayer.create(this@MyMessengerService, R.raw.music)
                        try {
                            val replyMsg = Message()
                            replyMsg.what = 10
                            val replyBundle = Bundle()
                            replyBundle.putInt("duration", player.duration)
                            replyMsg.obj = replyBundle
                            replyMessenger.send(replyMsg)   // 외부에 데이터 전달한 순간

                            // 음원 플레이
                            player.start()
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
                20 -> {
                    // stop 요청
                    if (player.isPlaying) {
                        player.stop()
                    }
                }
            }
        }
    }

    override fun onBind(intent: Intent): IBinder {
        messenger = Messenger(IncomingHandler(this))
        return messenger.binder
    }
}