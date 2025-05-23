package com.example.ch3.section3_anr

import android.os.AsyncTask
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.launch

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
//            val handler = object : Handler(Looper.getMainLooper()) {
//                // 아래의 함수는 main thread 에 의해 호출
//                // thread 가 sendMessage() 하는 순간
//                // 매개변수는 thread 에서 전달한 데이터
//                override fun handleMessage(msg: Message) {
//                    if (msg.what == 1) {
//                        Toast.makeText(
//                            this@Test3_1Activity,
//                            "${msg.obj as Long}",
//                            Toast.LENGTH_SHORT
//                        ).show()
//
//                    }
//                }
//            }
//
//            val obj = object: Runnable {
//                override fun run() {
//                    var sum = 0L
//                    for (i in 1..5_000_000_000) {
//                        sum += i
//                    }
//                    // 데이터 발생 -> 개발자 스레드에서 화면 변경 못 하기 때문에 main thread 에 의뢰
//                    val message = Message()
//                    message.what = 1        // 요청의 구분자
//                    message.obj = sum       // 넘기는 데이터
//                    handler.sendMessage(message)    // 요청 순간 main thread 동작
//                }
//            }
//            val thread = Thread(obj)
//            thread.start()

            // 방법2 - AsyncTask
            // Thread, Handler 에 의해 처리되어야 할 내용을 AsyncTask 상속 클래스에 담아서 처리
            // 제네릭 타입:
            // 그라운드 작업에 의해 전달될 데이터 타입
            // 스레드에 의해 반복적으로 발생되는 데이터 타입
            // 스레드의 최종 결과 데이터 타입
//            class MyAsyncTask: AsyncTask<Void, Long, Long>() {
//                // 시간이 오래 걸리는 업무를 담는 함수, 내부적으로 스레드에 의해 실행
//                override fun doInBackground(vararg params: Void?): Long {
//                    var sum = 0L
//                    for (i in 1..1_000_000_000) {
//                        sum += 1
//                    }
//                    return sum  // 이 값이 onPostExecute() 의 매개변수로 전달되는 스레드의 최종 값
//                }
//
//                override fun onPostExecute(result: Long?) {
//                    // main thread 에 의해 실행
//                    Toast.makeText(this@Test3_1Activity, "$result", Toast.LENGTH_SHORT).show()
//                }
//            }
//            MyAsyncTask().execute()

            // 방법3 - 코루틴
            // Main Thread 에 의해 실행될 코루틴과 백그라운드 업무를 담당하기 위한 코루틴이 같이 동작
            // 두 코루틴 간 데이터 전달을 위해
            val channel = Channel<Int> ()

            // 백그라운드 업무를 담당하는 코루틴이 실행될 스코프
            // 코루틴은 항상 스코프 내에서 실행
            // Dispatchers.Default - 극한의 CPU 작업이 필요한 코루틴을 돌리는 스레드
            val scope = CoroutineScope(Dispatchers.Default + Job())
            scope.launch {
                // 코루틴 구동
                var sum = 0L
                for (i in 1..1_000_000_000) {
                    sum += i
                }
                channel.send(sum.toInt())
            }

            GlobalScope.launch(Dispatchers.Main) {
                // MainThread 에 의해 실행되는 코루틴 구동
                channel.consumeEach {
                    // channel 에 의해 데이터가 발행된다몀
                    Toast.makeText(this@Test3_1Activity, "$it", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}