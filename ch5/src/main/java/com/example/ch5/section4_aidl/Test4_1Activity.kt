package com.example.ch5.section4_aidl

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.os.Message
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.ch5.R
import com.example.ch5.databinding.ActivityTest41Binding
import com.example.ch5_outer.MyAIDLInterface
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class Test4_1Activity : AppCompatActivity() {
    lateinit var binding: ActivityTest41Binding
    var connectionMode = "none"

    var aidlService: MyAIDLInterface? = null
    var aidlJob: Job? = null

    fun changeViewEnable() = when (connectionMode) {
        "aidl" -> {
            binding.playButton.isEnabled = false
            binding.stopButton.isEnabled = true
        } else -> {
            binding.playButton.isEnabled = true
            binding.stopButton.isEnabled = false
            binding.progressView.progress = 0
        }
    }

    val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            // 서비스로부터 넘어온 객체를 받음 -> 프로세스 간의 통신을 대행하는 Stub 임
            // aidl 을 구현했으므로 실제 객체로 인지해서 함수 호출
            aidlService = MyAIDLInterface.Stub.asInterface(service)
            aidlService?.start()
            binding.progressView.max = aidlService?.duration ?: 0

            // 코루틴 구동시켜서 progressbar 증가
            val scope = CoroutineScope(Dispatchers.Default + Job())
            aidlJob = scope.launch {
                while (binding.progressView.progress < binding.progressView.max) {
                    delay(1000)
                    binding.progressView.incrementProgressBy(1000)
                }
            }
            connectionMode = "aidl"
            changeViewEnable()
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            aidlService = null
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityTest41Binding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        binding.playButton.setOnClickListener {
            // intent 로 서비스 실행
            val intent = Intent("com.example.ch5_outer.ACTION_SERVICE_AIDL")
            // bindingService 로 외부앱 실행시키려면 꼭 package 명시
            // 코드에서 외부 앱 패키지 이용하면 manifest 에 패키지 공개한다고 명시
            intent.setPackage("com.example.ch5_outer")
            bindService(intent, connection, BIND_AUTO_CREATE)
        }

        binding.stopButton.setOnClickListener {
            aidlService?.stop()
            unbindService(connection)
            // 코루틴이 동작 중이라면 종료
            aidlJob?.cancel()
            connectionMode = "none"
            changeViewEnable()
        }
    }

    override fun onStop() {
        super.onStop()
        if (connectionMode == "aidl") {
            unbindService(connection)
        }

        connectionMode = "none"
        changeViewEnable()
    }
}