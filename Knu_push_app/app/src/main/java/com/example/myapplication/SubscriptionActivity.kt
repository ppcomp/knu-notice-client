package com.example.myapplication

import android.os.Bundle
import android.os.StrictMode
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_subscription.*
import kotlinx.android.synthetic.main.main_toolbar.*
import kotlinx.android.synthetic.main.subscription_item.view.*
import org.json.JSONArray
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class SubscriptionActivity : AppCompatActivity() {
    var subsList = arrayListOf<Subscription>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_subscription)

        //parsing 부분
        val subsAdapter = SubscriptionAdapter(this, subsList)
        subsResult.adapter = subsAdapter
        StrictMode.enableDefaults()
        val serverUrl = "http://15.165.178.103/notice/list/"
        try {
            val stream = URL(serverUrl).openConnection() as HttpURLConnection
            var read = BufferedReader(InputStreamReader(stream.inputStream, "UTF-8"))
            val response = read.readLine()
            val jArray = JSONArray(response)
            for (i in 0 until jArray.length()) {
                val obj = jArray.getJSONObject(i)
                val name = obj.getString("name")
                val line = Subscription(name, false)
                subsList.add(line)
            }
        } catch (e: Exception) {
            val line = Subscription(e.toString(), checked = false)
            subsList.add(line)
        }



        setSupportActionBar(main_layout_toolbar)//toolbar 지정
        supportActionBar?.setDisplayHomeAsUpEnabled(true)//toolbar  보이게 하기
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp)//메뉴 아이콘 지정
        supportActionBar?.setDisplayShowTitleEnabled(false) //타이틀 안보이게 하기

        var et = findViewById(R.id.subsSave) as TextView
        subsSave.setOnClickListener {

            for (i in 0 until subsAdapter.count) {
                var ch: Boolean = subsResult.getChildAt(i).subs_checkbox.isChecked()
                //체크 상태 확인
                if (ch == true) {
                    // 체크가 되었을때 저장
                } else {
                    // 체크가 안된것도 저장
                }

            }


        }
    }

}
