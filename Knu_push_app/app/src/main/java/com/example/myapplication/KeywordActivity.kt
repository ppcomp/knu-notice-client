package com.example.myapplication

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_keyword.*
import kotlinx.android.synthetic.main.keyword_toolbar.*

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
        setSupportActionBar(keyword_layout_toolbar)                                //toolbar 지정
        supportActionBar?.setDisplayHomeAsUpEnabled(true)                       //toolbar  보이게 하기
        supportActionBar?.setHomeAsUpIndicator(R.drawable.move_back)            //뒤로가기 아이콘 지정
        supportActionBar?.setDisplayShowTitleEnabled(false)                     //타이틀 안보이게 하기

    }
    /**
     * 화면 터치시 키보드 숨김
     * @author 희진
     */
    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if(currentFocus != null){
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(currentFocus!!.windowToken,0)
        }

        return super.dispatchTouchEvent(ev)
    }
}
