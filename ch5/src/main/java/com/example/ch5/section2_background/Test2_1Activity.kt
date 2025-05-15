package com.example.ch5.section2_background

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.ch5.R
import com.example.ch5.databinding.ActivityTest21Binding

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

        val permissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) {
            if (it) {
                Toast.makeText(this, "permission denied", Toast.LENGTH_SHORT).show()

            } else {
                Toast.makeText(this, "permission denied", Toast.LENGTH_SHORT).show()
            }
        }

        binding.backgroundButton.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (ContextCompat.checkSelfPermission(
                        this,
                        "android.permission.POST_NOTIFICATIONS"
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    val serviceIntent = Intent(this, MyService2::class.java)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        startForegroundService(serviceIntent)
                    } else {
                        startService(serviceIntent)
                    }
                } else {
                    permissionLauncher.launch("android.permission.POST_NOTIFICATIONS")
                }
            } else {
                // 서비스를 foreground service 로 구동
                // 서비스가 구동은 되지만 얼마 후 에러 발생
                val serviceIntent = Intent(this, MyService2::class.java)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    startForegroundService(serviceIntent)
                } else {
                    startService(serviceIntent)
                }
            }


        }

        binding.jobButton.setOnClickListener {

        }
    }
}