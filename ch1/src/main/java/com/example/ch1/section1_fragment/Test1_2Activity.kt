package com.example.ch1.section1_fragment

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.example.ch1.OneFragment
import com.example.ch1.R
import com.example.ch1.TwoFragment
import com.example.ch1.databinding.ActivityTest12Binding

class Test1_2Activity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val binding = ActivityTest12Binding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // transaction 으로 화면에 출력할 fragment 를 제어하는 것은 맞지만
        // 한번 transaction 을 commit 시키면 transaction 은 cloose 되어 다시 사용 불가
        // 다시 transaction 을 얻어서 제어 해야 함
        val fragmentManager: FragmentManager = supportFragmentManager
        val transaction: FragmentTransaction = fragmentManager.beginTransaction()
        val fragment = OneFragment()
        transaction.add(R.id.fragmentContainer, fragment)
        transaction.commit()    // 이 순간 화면에 출력

        binding.fragmentButton.setOnClickListener {
//            val fragment = TwoFragment()
//            transaction.replace(R.id.fragmentContainer, fragment)
//            transaction.commit()

            // back 버튼 클릭 시 액티비티 종료 (이전 화면 출력 X)
            // stack(history) 가 유지되지 않기 때문
//            val transaction2 = fragmentManager.beginTransaction()
//            val fragment = TwoFragment()
//            transaction2.replace(R.id.fragmentContainer, fragment)
//            transaction2.commit()

            // null 대신 임의 문자열을 지정하여 스택 정보의 식별자로 이용할 수 있음
            // 일반적으로 null 지정 시 이전 fragment 로 되돌아 감
            val transaction2 = fragmentManager.beginTransaction()
            val fragment = TwoFragment()
            transaction2.replace(R.id.fragmentContainer, fragment)
            transaction2.addToBackStack(null)
            transaction2.commit()
        }
    }
}