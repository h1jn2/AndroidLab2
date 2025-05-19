package com.example.ch7.section3_googlemap

import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.ch7.R
import com.example.ch7.databinding.ActivityTest31Binding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class Test3_1Activity : AppCompatActivity(), OnMapReadyCallback {
    var googleMap: GoogleMap? = null

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

        val mapFragment = supportFragmentManager.findFragmentById(binding.main.id) as SupportMapFragment
        mapFragment.getMapAsync(this)   // callback 등록

    }

    // 지도 이용 가능 상황에서 호출되는 함수
    override fun onMapReady(p0: GoogleMap?) {
        googleMap = p0
        moveMap(37.519, 127.123)
    }

    private fun moveMap(latitude: Double, longitude: Double) {
        // 지도에서의 위치 LatLng 객체로
        val latLng = LatLng(latitude, longitude)
        // 지도 위치 옵션
        val position: CameraPosition = CameraPosition.Builder()
            .target(latLng) // 지도 위치
            .zoom(16f)      // 초기 zoom level
            .build()

        // 지도 center 이동
        googleMap!!.moveCamera(CameraUpdateFactory.newCameraPosition(position))

        // 마커 (이미지) 올리기
        val markerOption = MarkerOptions()
        markerOption.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
        markerOption.position(latLng)
        markerOption.title("MyLocation")
        googleMap?.addMarker(markerOption)
    }
}

// AndroidManifest.xml
// permission, Map key, play service version

// 개발자 컴퓨터에서 SHA1 지문 획득 후 앱을 빌드할 때 사용했던 키를 이용해야 함
// 정식 배포용 앱이라면 앱 빌드 시에 정식으로 만든 키 이용
// 테스트 용은 디버그 키 이용