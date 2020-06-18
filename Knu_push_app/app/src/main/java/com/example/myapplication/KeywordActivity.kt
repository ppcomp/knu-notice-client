package com.example.myapplication

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_keyword.*

class KeywordActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_keyword)


        keywordEnrollment.setOnClickListener {
            val getValue = keywordInput.text.toString()

            val pref = getSharedPreferences("pref", Context.MODE_PRIVATE)
            val ed = pref.edit()

            val getKeyword = pref.getString("Keys", "")
            val set: MutableSet<String> = mutableSetOf("")
            val keywordList = pref.getString("Keys", "오류")?.split("+")
            if (keywordList != null) {
                for (i in 0 until keywordList.count()) {
                    set.add(keywordList[i])
                }
            }

            if (getKeyword.equals("")) {
                ed.putString("Keys", getValue)
                ed.apply()
            } else {
                val keyOverlap: Boolean = set.contains(getValue)// 저장된 게시판인지 확인하기위한 변수
                if (!keyOverlap) {
                    val storeKeyword = getKeyword + "+" + getValue
                    ed.putString("Keys", storeKeyword)
                    ed.apply()
                    Toast.makeText(this, "저장되었습니다.", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "이미 존재합니다.", Toast.LENGTH_SHORT).show()
                }
            }

            showKeyword.text = pref.getString("Keys", "저장 x")


        }


    }
}
