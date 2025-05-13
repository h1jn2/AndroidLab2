package com.example.ch3.section3_anr

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.ch3.R
import com.example.ch3.databinding.ActivityTest31Binding

class Test3_1Activity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val binding = ActivityTest31Binding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.toastButton.setOnClickListener {
            Toast.makeText(this, "show toast", Toast.LENGTH_SHORT).show()
        }

        binding.sumButton.setOnClickListener {
            // anr 문제가 발생하는 코드
//            var sum = 0L
//            for (i in 1..20_000_000_000) {
//                sum += i
//            }
//            Toast.makeText(this, "$sum", Toast.LENGTH_SHORT).show()

            // anr 문제를 해결하기 위해 시간이 오래 걸리는 작업을 thread 로 진행
//            val obj = object : Runnable {
//                override fun run() {
//                    var sum = 0L
//                    for (i in 1..20_000_000_000) {
//                        sum += i
//                    }
//                    Toast.makeText(this@Test3_1Activity, "$sum", Toast.LENGTH_SHORT).show()
//                }
//            }
//            val thread = Thread(obj)
//            thread.start()

            // thread handler 구조
            val handler = object : Handler(Looper.getMainLooper()) {
                // 아래의 함수는 main thread 에 의해 호출
                // thread 가 sendMessage() 하는 순간
                // 매개변수는 thread 에서 전달한 데이터
                override fun handleMessage(msg: Message) {
                    if (msg.what == 1) {
                        Toast.makeText(
                            this@Test3_1Activity,
                            "${msg.obj as Long}",
                            Toast.LENGTH_SHORT
                        ).show()

                    }
                }
            }

            val obj = object: Runnable {
                override fun run() {
                    var sum = 0L
                    for (i in 1..5_000_000_000) {
                        sum += i
                    }
                    // 데이터 발생 -> 개발자 스레드에서 화면 변경 못 하기 때문에 main thread 에 의뢰
                    val message = Message()
                    message.what = 1        // 요청의 구분자
                    message.obj = sum       // 넘기는 데이터
                    handler.sendMessage(message)    // 요청 순간 main thread 동작
                }
            }
            val thread = Thread(obj)
            thread.start()
        }
    }
}