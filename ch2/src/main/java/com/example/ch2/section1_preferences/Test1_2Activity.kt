package com.example.ch2.section1_preferences

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.datastore.preferences.preferencesDataStoreFile
import androidx.lifecycle.lifecycleScope
import com.example.ch2.R
import com.example.ch2.databinding.ActivityTest12Binding
import kotlinx.coroutines.launch

class Test1_2Activity : AppCompatActivity() {
    // 코드에서 필요한 순간 직접 생성
//    val dataStore: DataStore<Preferences> = PreferenceDataStoreFactory.create(
//        produceFile = { preferencesDataStoreFile("my_prefs") }
//    )

    // by 위임으로 생성하는 것이 일반적 (by 에 의해 생성하면 singleton 보장
    // 클래스 멤버 변수로 선언되어야 함 -> 내부적으로 getter/setter 함수 만들어져야
    val dataStore: DataStore<Preferences> by preferencesDataStore(name = "my_prefs")

    // 키 정의 직접 문자열로 키를 지정하는 것을 허용하지 않음
    val USER_NAME = stringPreferencesKey("user_name")
    val AGE = intPreferencesKey("age")

    // 데이터를 저장하기 위한 개발자 함수, 이 함스에서 dataStore 이용하여 데이터 저장
    // 이 함수는 코루틴에 의해 실행 되어야 함 (suspend 로 선언)
    private suspend fun save() {
        dataStore.edit { preferences ->
            preferences[USER_NAME] = "kim"
            preferences[AGE] = 10
        }
    }

    private suspend fun get() {
        // collect: 코르틴에서  데이터를 획득한 후, flow 로 데이터를 발행
        dataStore.data.collect {
            val userName = it[USER_NAME] ?: ""
            val age = it[AGE] ?: 0
            Log.d("kkang", "$userName, $age")
        }
    }
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

        binding.saveButton.setOnClickListener {
            // 코루틴을 구동시켜서 코루틴에 의해 save() 함수 호출되도록
            // 코루틴은 스코프가 있어야하고 그 스코프 내에서만 동작
            lifecycleScope.launch {
                // lifecycleScope 내에서 하나의 코루틴(비동기 흐름) 이 구동, 액티비티 생명주기와 같이 작동
                save()
            }
        }

        binding.getButton.setOnClickListener {
            lifecycleScope.launch {
                get()
            }
        }
    }
}