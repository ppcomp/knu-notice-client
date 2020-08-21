package com.ppcomp.knu.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View.GONE
import android.widget.ImageView
import android.widget.TextView
import com.ppcomp.knu.R
import kotlinx.android.synthetic.main.activity_main_toolbar.*

class LicenseActivity : AppCompatActivity() {

    private lateinit var searchIcon: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_license)

        val title = findViewById<TextView>(R.id.state_title)
        title.text = "라이선스"

        searchIcon = findViewById<ImageView>(R.id.search_icon)
        searchIcon.visibility = GONE

        setSupportActionBar(main_layout_toolbar)//toolbar 지정
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.move_back_ic)//뒤로가기 아이콘 지정
        supportActionBar?.setDisplayShowTitleEnabled(false) //타이틀 안보이게 하기
    }
}
