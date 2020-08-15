package com.ppcomp.knu.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.ppcomp.knu.R
import kotlinx.android.synthetic.main.activity_license_toolbar.*
import kotlinx.android.synthetic.main.activity_subscription_toolbar.*

class LicenseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_license)

        setSupportActionBar(license_layout_toolbar)//toolbar 지정
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.move_back_ic)//뒤로가기 아이콘 지정
        supportActionBar?.setDisplayShowTitleEnabled(false) //타이틀 안보이게 하기

    }
}
