package com.example.ch4.section2_notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.Person
import androidx.core.app.RemoteInput
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.IconCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.ch4.R
import com.example.ch4.databinding.ActivityTest21Binding

class Test2_1Activity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val binding = ActivityTest21Binding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // 퍼미션 요청
        val permissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) {
            if (it) {
                noti()
            } else {
                Toast.makeText(this, "permission denied", Toast.LENGTH_SHORT).show()
            }
        }
        binding.button.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (ContextCompat.checkSelfPermission(
                        this,
                        "android.permission.POST_NOTIFICATIONS"
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    noti()
                } else {
                    permissionLauncher.launch("android.permission.POST_NOTIFICATIONS")
                }
            } else {
                noti()
            }
        }
    }

    fun noti() {
        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val builder: NotificationCompat.Builder
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "oneChannel",
                "My Channel",
                NotificationManager.IMPORTANCE_HIGH
            )

            // 만들어진 채널을 시스템에 등록
            manager.createNotificationChannel(channel)
            // 등록된 채널을 이용해 Builder 생성
            builder = NotificationCompat.Builder(this, "oneChannel")
        } else {
            // Channel 개념이 없음
            builder = NotificationCompat.Builder(this)
        }

        builder.setSmallIcon(android.R.drawable.ic_notification_overlay)
        builder.setWhen(System.currentTimeMillis())
        builder.setContentTitle("메시지 도착")
        builder.setContentText("안녕하세요")

        // 확장 터치 이벤트
        val intent = Intent(this, DetailActivity::class.java)
        val pIntent = PendingIntent.getActivity(
            this, 10, intent,
            PendingIntent.FLAG_IMMUTABLE
        )
        builder.setContentIntent(pIntent)

        // action 추가 최대 3개
        val actionIntent = Intent(this, MyNotificationReceiver::class.java)
        val actionPendingIntent = PendingIntent.getBroadcast(
            this, 20, actionIntent,
            PendingIntent.FLAG_IMMUTABLE
        )
        builder.addAction(
            NotificationCompat.Action.Builder(
                android.R.drawable.stat_notify_chat,
                "Action",
                actionPendingIntent
            ).build()
        )

        // Remote Input
        val remoteInput = RemoteInput.Builder("key_reply").run {
            setLabel("답장")
            build()
        }

        // 준비된 RemoteInput 을 Action 으로 추가
        val remoteIntent = Intent(this, MyRemoteInputReceiver::class.java)
        val remotePendintIntent = PendingIntent.getBroadcast(
            this, 30,
            remoteIntent, PendingIntent.FLAG_MUTABLE
        )

        builder.addAction(
            NotificationCompat.Action.Builder(
                R.drawable.send, "답장", remotePendintIntent
            ).addRemoteInput(remoteInput).build()
        )

        // big picture 스타일
//        val bigPicture = BitmapFactory.decodeResource(resources, R.drawable.big)
//        val bigStyle = NotificationCompat.BigPictureStyle()
//        bigStyle.bigPicture(bigPicture)
//        builder.setStyle(bigStyle)

        // big text 스타일
//        val bigTextStyle = NotificationCompat.BigTextStyle()
//        bigTextStyle.bigText(resources.getString(R.string.big_txt))
//        builder.setStyle(bigTextStyle)

        // message 스타일
        // 메시지를 보낸 사람을 Person 으로 준비
        val person1: Person = Person.Builder()
            .setName("홍길동")
            .setIcon(IconCompat.createWithResource(this, R.drawable.person1))
            .build()

        val person2: Person = Person.Builder()
            .setName("김길동")
            .setIcon(IconCompat.createWithResource(this, R.drawable.person2))
            .build()

        val message1 = NotificationCompat.MessagingStyle.Message(
            "hello",
            System.currentTimeMillis(),
            person1
        )

        val message2 = NotificationCompat.MessagingStyle.Message(
            "world",
            System.currentTimeMillis(),
            person2
        )

        val messageStyle = NotificationCompat.MessagingStyle(person1)
            .addMessage(message1)
            .addMessage(message2)

        builder.setStyle(messageStyle)

        manager.notify(11, builder.build())
    }
}