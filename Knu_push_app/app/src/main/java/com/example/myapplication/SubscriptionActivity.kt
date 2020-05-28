package com.example.myapplication

import android.content.Context
import android.os.Bundle
import android.os.StrictMode
import android.widget.TextView
import android.widget.Toast
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
                val line = Subscription(name)
                subsList.add(line)
            }
        } catch (e: Exception) {
            val line = Subscription("오류")
            subsList.add(line)
        }



        setSupportActionBar(main_layout_toolbar)//toolbar 지정
        supportActionBar?.setDisplayHomeAsUpEnabled(true)//toolbar  보이게 하기
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp)//메뉴 아이콘 지정
        supportActionBar?.setDisplayShowTitleEnabled(false) //타이틀 안보이게 하기

        var et = findViewById(R.id.subsSave) as TextView
        var correct = findViewById(R.id.correct) as TextView
        subsSave.setOnClickListener {

            val pref = getSharedPreferences("pref", Context.MODE_PRIVATE)
            val ed = pref.edit()
            var store : String = ""


            for (i in 0 until subsAdapter.count) {
                var ch: Boolean = subsResult.getChildAt(i).subs_checkbox.isChecked()
                var name : String
                //체크 상태 불러오기
                if (ch == true) { // 체크 상태 확인
                    name = subsAdapter.getName(i)
                    store = store + name + "+"
                    // 체크가 되었을때 저장
                } else {
                    // 체크가 안된것도 저장
                }
            }
            if(store.equals(""))
            {
                ed.remove("Subs")
                ed.apply()
            }
            else
            {
                store = store.substring(0, store.length -1)
                ed.putString("Subs", store)
                ed.apply()
            }


        }

        correct.setOnClickListener {
            val pref2 = getSharedPreferences("pref", Context.MODE_PRIVATE)
            correct.setText(pref2.getString("Subs", "오류"))


        }
    }

}
