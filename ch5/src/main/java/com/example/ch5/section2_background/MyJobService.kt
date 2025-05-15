package com.example.ch5.section2_background

import android.app.Service
import android.app.job.JobParameters
import android.app.job.JobService
import android.content.Intent
import android.os.IBinder
import android.util.Log

class MyJobService : JobService() {

    override fun onStartJob(params: JobParameters?): Boolean {
        Log.d("kkang", "onStartJob")
        return false    // 완벽히 업무가 종료 되어 onDestroy() 바로 호출
        // true: 함수는 종료 되었지만 업무는 아직 업무가 진행 중이니 기다리도록
    }

    override fun onStopJob(params: JobParameters?): Boolean {
        Log.d("kkang", "onStopJob")
        return true     // 다시 등록
    }
}