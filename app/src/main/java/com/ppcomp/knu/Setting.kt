package com.ppcomp.knu

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_setting.*
import kotlinx.android.synthetic.main.main_toolbar.*

class Setting : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)

        subscriptionSetting.setOnClickListener{
            val intent = Intent(this, SubscriptionActivity::class.java)
            startActivity(intent)
        }
        keywordSetting.setOnClickListener{
            val intent = Intent(this, KeywordActivity::class.java)
            startActivity(intent)
        }
//        move_license.setOnClickListener{
//            val intent = Intent(this, License::class.java)
//            startActivity(intent)
//        }


        setSupportActionBar(main_layout_toolbar)                                //toolbar 지정
        supportActionBar?.setDisplayHomeAsUpEnabled(true)                       //toolbar  보이게 하기
        supportActionBar?.setHomeAsUpIndicator(R.drawable.move_back)            //뒤로가기 아이콘 지정
        supportActionBar?.setDisplayShowTitleEnabled(false)                     //타이틀 안보이게 하기
    }
}
