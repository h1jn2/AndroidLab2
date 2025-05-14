package com.example.ch4.section2_notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class MyNotificationReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        Log.d("kkang", "NotificationReceiver")
    }
}