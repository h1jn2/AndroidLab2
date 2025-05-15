package com.example.ch5.section1_service

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log

class MyService : Service() {

    // bindService() 로 자신을 구동 시킨 곳에 전달할 객체
    // Binder 만 상속받아 작섷하고 클래스 내부의 작성 규칙은 없음
    class MyBinder: Binder() {
        fun funA(arg: Int): Int = arg * arg

    }

    override fun onBind(intent: Intent): IBinder {
        Log.d("kkang", "MyService onBind")
        return MyBinder()
    }

    override fun onCreate() {
        super.onCreate()
        Log.d("kkang", "MyService onCreate")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("kkang", "MyService onDestroy")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("kkang", "MyService onStartCommand")
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onUnbind(intent: Intent?): Boolean {
        Log.d("kkang", "MyService onUnBind")
        return super.onUnbind(intent)
    }
}